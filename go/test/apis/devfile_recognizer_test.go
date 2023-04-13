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
	"testing"

	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	"github.com/redhat-developer/alizer/go/pkg/apis/recognizer"
)

func TestDetectQuarkusDevfile(t *testing.T) {
	detectDevFile(t, "quarkus", []string{"java-quarkus"})
}

func TestDetectMicronautDevfile(t *testing.T) {
	detectDevFile(t, "micronaut", []string{"java-maven"})
}

func TestDetectNodeJSDevfile(t *testing.T) {
	detectDevFile(t, "nodejs-ex", []string{"nodejs"})
}

func TestDetectDjangoDevfile(t *testing.T) {
	detectDevFile(t, "django", []string{"python-django"})
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
	detectDevFileUsingLanguages(t, "", languages, []string{"python-django"})
}

func TestDetectQuarkusDevfileUsingLanguages(t *testing.T) {
	detectDevFileUsingLanguages(t, "quarkus", []model.Language{}, []string{"java-quarkus"})
}

func TestDetectMicronautDevfileUsingLanguages(t *testing.T) {
	detectDevFileUsingLanguages(t, "micronaut", []model.Language{}, []string{"java-maven"})
}

func TestDetectNodeJSDevfileUsingLanguages(t *testing.T) {
	detectDevFileUsingLanguages(t, "nodejs-ex", []model.Language{}, []string{"nodejs"})
}

func TestDetectGoDevfile(t *testing.T) {
	detectDevFile(t, "golang-gin-app", []string{"go"})
}

func TestDetectAngularDevfile(t *testing.T) {
	detectDevFile(t, "angularjs", []string{"Angular"})
}

func TestDetectNextJsDevfile(t *testing.T) {
	detectDevFile(t, "nextjs-app", []string{"Next.js"})
}

func TestDetectNuxtJsDevfile(t *testing.T) {
	detectDevFile(t, "nuxt-app", []string{"nodejs-nuxtjs", "nodejs-vue"})
}

func TestDetectVueDevfile(t *testing.T) {
	detectDevFile(t, "vue-app", []string{"nodejs-vue"})
}

func TestDetectSpringDevfile(t *testing.T) {
	detectDevFile(t, "spring", []string{"java-spring", "java-springboot"})
}

func detectDevFile(t *testing.T, projectName string, devFilesName []string) {
	detectDevFilesFunc := func(devFileTypes []model.DevFileType) ([]int, error) {
		testingProjectPath := GetTestProjectPath(projectName)
		return recognizer.SelectDevFilesFromTypes(testingProjectPath, devFileTypes)
	}
	detectDevFileInner(t, devFilesName, detectDevFilesFunc)
}

func detectDevFileUsingLanguages(t *testing.T, projectName string, languages []model.Language, devFileName []string) {
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
	detectDevFileInner(t, devFileName, detectDevFileFunc)
}

func detectDevFileInner(t *testing.T, expectedDevFilesName []string, detectFuncInner func([]model.DevFileType) ([]int, error)) {
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
	}
}
