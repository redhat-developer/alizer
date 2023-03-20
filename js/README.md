# Alizer - NPM Package

## Usage
Alizer also offers a NPM package providing helpers for recognize languages/frameworks in a project. It still doesn't support devfile selection.
The syntax is the following:
```js
import * as recognizer from '@redhat-developer/alizer';

.....

const languages = await recognizer.detectLanguages('my_project_folder');

.....

```
The result is an ordered list of information for each language detected in the source tree, with the following informations:
- *name*: the name of the detected language
- *aliases*: other names which identify the detected language 
- *builder*: tools that used to build the application
- *frameworks*: a list of detected frameworks (Quarkus, Flash,...) used by the application.

export interface DevfileType {
    getName(): string;
    getLanguage(): string;
    getProjectType(): string;
    getTags(): string[];
}

The NMP package 
```js
import * as recognizer from '@redhat-developer/alizer';

.....

const devfile = await recognizer.selectDevFileFromTypes('my_project_folder');

.....

```
The result is an ordered list of information for each language detected in the source tree, with the following informations:
- *name*: the name of the detected language.
- *language*: name of the language associated with the devfile.
- *projectType*: type of project associated with the devfile.
- *tags*: list of tags associated with the devfile.