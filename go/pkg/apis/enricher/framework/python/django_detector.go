package recognizer

import (
	"github.com/redhat-developer/alizer/pkg/apis/language"
	"github.com/redhat-developer/alizer/pkg/utils"
)

type DjangoDetector struct{}

func (d DjangoDetector) DoFrameworkDetection(language *language.Language, files *[]string) {
	managePy := utils.GetFile(files, "manage.py")
	urlsPy := utils.GetFile(files, "urls.py")
	wsgiPy := utils.GetFile(files, "wsgi.py")
	asgiPy := utils.GetFile(files, "asgi.py")

	djangoFiles := []string{}
	utils.AddToArrayIfValueExist(&djangoFiles, managePy)
	utils.AddToArrayIfValueExist(&djangoFiles, urlsPy)
	utils.AddToArrayIfValueExist(&djangoFiles, wsgiPy)
	utils.AddToArrayIfValueExist(&djangoFiles, asgiPy)

	if hasFramework(files, "from django.") {
		language.Frameworks = append(language.Frameworks, "Django")
	}
}
