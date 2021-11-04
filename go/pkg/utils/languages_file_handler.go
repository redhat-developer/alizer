package utils

import (
	"errors"
	"os"
	"path/filepath"
	"runtime"

	"github.com/redhat-developer/alizer/pkg/schema"
	"gopkg.in/yaml.v3"
)

type LanguageFileItem struct {
	Name    string
	Aliases []string
	Kind    string
	Group   string
}

type LanguageFile struct {
	languages           map[string]LanguageFileItem
	extensionsXLanguage map[string][]LanguageFileItem
}

var instance *LanguageFile

func Get() *LanguageFile {
	if instance == nil {
		instance = create()
	}
	return instance
}

func create() *LanguageFile {
	languages := make(map[string]LanguageFileItem)
	extensionsXLanguage := make(map[string][]LanguageFileItem)

	languagesProperties := getLanguagesProperties()

	for name, properties := range languagesProperties {
		languageItem := LanguageFileItem{
			Name:    name,
			Aliases: properties.Aliases,
			Kind:    properties.Type,
			Group:   properties.Group,
		}
		languages[name] = languageItem
		extensions := properties.Extensions
		for _, ext := range extensions {
			languagesByExtension := extensionsXLanguage[ext]
			languagesByExtension = append(languagesByExtension, languageItem)
			extensionsXLanguage[ext] = languagesByExtension
		}
	}

	return &LanguageFile{
		languages:           languages,
		extensionsXLanguage: extensionsXLanguage,
	}
}

func getLanguagesProperties() schema.LanguagesProperties {
	langFilePath, err := getLanguagesFilePath()
	if err != nil {
		return schema.LanguagesProperties{}
	}
	yamlFile, err := os.ReadFile(langFilePath)
	if err != nil {
		return schema.LanguagesProperties{}
	}
	var data schema.LanguagesProperties
	yaml.Unmarshal(yamlFile, &data)
	return data
}

func getLanguagesFilePath() (string, error) {
	_, b, _, _ := runtime.Caller(0)
	basepath := filepath.Dir(b)
	langFilePath := filepath.Join(basepath, "..", "..", "languages.yml")
	if _, err := os.Stat(langFilePath); errors.Is(err, os.ErrNotExist) {
		return "", errors.New("no languages.yml file found")
	}
	return langFilePath, nil
}

func (l LanguageFile) GetLanguagesByExtension(extension string) []LanguageFileItem {
	return l.extensionsXLanguage[extension]
}

func (l LanguageFile) GetLanguageByName(name string) (LanguageFileItem, error) {
	for langName, langItem := range l.languages {
		if langName == name {
			return langItem, nil
		}
	}
	return LanguageFileItem{}, errors.New("no language found with this name")
}
