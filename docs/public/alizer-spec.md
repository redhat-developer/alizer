# Alizer Specification

## Background

This document outlines the features Alizer offers and how they actually work.

Currently, Alizer provides 3 detection options:

- _Language Detection_ (Language/Tools/Frameworks)
- _DevFile Detection_
- _Component Detection_

## Language Detection

Language detection is based on the file `languages.yml` taken from the [GitHub's Linguist project](https://github.com/github/linguist/blob/master/lib/linguist/languages.yml). Because of that, Alizer is able to recognize almost any programming languages, with a customized deeper detection of the
ones listed below:

- Java
- Python
- Javascript
- Typescript
- C#, F#, VB.NET
- GoLang

Language recognition is performed by using files extensions within the source, and only the languages with a significant
presence (>2% total files) are taken into account for further calculations. If any of languages above is detected, Alizer proceeds to check for the presence of tools and frameworks.

### Java

The first step in our deeper Java detection is finding the configuration file used. If a `pom.xml` file is discovered,
Alizer assumes is a Maven project. The same for a `build.gradle` file which is a Gradle Project or a `build.xml` for an
Ant project.

NOTE: Maven, Gradle and Ant are saved as Tools inside the data structure returned by the analyze primitive

By reading the content of its configuration file and mainly its dependencies section, Alizer is also able to detect
frameworks. Currently, it recognizes:

- Micronaut
- OpenLiberty
- Quarkus
- SpringBoot
- Vertx

```
{
    name: 'java',
    tools: [ 'maven' ],
    frameworks: [ 'quarkus' ]
}
```

### Javascript/Typescript

The detection for Javascript/Typescript works similar to Java. The first thing Alizer does is to check if a `package.json`
file is in the project. If so, Alizer assumes it is a NodeJs project.

At this point, it reads its content looking for dependencies to discover frameworks. Currently, it recognizes:

- Angular
- ExpressJs
- Next
- Nuxt
- ReactJS
- Svelte
- Vue

```
{
    name: 'javascript',
    tools: [ 'nodejs' ],
    frameworks: [ 'express' ]
}
```

### Python

Currently, the only Python framework Alizer is able to detect is Django.
To recognize it, it scans all files within the source looking for a file such as `manage.py`, `urls.py`, `wsgi.py`,
`asgi.py`. If at least one of them if discovered, it checks its content looking for a django import.

```
{
    name: 'python',
    tools: [],
    frameworks: [ 'django' ]
}
```

### GoLang

The detection for GoLang works similar to Java. The first thing Alizer does is to check if a `go.mod`
file is in the project. If so, Alizer assumes it is a GoLang project.

At this point, it reads its content looking for dependencies to discover frameworks. Currently, it recognizes:

- Beego
- Echo
- FastHttp
- Gin
- GoFiber
- Mux

```
{
    name: 'go',
    tools: [ '1.17' ],
    frameworks: [ 'gin' ]
}
```

NOTE: The Go version is saved as Tools inside the data structure returned by the analyze primitive

## DevFile detection

It is possible to select a devfile from a list of devfile metadatas provided by the caller based on information that
Alizer extracts from the source.

Alizer searches for a component in the root folder and uses its information to select a devfile. If nothing is found in the root,
a full components detection is performed and the first component in the resulting list is used to select a devfile.
If no component is found in the whole source tree, a generic Language detection is run and the first language in the resulting list is used to select a devfile.

The CLI, through the registry-support module, also works with URLS. If a registry URL is fed to Alizer, it will try to
download all devfiles from it and select the one which fits best the source, by prioritizing frameworks over tools and languages.
For example, if the source is a Java Maven Quarkus project and the devfiles list contains a Quarkus devfile and a Maven
one, the Quarkus devfile will be selected.

## Component detection

The concept of component is taken from Odo and its definition can be read on [odo.dev](https://odo.dev/docs/getting-started/basics/#component).

Component detection is only enabled for a subset of programming languages

- Java
- Go
- .NET (C#, F#, VB.NET)
- JavaScript
- Python
- Rust

To perform component detection Alizer splits the languages in two sets: `languages with a configuration file` (like Java
which can have a pom.xml or a build.gradle) and `languages without a configuration file` (such as Python which does not have a
default config file).

It first scans all files from the source tree looking for a file that matches the name of a known
config file. If found, it checks if this is a valid configuration file (e.g. for a multi-module Maven project only single
modules pom.xml are taken into account). If true, a component is found. This step gets repeated for all configuration files found in the source tree.
Only one component per folder is possible.

Once the first step ends up, if there are other free subfolders (free = folders that do not belong to any component) Alizer tries to search for
a `language without a configuration file` in them. A simple Language detection is performed and the first language is taken into account for further calculations.
