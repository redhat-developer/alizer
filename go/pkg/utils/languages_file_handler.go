package utils

import (
	"errors"
	"os"
	"path/filepath"
	"reflect"
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

	allLanguages := getLanguagesFileContent()
	fields := reflect.VisibleFields(reflect.TypeOf(struct{ schema.Languages }{}))
	values := reflect.ValueOf(&allLanguages).Elem()

	for _, field := range fields[1:] {
		languageAttributes := values.FieldByName(field.Name)
		languageItem := LanguageFileItem{
			Name:    field.Name,
			Aliases: reflectValueToStringSlice(languageAttributes.FieldByName("Aliases")),
			Kind:    reflectValueToString(languageAttributes.FieldByName("Type")),
			Group:   reflectValueToString(languageAttributes.FieldByName("Group")),
		}
		languages[field.Name] = languageItem
		extensions := reflectValueToStringSlice(languageAttributes.FieldByName("Extensions"))
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

func getLanguagesFileContent() schema.Languages {
	langFilePath, err := getLanguagesFilePath()
	if err != nil {
		return schema.Languages{}
	}
	yamlFile, err := os.ReadFile(langFilePath)
	if err != nil {
		return schema.Languages{}
	}
	var data schema.Languages
	yaml.Unmarshal(yamlFile, &data)
	return data
}

func getLanguagesFilePath() (string, error) {
	_, b, _, _ := runtime.Caller(0)
	basepath := filepath.Dir(b)
	langFilePath := filepath.Join(basepath, "..", "..", "languages.yml")
	if _, err := os.Stat(langFilePath); errors.Is(err, os.ErrNotExist) {
		return "", errors.New("No languages.yml file found")
	}
	return langFilePath, nil
}

func reflectValueToString(value reflect.Value) string {
	if !value.IsValid() {
		return ""
	}
	return value.String()
}

func reflectValueToStringSlice(value reflect.Value) []string {
	var slice, _ = []string{}, false
	if value.IsValid() {
		slice, _ = value.Interface().([]string)
	}
	return slice
}

func (l LanguageFile) GetLanguagesByExtension(extension string) []LanguageFileItem {
	return l.extensionsXLanguage[extension]
}

func (l LanguageFile) GetLanguageByName(name string) (LanguageFileItem, error) {
	var languageItem LanguageFileItem
	for langName, langItem := range l.languages {
		if langName == name {
			return langItem, nil
		}
	}
	return languageItem, errors.New("No language found with this name")
}
