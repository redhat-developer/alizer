# Alizer - Go Library & CLI

## Usage

### Library
Below you can find usage information about the Go library.

#### Language Detection
To analyze your source code with Alizer, just import it and use the recognizer:
```go
import "github.com/redhat-developer/alizer/pkg/apis/recognizer"

languages, err := recognizer.Analyze("./")
```

The result is an ordered list *(sorted by usage)* of information for each language detected in the source tree, with the following [data](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/language/language.go#L13):
- *Name*: the name of the detected language
- *Aliases*: other names which identify the detected language
- *Framework*: a list of detected frameworks (Quarkus, Flash,...) used by the application
- *Tools*: a list of tools (Maven,...) used by the application
- *Weight*: a double value that represents the language weight compared to the others.

*NOTE: the sum of all weights can be over 100 because a file may be associated to multiple languages and Alizer may not be able to detect it precisely. E.g. a SQL script could be associated to SPLPL, TSQL, PLSQL so Alizer will print out all 3 languages with the same weight.*

#### Devfile Detection
It selects a devfile from a list of devfiles (from a devfile registry or other storage) based on the information found in the source tree. 
```go
import "github.com/redhat-developer/alizer/pkg/apis/recognizer"

devfile, err := recognizer.SelectDevFileFromTypes("myproject", devfiles)
```

The response is a slice of `DevfileType` struct which contains the following named fields/properties:
- *name*: name of the devfile
- *language*: name of the language associated with the devfile
- *projectType*: type of project associated with the devfile
- *tags*: list of tags associated with the devfile

Alizer searches for a component in the root folder and uses its information to select a devfile. If nothing is found in the root, a full components detection is performed and the first component in the resulting list is used to select a devfile. If no component is found in the whole source tree, a generic Language detection is run and the first language in the resulting list is used to select a devfile.

*NOTE: The int value returned by the method is the position of the devfile selected by Alizer within the slice passed as parameter.*

#### Component Detection
It detects all components which are found in the source tree. The concept of component is taken from Odo and its definition can be read on [odo.dev](https://odo.dev/docs/overview/basics#component).
```go
import "github.com/redhat-developer/alizer/pkg/apis/recognizer"

components, err := recognizer.DetectComponents("./")
```

The result is a list of components where each component consists of:
- *name*: name of the component
- *path*: root of the component 
- *languages*: list of languages belonging to the component ordered by their relevance.
- *ports*: list of ports used by the component

Port detection is a long and consuming job and because of the different algorithms used for each different framework it deserves its own section. For more info check [port detection](docs/port_detection.md)

For more info about name detection plase check the [name detection](docs/name_detection.md) doc.

Component detection is only enabled for a subset of programming languages
- Java
- Go
- .NET (C#, F#, VB.NET)
- JavaScript
- Python
- Rust
- PHP

It consists of two steps:
1) Detect languages which have a configuration file such as Java (`pom.xml`, `build.gradle`)
2) Detect languages which may not have a configuration file such as Python

Alizer scans all files in the source tree to find out a configuration file. If found and the language associated to it (e.g pom.xml -> JAVA) is enabled (belong to the list above) a component is said to be found starting from that path. This step gets repeated for all configuration files present in the source tree. To speed up the detection only the language associated to the configuration file is analyzed. If a configuration file can be associated to multiple languages (e.g. `.proj` can be part of either a C# or F# or VB.NET project) then a deep analysis is made.

Once the first step ends up, if there are no other free subfolders (free = folders that do not belong to any component) the second step is skipped otherwise Alizer tries to search for a component written with a language which may not have a configuration file. In that subfolder a simple Language detection is performed and the first language is taken into account for further calculations. 

### CLI
The Golang implementation includes CLI for applications.

#### How to Build & Use
The Go CLI can be built with the below command:
```bash
# inside the go/ dir
$ go build alizer.go
```

In order to use the CLI:
```
$ ./alizer analyze <path>
$ ./alizer devfile <path>
```

#### Outputs
Example of `analyze` command:
```json
[
	{
		"Name": "Go",
		"Aliases": [
			"golang"
		],
		"Weight": 94.72,
		"Frameworks": [],
		"Tools": [
			"1.18"
		],
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
				"Frameworks": [
					"Spring"
				],
				"Tools": [
					"Maven"
				],
				"CanBeComponent": true
			}
		],
		"Ports": null
	},
]
```

Example of `devfile` command:
```json
[
	{
		"Name": "nodejs",
		"Language": "JavaScript",
		"ProjectType": "Node.js",
		"Tags": [
			"Node.js",
			"Express",
			"ubi8"
		]
	}
]
```