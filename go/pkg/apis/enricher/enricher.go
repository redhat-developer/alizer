package recognizer

import (
	"strings"

	"github.com/redhat-developer/alizer/pkg/apis/language"
)

type Enricher interface {
	GetSupportedLanguages() []string
	DoEnrichLanguage(language *language.Language, files *[]string)
}

type FrameworkDetectorWithConfigFile interface {
	DoFrameworkDetection(language *language.Language, config string)
}

type FrameworkDetectorWithoutConfigFile interface {
	DoFrameworkDetection(language *language.Language, files *[]string)
}

func getEnrichers() []Enricher {
	return []Enricher{
		&JavaEnricher{},
		&JavaScriptEnricher{},
		&PythonEnricher{},
	}
}

func GetEnricherByLanguage(language *language.Language) Enricher {
	for _, enricher := range getEnrichers() {
		if isLanguageSupportedByEnricher(language.Name, enricher) {
			return enricher
		}
	}
	return nil
}

func isLanguageSupportedByEnricher(nameLanguage string, enricher Enricher) bool {
	for _, language := range enricher.GetSupportedLanguages() {
		if strings.ToLower(language) == strings.ToLower(nameLanguage) {
			return true
		}
	}
	return false
}
