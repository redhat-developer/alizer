# Alizer Specification

## Background

This document outlines the features Alizer offers and how they actually work.

Currently, Alizer provides 3 detection options:

- Language Detection (Language/Tools/Frameworks)
- DevFile Detection
- Component Detection

and it is provided in 4 different formats:

- Java library
- CLI
- NPM package
- Go library

so that it can be integrated easily in other projects.

### Inner-loop scenario

As a Vscode/IntelliJ plugin developer, I would like to detect the languages, tools, frameworks, services used within the
opened user project in the IDE to provide a better user experience and tailor the functionalities I offer them.

## Language Detection

Language detection is based on the file `languages.yml` taken from the [GitHub's Linguist project](https://github.com/github/linguist/blob/master/lib/linguist/languages.yml).
Because of that, Alizer is able to recognize almost any programming languages, with a customized deeper detection of the
ones listed below:

- Java
- Python
- Javascript
- Typescript

Language recognition is performed by using files extensions within the source, and only the languages with a significant
presence (>2% total files) are taken into account for further calculations.
If Java, Python, Javascript or Typescript languages are detected, Alizer proceeds to check for the presence of tools and 
frameworks.

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

- ExpressJs
- ReactJS

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

## DevFile detection

It is possible to select a devfile from a list of devfile metadatas provided by the caller based on information that 
Alizer extracts from the source.

The CLI, through the registry-support module, also works with URLS. If a registry URL is fed to Alizer, it will try to 
download all devfiles from it and select the one which fits best the source, by prioritizing frameworks over tools and languages.
For example, if the source is a Java Maven Quarkus project and the devfiles list contains a Quarkus devfile and a Maven 
one, the Quarkus devfile will be selected.

## Component detection

The concept of component is taken from Odo and its definition can be read on [odo.dev](https://odo.dev/docs/getting-started/basics/#component).

The detection of a component is based on two rules. It is discovered if and only if:

1) The main language of the component source is one of those that supports component detection (Java, Python, Javascript, Go)
2) The source has at least one framework

To perform component detection Alizer splits the languages in two sets: `languages with a configuration file` (like Java
which can have a pom.xml or a build.gradle) and `languages without a configuration file` (such as Python which does not have a 
default config file). 

It first scans all files from the source looking for a file that matches the name of a known 
config file. If found, it checks if this is a valid configuration file (e.g. for a multi-module Maven project only single 
modules pom.xml are taken into account) and if it uses at least one framework. If true, a component is found.

If no config file is detected, Alizer assumes it is dealing with a `language without a configuration file` which can be 
a potential component. It then performs a basic language detection. If a valid `language without a configuration file`
is discovered, a component is found.

## Feature table

|                                  | Java API | CLI | Javascript | Go |
|----------------------------------|----------|-----|------------|----|
| Language/Framework detection     | X        | X   | X          | X  |
| Devfile detection (metadata)     | X        | X   | X          |    |
| Devfile detection (registry URL) |          | X   |            |    |
| Component detection              | X        | X   |            |    |
