# Alizer

![Go](https://img.shields.io/badge/Go-1.19-blue)
![Build status](https://github.com/redhat-developer/alizer/actions/workflows/CI.yml/badge.svg)
[![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)](./LICENSE)

Alizer (which stands for Application Analyzer) is a utilily whose goal is to extract informations about an application source code.
Such informations are:

- Programming languages.
- Frameworks.
- Tools used to build the application.

Additionaly, Alizer can also select one devfile (cloud workspace file) from a list of available devfiles and/or
detect components (the concept of component is taken from Odo and its definition can be read on [odo.dev](https://odo.dev/docs/getting-started/basics/#component)).

## Usage

### CLI

The Go CLI can be built with the below command:

```bash
# inside the go/ dir
$ go build alizer.go
```

### CLI Arguments

#### alizer analyze

```shell
./alizer analyze [OPTION]... [PATH]...
```

```sh
  --log {debug|info|warning}    sets the logging level of the CLI. The arg accepts only 3 values [`debug`, `info`, `warning`]. The default value is `warning` and the logging level is `ErrorLevel`.
```

#### alizer component

```shell
./alizer component [OPTION]... [PATH]...
```

```sh
  --port-detection {docker|compose|source}    port detection strategy to use when detecting a port. Currently supported strategies are 'docker', 'compose' and 'source'. You can pass more strategies at the same time. They will be executed in order. By default Alizer will execute docker, compose and source.
```

#### alizer devfile

```shell
./alizer devfile [OPTION]... [PATH]...
```

```sh
  --registry strings    registry where to download the devfiles. Default value: https://registry.devfile.io
```

### Library Package

#### Language Detection

To analyze your source code with Alizer, just import it and use the recognizer:

```go
import "github.com/redhat-developer/alizer/pkg/apis/recognizer"

languages, err := recognizer.Analyze("your/project/path")
```

#### Component Detection

It detects all components which are found in the source tree where each component consists of:

- _Name_: name of the component
- _Path_: root of the component
- _Languages_: list of languages belonging to the component ordered by their relevance.
- _Ports_: list of ports used by the component

```go
import "github.com/redhat-developer/alizer/pkg/apis/recognizer"

components, err := recognizer.DetectComponents("your/project/path")
```

For more info about name detection, see the [name detection](docs/public/name_detection.md) doc.

For more info about port detection, see the [port detection](docs/public/port_detection.md) doc.

#### Devfile Detection

It selects a devfile from a list of devfiles (from a devfile registry or other storage) based on the information found in the source tree.

```go
import "github.com/redhat-developer/alizer/pkg/apis/recognizer"

devfile, err := recognizer.SelectDevFileFromTypes("your/project/path", devfiles)
```

## Outputs

Example of `analyze` command:

```json
[
  {
    "Name": "Go",
    "Aliases": ["golang"],
    "Weight": 94.72,
    "Frameworks": [],
    "Tools": ["1.18"],
    "CanBeComponent": true
  }
]
```

Example of `component` command:

```json
[
  {
    "Name": "spring4mvc-jpa",
    "Path": "path-of-the-component",
    "Languages": [
      {
        "Name": "Java",
        "Aliases": null,
        "Weight": 100,
        "Frameworks": ["Spring"],
        "Tools": ["Maven"],
        "CanBeComponent": true
      }
    ],
    "Ports": null
  }
]
```

Example of `devfile` command:

```json
[
  {
    "Name": "nodejs",
    "Language": "JavaScript",
    "ProjectType": "Node.js",
    "Tags": ["Node.js", "Express", "ubi8"]
  }
]
```

## Contributing

This is an open source project open to anyone. This project welcomes contributions and suggestions!

For information on getting started, refer to the [CONTRIBUTING instructions](CONTRIBUTING.md).

## Release process

The release process of `alizer` is very straightforward. You can create a new release [here](https://github.com/redhat-developer/alizer/releases/new).

- The _title_ of the release has to be the new version. `Alizer` follows the `v{major}.{minor}.{bugfix}` format (e.g `v0.1.0`)
- The _description_ of the release is optional. You may add a description if there were outstanding updates in the project, not mentioned in the issues or PRs of this release.

### Release Binaries
For each release a group of binary files is generated. More detailed we have the following types:
- `linux/amd64`
- `linux/ppc65le`
- `linux/s390x`
- `windows/amd64`
- `darwin/amd64`

In order to download a binary file:
* Go to the release you are interested for `https://github.com/thepetk/alizer/releases/tag/<release-tag>`
* In the **Assets** section you will see the list of generated binaries for the release.

## Feedback & Questions

If you discover an issue please file a bug and we will fix it as soon as possible.

- File a bug in [GitHub Issues](https://github.com/redhat-developer/alizer/issues).
