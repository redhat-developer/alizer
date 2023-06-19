# Enhance port detection for dockerfiles that are not in a component.

## Related GitHub Issues
* https://github.com/redhat-developer/alizer/issues/235

## Background

What is a component?

Components are defined in the `languages-customization.yml` as a language that has specific `configuration_files`.

Example of a Go component:

```yaml
Go:
  exclude_folders:
    - "vendor"
  configuration_files:
    - "go.mod"
  component: true
```

What happens when the repo being scanned has component(s) found but also contains a dockerfile one level outside the repo?

```shell
.repo-being-detected-by-alizer
├── go-component/
│   ├── go.mod
│   └── dockerfile
└── dockerfile
```

The first component detected would be `go-component` because it contains a `go.mod` file.
```shell
└── go-component/
    ├── go.mod
    └── dockerfile
```

The dockerfile in the root level would not be used during analysis because it does not have a component definition in the `languages-customization.yml`.

## Potential Solution

Introduce a new component field called `orphan_component` and add it to the `Dockerfile` language:

```yaml
Dockerfile:
  configuration_files:
    - "[Dd]ockerfile"
    - "[Cc]ontainerfile"
  orphan_component: true
```

Introduce a new component enricher for the `Dockerfile` language that reuses the existing strategy for finding ports in a Dockerfile. There are currently three strategies for port detection, but only the DockerFile strategy is required. [1]

```go
func (d DockerEnricher) DoEnrichComponent(component *model.Component, settings model.DetectionSettings, ctx *context.Context) {
    // get ports from the dockerfile using our existing dockerfile strategy
    for _, algorithm := range settings.PortDetectionStrategy {
      switch algorithm {
        case model.DockerFile:
        {
          ports = GetPortsFromDockerFile(component.Path)
          break
        }
      }
    }
```

Using the `orphan_component` field, we can separate out a dockerfile as a component from what we currently define as a component. This can also be used for any future components that are similar to a dockerfile.

As a result, the example from above will return two components:

* A Go component:
    ```shell
    └── go-component/  # detected as a Go component
        ├── go.mod
        └── dockerfile
    ```
* And the newly defined Dockerfile component:
    ```shell
    .repo-being-detected-by-alizer
    └── dockerfile    # will be a component with path `repo-being-detected-by-alizer`
    ```

This can also be applied to common locations for Dockerfiles where it is inside a directory:
```shell
.repo-being-detected-by-alizer
├── docker/
│   └── dockerfile    # will be a component with path `docker/dockerfile`
├── .docker/
│   └── Containerfile # will be a component with path `.docker/Containerfile`
└── build/
    └── containerfile # will be a component with path `build/containerfile`
```

## Potential Problems

* Weak documentation as to what the fields in the `language-customization.yml` currently do.
  * Action: Add descriptions of the fields similar to `languages.yml`
* Could have potential naming collision with field names if the `language.yml` file is updated.
  * Action: Could add a workflow test that checks the fields between `language-customization.yml` and `languages.yml`
  * Action: Add as a PR review check item
  * Action: Add a comment about potential naming collisions in the `language-customization.yml` file

## References

[1] More information about port detection [here](../public/port_detection.md)
