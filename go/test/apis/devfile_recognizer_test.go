package recognizer

/*******************************************************************************
 * Copyright (c) 2022 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc.
 ******************************************************************************/
import (
	"fmt"
	"testing"

	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	"github.com/redhat-developer/alizer/go/pkg/apis/recognizer"
	"github.com/stretchr/testify/assert"
)

func TestDetectQuarkusDevfile(t *testing.T) {
	detectDevFiles(t, "quarkus", []string{"java-quarkus"})
}

func TestDetectMicronautDevfile(t *testing.T) {
	detectDevFiles(t, "micronaut", []string{"java-maven"})
}

func TestDetectNodeJSDevfile(t *testing.T) {
	detectDevFiles(t, "nodejs-ex", []string{"nodejs"})
}

func TestDetectDotNet50Devfile(t *testing.T) {
	detectDevFile(t, "dotnet5.0", []string{"dotnet50"})
}

func TestDetectDotNet60Devfile(t *testing.T) {
	detectDevFile(t, "dotnet6.0", []string{"dotnet60"})
}

func TestDetectDotNetCore31Devfile(t *testing.T) {
	detectDevFile(t, "dotnetcore3.1", []string{"dotnetcore31"})
}

func TestDetectDjangoDevfile(t *testing.T) {
	detectDevFiles(t, "django", []string{"python-django"})
}

func TestDetectFlaskDevfile(t *testing.T) {
	detectDevFiles(t, "flask", []string{"python"})
}

func TestDetectWildflyDevfile(t *testing.T) {
	detectDevFiles(t, "wildfly", []string{"java-wildfly"})
}

func TestDetectDjangoDevfileUsingLanguages(t *testing.T) {
	languages := []model.Language{
		{
			Name: "Python",
			Aliases: []string{
				"python3",
			},
			Weight: 88.23,
			Frameworks: []string{
				"Django",
			},
			Tools:          []string{},
			CanBeComponent: false,
		},
		{
			Name: "Shell",
			Aliases: []string{
				"sh",
			},
			Weight:         11.77,
			Frameworks:     []string{},
			Tools:          []string{},
			CanBeComponent: false,
		},
	}
	detectDevFilesUsingLanguages(t, "", languages, []string{"python-django"})
}

func TestDetectQuarkusDevfileUsingLanguages(t *testing.T) {
	detectDevFilesUsingLanguages(t, "quarkus", []model.Language{}, []string{"java-quarkus"})
}

func TestDetectMicronautDevfileUsingLanguages(t *testing.T) {
	detectDevFilesUsingLanguages(t, "micronaut", []model.Language{}, []string{"java-maven"})
}

func TestDetectNodeJSDevfileUsingLanguages(t *testing.T) {
	detectDevFilesUsingLanguages(t, "nodejs-ex", []model.Language{}, []string{"nodejs"})
}

func TestDetectGoDevfile(t *testing.T) {
	detectDevFiles(t, "golang-gin-app", []string{"go"})
}

func TestDetectAngularDevfile(t *testing.T) {
	detectDevFiles(t, "angularjs", []string{"Angular"})
}

func TestDetectVertxDevfile(t *testing.T) {
	detectDevFile(t, "vertx", []string{"java-vertx"})
}

func TestDetectNextJsDevfile(t *testing.T) {
	detectDevFiles(t, "nextjs-app", []string{"Next.js"})
}

func TestDetectNuxtJsDevfile(t *testing.T) {
	detectDevFiles(t, "nuxt-app", []string{"nodejs-nuxtjs", "nodejs-vue"})
}

func TestDetectVueDevfile(t *testing.T) {
	detectDevFiles(t, "vue-app", []string{"nodejs-vue"})
}

func TestDetectReactJSDevfile(t *testing.T) {
	detectDevFiles(t, "react", []string{"nodejs-react"})
}

func TestDetectSvelteDevfile(t *testing.T) {
	detectDevFiles(t, "svelte-app", []string{"nodejs-svelte"})
}

func TestDetectSpringDevfile(t *testing.T) {
	detectDevFiles(t, "spring", []string{"java-spring", "java-springboot"})
}

func TestDetectLaravelDevfile(t *testing.T) {
	detectDevFiles(t, "laravel", []string{"php-laravel"})
}

func TestGetUrlWithVersions(t *testing.T) {
	tests := []struct {
		name          string
		minVersion    string
		maxVersion    string
		testUrl       string
		expectedError error
		expectedUrl   string
	}{
		{

			name:          "Case 1: Url with valid min and max versions",
			minVersion:    "2.0.0",
			maxVersion:    "2.2.0",
			testUrl:       "http://localhost:5000/",
			expectedError: nil,
		},
		{
			name:          "Case 2: Url with valid min version",
			minVersion:    "2.0.0",
			maxVersion:    "",
			testUrl:       "http://localhost:5000",
			expectedError: nil,
		},
		{
			name:          "Case 3: Url with valid max version",
			minVersion:    "",
			maxVersion:    "2.2.0",
			testUrl:       "http://localhost:5000/",
			expectedError: nil,
		},
		{
			name:          "Case 4: Url with max version lower than min version",
			minVersion:    "2.2.0",
			maxVersion:    "2.1.0",
			testUrl:       "http://localhost:5000/v2index",
			expectedError: fmt.Errorf("max-version cannot be lower than min-version"),
		},
		{
			name:          "Case 5: Url with max version lower than minimum allowed version",
			minVersion:    "0.1.0",
			maxVersion:    "1.1.0",
			testUrl:       "http://localhost:5000/v2index",
			expectedError: fmt.Errorf("min and/or max version are lower than the minimum allowed version (2.0.0)"),
		},
		{
			name:          "Case 6: Url with max version lower than minimum allowed version & minVersion",
			minVersion:    "2.1.0",
			maxVersion:    "1.1.0",
			testUrl:       "http://localhost:5000/v2index",
			expectedError: fmt.Errorf("max-version cannot be lower than min-version"),
		},
		{
			name:          "Case 7: Url with min version lower than minimum allowed version",
			minVersion:    "1.1.0",
			maxVersion:    "",
			testUrl:       "http://localhost:5000/v2index",
			expectedError: fmt.Errorf("min version is lower than the minimum allowed version (2.0.0)"),
		},
		{
			name:          "Case 8: Url with max version lower than minimum allowed version",
			minVersion:    "",
			maxVersion:    "1.1.0",
			testUrl:       "http://localhost:5000/v2index",
			expectedError: fmt.Errorf("max version is lower than the minimum allowed version (2.0.0)"),
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result, err := recognizer.GetUrlWithVersions(tt.testUrl, tt.minVersion, tt.maxVersion)
			if err != nil {
				assert.EqualValues(t, tt.expectedError, err)
			} else {
				assert.EqualValues(t, getExceptedVersionsUrl(tt.testUrl, tt.minVersion, tt.maxVersion, tt.expectedError), result)
			}
		})
	}
}

func getExceptedVersionsUrl(url, minVersion, maxVersion string, err error) string {
	if err != nil {
		return ""
	} else if minVersion != "" && maxVersion != "" {
		return fmt.Sprintf("%s?minSchemaVersion=%s&maxSchemaVersion=%s", url, minVersion, maxVersion)
	} else if minVersion != "" {
		return fmt.Sprintf("%s?minSchemaVersion=%s", url, minVersion)
	} else if maxVersion != "" {
		return fmt.Sprintf("%s?maxSchemaVersion=%s", url, maxVersion)
	} else {
		return url
	}
}

func detectDevFiles(t *testing.T, projectName string, devFilesName []string) {
	detectDevFilesFunc := func(devFileTypes []model.DevFileType) ([]int, error) {
		testingProjectPath := GetTestProjectPath(projectName)
		return recognizer.SelectDevFilesFromTypes(testingProjectPath, devFileTypes)
	}
	detectDevFilesInner(t, devFilesName, detectDevFilesFunc)
}

func detectDevFile(t *testing.T, projectName string, devFilesName []string) {
	detectDevFilesFunc := func(devFileTypes []model.DevFileType) ([]int, error) {
		testingProjectPath := GetTestProjectPath(projectName)
		devfileIndex, err := recognizer.SelectDevFileFromTypes(testingProjectPath, devFileTypes)
		return []int{devfileIndex}, err
	}
	detectDevFilesInner(t, devFilesName, detectDevFilesFunc)
}

func detectDevFilesUsingLanguages(t *testing.T, projectName string, languages []model.Language, devFileName []string) {
	if projectName != "" {
		testingProjectPath := GetTestProjectPath(projectName)
		var err error
		languages, err = recognizer.Analyze(testingProjectPath)
		if err != nil {
			t.Error(err)
		}
	}
	detectDevFileFunc := func(devFileTypes []model.DevFileType) ([]int, error) {
		return recognizer.SelectDevFilesUsingLanguagesFromTypes(languages, devFileTypes)
	}
	detectDevFilesInner(t, devFileName, detectDevFileFunc)
}

func detectDevFilesInner(t *testing.T, expectedDevFilesName []string, detectFuncInner func([]model.DevFileType) ([]int, error)) {
	devFileTypes := getDevFileTypes()
	foundDevFilesIndexes, err := detectFuncInner(devFileTypes)
	if err != nil {
		t.Error(err)
	}

	found := false
	for _, expectedDevFile := range expectedDevFilesName {
		found = false
		for _, foundDevFileIndex := range foundDevFilesIndexes {
			if devFileTypes[foundDevFileIndex].Name == expectedDevFile {
				found = true
			}
		}
		if !found {
			t.Error("Expected value " + expectedDevFile + " but it was not found")
			return
		}
	}
}

func getDevFileTypes() []model.DevFileType {
	return []model.DevFileType{
		{
			Name:        "java",
			Language:    "java",
			ProjectType: "java",
			Tags:        make([]string, 0),
		},
		{
			Name:        "udi",
			Language:    "Polyglot",
			ProjectType: "default",
			Tags: []string{
				"Java",
				"Maven",
				"Scala",
				"PHP",
				".NET",
				"Node.js",
				"Go",
				"Python",
				"Pip",
				"ubi8",
			},
		},
		{
			Name:        "dotnet50",
			Language:    ".NET",
			ProjectType: "dotnet",
			Tags: []string{
				".Net",
				".Net 5.0",
			},
		},
		{
			Name:        "dotnet60",
			Language:    ".NET",
			ProjectType: "dotnet",
			Tags: []string{
				".Net",
				".Net 6.0",
			},
		},
		{
			Name:        "dotnetcore31",
			Language:    ".NET",
			ProjectType: "dotnet",
			Tags: []string{
				".Net",
				".Net Core App 3.1",
			},
		},
		{
			Name:        "java-quarkus",
			Language:    "java",
			ProjectType: "quarkus",
			Tags: []string{
				"Java",
				"Quarkus",
			},
		},
		{
			Name:        "java-maven",
			Language:    "java",
			ProjectType: "java",
			Tags: []string{
				"Java",
				"Maven",
			},
		},
		{
			Name:        "java-spring",
			Language:    "java",
			ProjectType: "spring",
			Tags: []string{
				"Java",
				"Spring",
			},
		},
		{
			Name:        "java-springboot",
			Language:    "java",
			ProjectType: "springboot",
			Tags: []string{
				"Java",
				"Spring Boot",
			},
		},
		{
			Name:        "java-vertx",
			Language:    "java",
			ProjectType: "vertx",
			Tags: []string{
				"Java",
				"Vert.x",
			},
		},
		{
			Name:        "java-wildfly",
			Language:    "java",
			ProjectType: "wildfly",
			Tags: []string{
				"Java",
				"Wildfly",
			},
		},
		{
			Name:        "nodejs",
			Language:    "nodejs",
			ProjectType: "nodejs",
			Tags: []string{
				"NodeJS",
				"Express",
			},
		},
		{
			Name:        "python-django",
			Language:    "python",
			ProjectType: "django",
			Tags: []string{
				"Python",
				"pip",
			},
		},
		{
			Name:        "python",
			Language:    "python",
			ProjectType: "Python",
			Tags: []string{
				"Python",
				"pip",
				"Flask",
			},
		},
		{
			Name:        "go",
			Language:    "go",
			ProjectType: "go",
			Tags: []string{
				"go",
			},
		},
		{
			Name:        "Angular",
			Language:    "TypeScript",
			ProjectType: "Angular",
			Tags: []string{
				"NodeJS",
				"Angular",
			},
		},
		{
			Name:        "Next.js",
			Language:    "TypeScript",
			ProjectType: "Next.js",
			Tags: []string{
				"Node.js",
				"Next.js",
			},
		},
		{
			Name:        "nodejs-react",
			Language:    "TypeScript",
			ProjectType: "React",
			Tags: []string{
				"Node.js",
				"React",
			},
		},
		{
			Name:        "nodejs-svelte",
			Language:    "TypeScript",
			ProjectType: "Svelte",
			Tags: []string{
				"Node.js",
				"Svelte",
			},
		},
		{
			Name:        "nodejs-nuxtjs",
			Language:    "TypeScript",
			ProjectType: "Nuxt.js",
			Tags: []string{
				"Node.js",
				"Nuxt.js",
			},
		},
		{
			Name:        "nodejs-vue",
			Language:    "TypeScript",
			ProjectType: "Vue",
			Tags: []string{
				"Node.js",
				"Vue",
			},
		},
		{
			Name:        "php-laravel",
			Language:    "PHP",
			ProjectType: "Laravel",
			Tags: []string{
				"PHP",
				"Composer",
				"Laravel",
			},
		},
	}
}
