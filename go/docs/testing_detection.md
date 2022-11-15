## Testing Detection in Alizer

As you may guess, testing Alizer is a very tough task as it can be run on any project out there and being sure that the detection works fine with every combination of languages/frameworks/tools/project structure is almost impossible.

To simplify it, we implemented a system to allow to add any GitHub repository as target to test Alizer detection without writing any new code. 
The file `git_test.json` contains a list of all repositories and the outcome that Alizer should detect when running on those repos.
When a new repo is considered a valid candidate to test Alizer it can be added to `git_test.json` without editing any code.

The Json schema used is 

```
    <repository-url>: { // the url of a github repository
        "commit": <commit-id>, // we use commit id instead of a branch name as this could lead to unstable results if the branch is updated over time
        "components": [ // all components that Alizer should detect
            {
                "name": <component-name>, // the name of the component/project detected
                "languages": [
                    {
                        "name": <language-name>, // language used to develop this component. The list of languages known by Alizer is limited to those documented.
                        "frameworks": <frameworks-list>, // zero or more frameworks used in this component. The list of frameworks known by Alizer is limited to those documented.
                        "tools": <tools-list> // zero or more frameworks used in this component. The list of tools known by Alizer is limited to those documented.
                    },
                    <language-entry>,  // one or more other language entries 
                ]
            },
            <component-entry> // one or more other component entries
        ]
    }
``` 

*Note:* the `commit-id` value must contains the full commit id. By using the short/truncated one may lead to execution errors.

Below a real example

```
    "https://github.com/lstocchi/translate-example.git": {
        "commit": "7faf51a95f588b32d8367aa48f61d55397ff6229",
        "components": [
            {
                "languages": [
                    {
                        "name": "JavaScript",
                        "frameworks": [ "React" ],
                        "tools": [
                            "NodeJs"
                        ]
                    }        
                ],
                "name": "event-handler"
            },
            {
                "name": "viewer",
                "languages": [
                    {
                        "name": "Go",
                        "frameworks": [],
                        "tools": [
                            "1.14"
                        ]
                    },
                    {
                        "name": "Modula-2",
                        "frameworks": [],
                        "tools": []
                    },
                    {
                        "name": "AMPL",
                        "frameworks": [],
                        "tools": []
                    }        
                ]
            }
        ]
    }
```