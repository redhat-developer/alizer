package apis

import (
	"path/filepath"
	"runtime"
	"strings"
	"testing"

	"github.com/redhat-developer/alizer/pkg/apis"
)

func TestAnalyzeOnMicronaut(t *testing.T) {
	isLanguageInProject(t, "micronaut", "java")
}

func TestAnalyzeOnQuarkus(t *testing.T) {
	isLanguageInProject(t, "quarkus", "java")
}

func TestAnalyzeOnJavascript(t *testing.T) {
	isLanguageInProject(t, "nodejs-ex", "javascript")
}

func TestAnalyzeOnDjango(t *testing.T) {
	isLanguageInProject(t, "django", "python")
}

func isLanguageInProject(t *testing.T, project string, wantedLanguage string) {
	testingProjectPath := getTestProjectPath(project)

	languages, err := apis.Analyze(testingProjectPath)
	if err != nil {
		t.Error(err)
	}

	if !hasWantedLanguage(languages, wantedLanguage) {
		t.Errorf("Project does not use " + wantedLanguage + " language")
	}
}

func hasWantedLanguage(languages []apis.Language, wantedLang string) bool {
	for _, lang := range languages {
		if strings.ToLower(lang.Name) == wantedLang {
			return true
		}
	}
	return false
}

func getTestProjectPath(folder string) string {
	_, b, _, _ := runtime.Caller(0)
	basepath := filepath.Dir(b)
	return filepath.Join(basepath, "..", "..", "..", "resources/projects", folder)
}
