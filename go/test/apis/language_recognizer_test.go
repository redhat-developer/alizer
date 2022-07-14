/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc.
 ******************************************************************************/
package recognizer

import (
	"path/filepath"
	"runtime"
	"strings"
	"testing"

	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	"github.com/redhat-developer/alizer/go/pkg/apis/recognizer"
)

func TestAnalyzeOnMicronaut(t *testing.T) {
	isLanguageInProject(t, "micronaut", "java", []string{"maven"}, []string{"micronaut"})
}

func TestAnalyzeOnQuarkus(t *testing.T) {
	isLanguageInProject(t, "quarkus", "java", []string{"maven"}, []string{"quarkus"})
}

func TestAnalyzeOnJavascript(t *testing.T) {
	isLanguageInProject(t, "nodejs-ex", "javascript", []string{"nodejs"}, []string{"express"})
}

func TestAnalyzeOnDjango(t *testing.T) {
	isLanguageInProject(t, "django", "python", []string{}, []string{"django"})
}

func TestAnalyzeOnCSharp(t *testing.T) {
	isLanguageInProject(t, "s2i-dotnetcore-ex", "c#", []string{}, []string{"net6.0"})
}

func TestAnalyzeOnFSharp(t *testing.T) {
	isLanguageInProject(t, "net-fsharp", "f#", []string{}, []string{"netcoreapp3.1"})
}

func TestAnalyzeOnVBNET(t *testing.T) {
	isLanguageInProject(t, "net-vb", "visual basic .net", []string{}, []string{"netcoreapp3.1"})
}

func TestAnalyzeOnGoGin(t *testing.T) {
	isLanguageInProject(t, "golang-gin-app", "go", []string{"1.15"}, []string{"gin"})
}

func isLanguageInProject(t *testing.T, project string, wantedLanguage string, wantedTools []string, wantedFrameworks []string) {
	testingProjectPath := GetTestProjectPath(project)

	languages, err := recognizer.Analyze(testingProjectPath)
	if err != nil {
		t.Error(err)
	}

	if !hasWantedLanguage(languages, wantedLanguage, wantedTools, wantedFrameworks) {
		t.Errorf("Project does not use " + wantedLanguage + " language")
	}
}

func hasWantedLanguage(languages []model.Language, wantedLang string, wantedTools []string, wantedFrameworks []string) bool {
	for _, lang := range languages {
		if strings.ToLower(lang.Name) == wantedLang {
			return hasWantedTools(lang, wantedTools) && hasWantedFrameworks(lang, wantedFrameworks)
		}
	}
	return false
}

func hasWantedFrameworks(language model.Language, wantedFrameworks []string) bool {
	for _, wantedFramework := range wantedFrameworks {
		if !hasWantedFramework(language, wantedFramework) {
			return false
		}
	}
	return true
}

func hasWantedFramework(language model.Language, wantedFramework string) bool {
	for _, framework := range language.Frameworks {
		if strings.ToLower(framework) == wantedFramework {
			return true
		}
	}
	return false
}

func hasWantedTools(language model.Language, wantedTools []string) bool {
	for _, wantedTool := range wantedTools {
		if !hasWantedTool(language, wantedTool) {
			return false
		}
	}
	return true
}

func hasWantedTool(language model.Language, wantedTool string) bool {
	for _, tool := range language.Tools {
		if strings.ToLower(tool) == wantedTool {
			return true
		}
	}
	return false
}

func GetTestProjectPath(folder string) string {
	_, b, _, _ := runtime.Caller(0)
	basepath := filepath.Dir(b)
	return filepath.Join(basepath, "..", "..", "..", "resources/projects", folder)
}
