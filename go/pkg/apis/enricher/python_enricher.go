package recognizer

import (
	framework "github.com/redhat-developer/alizer/pkg/apis/enricher/framework/python"
	"github.com/redhat-developer/alizer/pkg/apis/language"
)

type PythonEnricher struct{}

func getPythonFrameworkDetectors() []FrameworkDetectorWithoutConfigFile {
	return []FrameworkDetectorWithoutConfigFile{
		&framework.DjangoDetector{},
	}
}

func (p PythonEnricher) GetSupportedLanguages() []string {
	return []string{"python"}
}

func (p PythonEnricher) DoEnrichLanguage(language *language.Language, files *[]string) {
	language.Tools = []string{}
	detectPythonFrameworks(language, files)
}

func detectPythonFrameworks(language *language.Language, files *[]string) {
	for _, detector := range getPythonFrameworkDetectors() {
		detector.DoFrameworkDetection(language, files)
	}
}
