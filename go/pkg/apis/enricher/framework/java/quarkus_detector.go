package recognizer

import (
	"github.com/redhat-developer/alizer/pkg/apis/language"
)

type QuarkusDetector struct{}

func (q QuarkusDetector) DoFrameworkDetection(language *language.Language, config string) {
	if hasFramework(config, "io.quarkus") {
		language.Frameworks = append(language.Frameworks, "Quarkus")
	}
}
