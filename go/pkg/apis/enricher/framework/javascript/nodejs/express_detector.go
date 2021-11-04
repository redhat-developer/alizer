package recognizer

import (
	"github.com/redhat-developer/alizer/pkg/apis/language"
)

type ExpressDetector struct{}

func (e ExpressDetector) DoFrameworkDetection(language *language.Language, config string) {
	if hasFramework(config, "express") {
		language.Frameworks = append(language.Frameworks, "Express")
	}
}
