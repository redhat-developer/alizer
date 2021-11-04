package recognizer

import (
	utils "github.com/redhat-developer/alizer/pkg/utils"
)

func hasFramework(configFile string, tag string) bool {
	return utils.IsTagInPackageJsonFile(configFile, tag)
}
