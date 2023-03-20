# Alizer - Java Library & CLI

## Usage

### Library
Below you can find usage information about the Java library.

#### Language Detection
In order to access the Alizer Java API you will have to create a `LanguageRecognizer` object:
```java
LanguageRecognizer recognizer = new RecognizerFactory().createLanguageRecognizer();
```

Then, access the project information with the `analyze` method:
```java
List<Language> languages = recognizer.analyze("myproject");
```

The result is an ordered list of information for each language detected in the source tree, with the following informations:
- *name*: the name of the detected language
- *framework*: a list of detected frameworks (Quarkus, Flash,...) used by the application
- *tools*: a list of tools (Maven,...) used by the application
- *weight*: a double value that represents the language weight compared to the others.

*NOTE: the sum of all weights can be over 100 because a file may be associated to multiple languages and Alizer may not be able to detect it precisely. E.g. a SQL script could be associated to SPLPL, TSQL, PLSQL languages so Alizer will return all 3 with the same weight.*

#### Devfile Detection
It is also possible to select a devfile from a list of existing devfiles (from a devfile registry or other storage) based on the information that we can now extract from the source tree:
```java
DevfileType devfile = recognizer.selectDevFileFromTypes("myproject", devfiles)
```
where devfiles is a list of objects implementing the `DevfileType` interface which defines the following properties:
- *name*: name of the devfile
- *language*: name of the language associated with the devfile
- *projectType*: type of project associated with the devfile
- *tags*: list of tags associated with the devfile

Please note that the devfile object that is returned by the method is one of the object from the list of devfiles given in input for better matching for the user.

#### Component detection
Alizer is also able to detect components. The concept of component is taken from Odo and its definition can be read on [odo.dev](https://odo.dev/docs/getting-started/basics/#component).

The detection of a component is based on only one rule. It is discovered if and only if the main language of the component source is one of those that supports component detection (Java, Python, Javascript, Go, ...)

The result is a list of components where each component consists of:
- *path*: root of the component 
- *languages*: list of languages belonging to the component ordered by their relevance.

```java
ComponentRecognizer recognizer = new RecognizerFactory().createComponentRecognizer();
List<Component> components = recognizer.analyze("myproject");
```

### CLI
The Java implementation includes a CLI for applications.

#### How to Build & Use
The Java CLI can be built with the below command:
```bash
# inside the java/ dir
$ ./mvnw package
```

In order to use the CLI:
```
# inside the java/alizer-cli/target dir
alizer-cli-$version-runner analyze [-o json] <path>
alizer-cli-$version-runner devfile [-o json] [-r registry-url] <path>
```

#### Outputs 

Example of `analyze` command:
```
Language:JavaScript Frameworks:Express Tools:nodejs Accuracy:100.0
```

Example of `component` command:
```
Component:path/to/the/component1 Languages:JavaScript
Component:path/to/the/component2 Languages:TypeScript,JavaScript
Component:path/to/the/component3 Languages:TypeScript
```
