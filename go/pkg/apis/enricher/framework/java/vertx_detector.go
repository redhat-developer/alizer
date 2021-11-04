package recognizer

import (
	"github.com/redhat-developer/alizer/pkg/apis/language"
)

type VertxDetector struct{}

func (v VertxDetector) DoFrameworkDetection(language *language.Language, config string) {
	if hasFramework(config, "io.vertx") {
		language.Frameworks = append(language.Frameworks, "Vertx")
	}
}
