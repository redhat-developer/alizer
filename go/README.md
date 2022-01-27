# Alizer

## Summary

Alizer (which stands for Application Analyzer) is a utilily whose goal is to extract information about an application source code. 

This folder includes the GoLang version of it.

## Usage

### Information
To analyze your source code with Alizer, just import it and use the recognizer:

```
import "github.com/redhat-developer/alizer/pkg/apis/recognizer"

languages, err := recognizer.Analyze("./")
```

The result is an ordered list of information for each language detected in the source tree, with the following [data](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/language/language.go#L13):

- *Name*: the name of the detected language
- *Aliases*: other names which identify the detected language
- *Framework*: a list of detected frameworks (Quarkus, Flash,...) used by the application
- *Tools*: a list of tools (Maven,...) used by the application
- *UsageInPercentage*: a double value that represents the language weight compared to the others.


## Building it for the first time
This project includes a Makefile to make you start working with this project smoothly.

To avoid duplicate some configuration files among all implementations we added them to the root of this repo.
So we first need to copy them withing the go project and then build it so it can embed them.

There are two scripts based on which OS you work with:

Windows

```
$ make initWin
```

Linux/Mac

```
$ make init 
```

This will copy all needed files in correct location and do a first build.
From now you can compile your code as you usually do.