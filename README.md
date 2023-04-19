# Alizer

[release-svg]: https://img.shields.io/nexus/r/com.redhat.devtools.alizer/alizer?server=https%3A%2F%2Frepository.jboss.org%2Fnexus
[nightly-svg]: https://img.shields.io/nexus/s/com.redhat.devtools.alizer/alizer?server=https%3A%2F%2Frepository.jboss.org%2Fnexus

![Build status](https://github.com/redhat-developer/alizer/actions/workflows/CI.yml/badge.svg)
![Release][release-svg]
![Nightly][nightly-svg]

## Overview

Alizer (which stands for Application Analyzer) is a utilily whose goal is to extract informations about an application source code.
Such informations are:

- Programming languages.
- Frameworks.
- Tools used to build the application.

Additionaly, Alizer can also select one devfile (cloud workspace file) from a list of available devfiles and/or
detect components (the concept of component is taken from Odo and its definition can be read on [odo.dev](https://odo.dev/docs/getting-started/basics/#component)).

## Usage

- [Go library & CLI](go/README.md#Usage)

## Contributing

This is an open source project open to anyone. This project welcomes contributions and suggestions!

For information on getting started, refer to the [CONTRIBUTING instructions](CONTRIBUTING.md).

## Release process

The release process of `alizer` is very straightforward. You can create a new release [here](https://github.com/redhat-developer/alizer/releases/new).

- The _title_ of the release has to be the new version. `Alizer` follows the `{major}.{minor}.{bugfix}` format (e.g `0.1.0`)
- The _description_ of the release is optional. You may add a description if there were outstanding updates in the project, not mentioned in the issues or PRs of this release.

## Feedback & Questions

If you discover an issue please file a bug and we will fix it as soon as possible.

- File a bug in [GitHub Issues](https://github.com/redhat-developer/alizer/issues).

## License

EPL 2.0, See [LICENSE](LICENSE) for more information.
