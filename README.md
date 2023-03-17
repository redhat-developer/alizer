# Alizer
[release-svg]: https://img.shields.io/nexus/r/com.redhat.devtools.alizer/alizer?server=https%3A%2F%2Frepository.jboss.org%2Fnexus
[nightly-svg]: https://img.shields.io/nexus/s/com.redhat.devtools.alizer/alizer?server=https%3A%2F%2Frepository.jboss.org%2Fnexus
![Build status](https://github.com/redhat-developer/alizer/actions/workflows/CI.yml/badge.svg)
![Release][release-svg]
![Nightly][nightly-svg]

## Overview
Alizer (which stands for Application Analyzer) is a utilily whose goal is to extract informations about an application source code.
Such informations are:

* Programming languages.
* Frameworks.
* Tools used to build the application.

Additionaly, Alizer can also select one devfile (cloud workspace file) from a list of available devfiles and/or
detect components (the concept of component is taken from Odo and its definition can be read on [odo.dev](https://odo.dev/docs/getting-started/basics/#component)).

In order to be easily intergrated in other projects, comes in 3 different implementations:

* Java library & CLI
* Go library & CLI
* NPM package

*NOTE: Not all implementations support the same features. Please check the table at [alizer-spec](docs/public/alizer-spec.md#feature-table) for a detailed overview.*

## Usage
As mentioned above Alizer comes with 3 different implementations. Each one has a different usage guide:

* [Java library & CLI](java/README.md#Usage)
* [Go library & CLI](go/README.md#Usage)
* [NPM package](js/README.md#Usage)

## Contributing
This is an open source project open to anyone. This project welcomes contributions and suggestions!

For information on getting started, refer to the [CONTRIBUTING instructions](CONTRIBUTING.md).

## Feedback & Questions
If you discover an issue please file a bug and we will fix it as soon as possible.
* File a bug in [GitHub Issues](https://github.com/redhat-developer/alizer/issues).

## License
EPL 2.0, See [LICENSE](LICENSE) for more information.
