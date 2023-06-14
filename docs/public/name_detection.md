## Component Name Detection in Alizer

Name detection is one of the step included during component detection and it refers to the name of the app/project.

The process consists of two steps:
1) Some languages (Java, Javascript) have a specific place where the project name is set. If Alizer discovers one of those languages it checks for their configuration files to find out the name; if it fails or a language with no custom detection is detected, it proceeds with (2)
2) The directory name is used as name of the component

Below a list of the languages with a custom detection

### Java

#### Maven

Alizer searches for the `pom.xml` file in the root folder and takes the value defined by the `artifactId` field.

#### Gradle

Alizer searches for the `settings.gradle` file in the root folder and takes the value defined by the `rootProject.name` field.

### Javascript

Alizer searches for the `package.json` file in the root folder and takes the value defined by the `name` field