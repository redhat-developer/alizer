package recognizer

import (
	"github.com/redhat-developer/alizer/pkg/apis/language"
)

type SpringDetector struct{}

func (s SpringDetector) DoFrameworkDetection(language *language.Language, config string) {
	if hasFramework(config, "org.springframework") {
		language.Frameworks = append(language.Frameworks, "Spring")
	}
}
