package recognizer

import (
	"github.com/redhat-developer/alizer/pkg/apis/language"
)

type MicronautDetector struct{}

func (m MicronautDetector) DoFrameworkDetection(language *language.Language, config string) {
	if hasFramework(config, "io.micronaut") {
		language.Frameworks = append(language.Frameworks, "Micronaut")
	}
}
