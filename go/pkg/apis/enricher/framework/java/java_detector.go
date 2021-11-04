package recognizer

import (
	utils "github.com/redhat-developer/alizer/pkg/utils"
)

func hasFramework(configFile string, tag string) bool {
	if utils.IsPathOfWantedFile(configFile, "build.gradle") {
		return utils.IsTagInFile(configFile, tag)
	} else {
		return utils.IsTagInPomXMLFile(configFile, tag)
	}
}
