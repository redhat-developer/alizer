package recognizer

import (
	"github.com/redhat-developer/alizer/pkg/apis/language"
)

type OpenLibertyDetector struct{}

func (o OpenLibertyDetector) DoFrameworkDetection(language *language.Language, config string) {
	if hasFramework(config, "io.openliberty") {
		language.Frameworks = append(language.Frameworks, "OpenLiberty")
	}
}
