# Alizer

## Summary

Alizer (which stands for Application Analyzer) is a utilily whose goal is to extract information about an application source code. 

This folder includes the GoLang version of it.

Alizer in Go extends three different detections:

- Language Detection
- Component Detection
- Devfile Detection

## Usage

### Language Detection

It allows to analyze the source code and extract informations about the languages, frameworks and tools used.

The result is an ordered (sorted by usage) list of informations for each language detected in the source tree, with the following [data](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/language/language.go#L13):

- *Name*: the name of the detected language
- *Aliases*: other names which identify the detected language
- *Framework*: a list of detected frameworks (Quarkus, Flash,...) used by the application
- *Tools*: a list of tools (Maven,...) used by the application
- *Weight*: a double value that represents the language weight compared to the others.

NOTE: the sum of all weights can be over 100 because a file may be associated to multiple languages and Alizer may not be able to detect it precisely. E.g. a SQL script could be associated to SPLPL, TSQL, PLSQL so Alizer will print out all 3 languages with the same weight.

To analyze your source code with Alizer, just import it and use the recognizer:

```
import "github.com/redhat-developer/alizer/pkg/apis/recognizer"

languages, err := recognizer.Analyze("./")
```

### Component Detection

It detects all components which are found in the source tree. The concept of component is taken from Odo and its definition can be read on [odo.dev](https://odo.dev/docs/overview/basics#component).

Component detection is only enabled for a subset of programming languages
- Java
- Go
- .NET (C#, F#, VB.NET)
- JavaScript
- Python
- Rust

and it consists of two steps:
1) Detect languages which have a configuration file such as Java (`pom.xml`, `build.gradle`)
2) Detect languages which may not have a configuration file such as Python

Alizer scans all files in the source tree to find out a configuration file. If found and the language detected is enabled (belong to the list above) a component is said to be found starting from that path. Only one component per path is possible. This step gets repeated for all configuration files present in the source tree which live into different paths.
Once the first step ends up, if there are no other free subfolders (free = folders that do not belong to any component) the second step is skipped otherwise Alizer tries to search for a component written with a language which may not have a configuration file. In that subfolder a simple Language detection is performed and the first language is taken into account for further calculations. 

The result is a list of components where each component consists of:
- *name*: name of the component
- *path*: root of the component 
- *languages*: list of languages belonging to the component ordered by their relevance.

```
import "github.com/redhat-developer/alizer/pkg/apis/recognizer"

components, err := recognizer.DetectComponents("./")
```

For more info about name detection plase check the [name detection](docs/name_detection.md) doc.
### Devfile Detection

It selects a devfile from a list of devfiles (from a devfile registry or other storage) based on the information found in the source tree. 

Alizer searches for a component in the root folder and uses its information to select a devfile. If nothing is found in the root, a full components detection is performed and the first component in the resulting list is used to select a devfile. If no component is found in the whole source tree, a generic Language detection is run and the first language in the resulting list is used to select a devfile.

```
import "github.com/redhat-developer/alizer/pkg/apis/recognizer"

devfile, err := recognizer.SelectDevFileFromTypes("myproject", devfiles)
```

where devfiles is a slice of `DevfileType` struct which contains the following named fields/properties:
- *name*: name of the devfile
- *language*: name of the language associated with the devfile
- *projectType*: type of project associated with the devfile
- *tags*: list of tags associated with the devfile

Please note that the int value returned by the method is the position of the devfile selected by Alizer within the slice passed as parameter.
