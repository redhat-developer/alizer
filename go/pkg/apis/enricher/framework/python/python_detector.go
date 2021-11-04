package recognizer

import (
	utils "github.com/redhat-developer/alizer/pkg/utils"
)

func hasFramework(files *[]string, tag string) bool {
	for _, file := range *files {
		if utils.IsTagInFile(file, tag) {
			return true
		}
	}
	return false
}
