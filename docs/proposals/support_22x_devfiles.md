# Add Support for Specific Devfile Versions & Enhance Logging

## Related Github Issues
 - EPIC: https://github.com/redhat-developer/alizer/issues/159
  
## Background
As described in the dedicated proposal [Devfile 2.0 outer-loop devfile build and deploy functions](https://github.com/redhat-developer/alizer/issues/153) the devfile version 2.2.x is supporting the outer-loop scenarios. This means that the devfile build will not only focus on the building of the application but it will also cover building runtime container that runs the application.

On the other hand, it would be useful to add some logging on the 3 commands available to the CLI (`analyze`, `devfile`, `component`). As both features demand new args for the CLI, it might be a good idea to include them both in the same proposal.

## Alizer's Role for Devfiles 2.2.x Support
Alizer's CLI & Library should be able to always match a devfile that is being supported by the client. For example, if a client supports versions `<=2.1.x` the CLI or the Library shouldn nott match a devfile of version `2.2.x`.

## Logging Enhancements
As mentioned above, there is common ground on the two issues. This means that we can introduce an additional argument for the CLI which will enable logging.

## CLI Arguments
The `alizer devfile` command should have arguments in order to specify which devfiles are acceptable or not.

### max-version
If this argument is passed in a alizer devfile command then, alizer should match a devfile with **equal or older version** than the one given.

### min-version
If this argument is passed in a alizer devfile command then, alizer should match a devfile with **equal or newer version** than the one given.

_The results of devfiles have to be sorted from newest to oldest._

### deployment-scope
Accepted values [innerloop, outerloop]. If another value is passed raises an error.

If this argument is passed in a alizer devfile command then, alizer should match a devfile having the request deployment-scope.

_The results of devfiles have to be sorted from newest to oldest._

### log
This argument can have the values: debug, info, warning. If it's passed to the CLI command we will have to log information regarding the running command.

## Logging Level Examples
Let's say that someone wants to analyze a project and they want to debug alizer and its logs for this command:
```bash
$ ./alizer analyze --log debug <path>

2023-03-07T11:32:29.167Z  DEBUG Searching file paths from root
2023-03-07T11:32:29.167Z  INFO  Found 1 Cached file paths from root
2023-03-07T11:32:29.167Z  DEBUG Searching for language file extensions in given paths
2023-03-07T11:32:29.167Z  INFO  Found 15 file extensions in given paths
2023-03-07T11:32:29.167Z  DEBUG Checking extension .k
2023-03-07T11:32:29.167Z  DEBUG Not able to match .k extension with any known language
2023-03-07T11:32:29.167Z  DEBUG Checking extension .py
2023-03-07T11:32:29.167Z  DEBUG Found 2 languages for extension .py
2023-03-07T11:32:29.167Z  INFO  Accessing languages for extension .py
2023-03-07T11:32:29.167Z  DEBUG Accessing Jython language
2023-03-07T11:32:29.167Z  INFO  Jython is not a programming language skipping
2023-03-07T11:32:29.167Z  DEBUG Accessing Python language
2023-03-07T11:32:29.167Z  INFO  Extension .py was found 4 times. Adding Python to detectedLanguages
...
After checking all extensions
...
2023-03-07T11:32:29.167Z  DEBUG Accessing 1 detected programming languages
2023-03-07T11:32:29.167Z  INFO  Python weight is 100. Detecting frameworks
```

_If --log is absent the default behavior is to have ERROR level logging._

## Version Examples
For example let's say that the project A has 3 potential matches for devfiles `devfile A`, `devfile B` and `devfile C`:

_devfileA.yml:_
```yaml
schemaVersion: 2.0.0
metadata:
  name: devfileA
  displayName: Devfile A 
  tags:
    - tagA
  projectType: SomeProjectType
  language: SomeLanguage
...
```

_devfileB.yml:_
```yaml
schemaVersion: 2.1.0
metadata:
  name: devfileB
  displayName: Devfile B
  tags:
    - tagB
  projectType: SomeProjectType
  language: SomeLanguage
...
```

_devfileC.yml_
```yaml
schemaVersion: 2.2.0
metadata:
  name: devfileC
  displayName: Devfile C
  tags:
    - tagC
  projectType: SomeProjectType
  language: SomeLanguage
...
```

### Case A - Define only a max-version.
If we choose `max-version = 2.1.0` we should get a list of 2 devfiles from our devfile command:
```bash
$ ./alizer devfile --max-version 2.1.0
[
	{
		"Name": "devfileB",
		"Language": "SomeLanguage",
		"ProjectType": "SomeProjectType",
		"Tags": [
			"tagB"
		]
	},
	{
		"Name": "devfileA",
		"Language": "SomeLanguage",
		"ProjectType": "SomeProjectType",
		"Tags": [
			"tagA"
		]
	}
]
```

### Case B - Define only a min-version.
If we choose `min-version = 2.1.0` we should get a list of 2 devfiles from our devfile command:
```bash
$ ./alizer devfile --min-version 2.1.0
[
	{
		"Name": "devfileC",
		"Language": "SomeLanguage",
		"ProjectType": "SomeProjectType",
		"Tags": [
			"tagC"
		]
	},
	{
		"Name": "devfileB",
		"Language": "SomeLanguage",
		"ProjectType": "SomeProjectType",
		"Tags": [
			"tagB"
		]
	}
]
```

### Case C - Define both min-version and max-version.
If we choose `min-version = 2.1.0` and `max-version = 2.1.2` we should get a list of 1 devfile from our devfile command:
```bash
$ ./alizer devfile --min-version 2.1.0 --max-version 2.1.2
[
	{
		"Name": "devfileB",
		"Language": "SomeLanguage",
		"ProjectType": "SomeProjectType",
		"Tags": [
			"tagB"
		]
	}
]
```

_An error will be raised if the min-version is greater than the max-version_

## Library
In order to facilitate these updates we will have to update our library. The main work will focus inside `devfile_recognizer.go`. There, we will have to implement a new function (`MatchDevfiles`) which will handle cases that we want to filter the devfiles list fetched from a registry. Another addition would be to add a new attribute `SchemaVersion` string in the `model.DevFileType` (`SchemaVersion string`).

_The MatchDevfile function could be a point where we can introduce more args in the future regarding other features._

_We have to keep the old way of fetching devfiles from library in order to ensure the backwards compatibility._

### recognizer.MatchDevfiles
This function will take 3 parameters `(path string, devFileTypes []model.DevFileType, filter map[string]interface{})`. If someone wants to filter the selected devfiles for specific version range the have to write:
```golang
import "github.com/redhat-developer/alizer/pkg/apis/recognizer"
filter := map[string]interface{} {
        "max-version": "2.1.0",
        "min-version": "2.0.0",
}
devfiles, err := recognizer.SelectDevFilesFromTypesWithArgs("myproject", devfiles, filter)
```

### model.DevfileType
In order to be able to check and compare the version of each devfile with the given attribute we have to create a dedicated attribute inside this model:
```golang
type DevFileType struct {
	Name        		string
	Language    		string
	ProjectType 		string
	Tags        		[]string
	SchemaVersion 	      string
}
```