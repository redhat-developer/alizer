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
	detectDevFile(t, "quarkus", "java-quarkus")
}

func TestDetectMicronautDevfile(t *testing.T) {
	detectDevFile(t, "micronaut", "java-maven")
}

func TestDetectNodeJSDevfile(t *testing.T) {
	detectDevFile(t, "nodejs-ex", "nodejs")
}

func TestDetectDjangoDevfile(t *testing.T) {
	detectDevFile(t, "django", "python-django")
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
	detectDevFileUsingLanguages(t, "", languages, "python-django")
}

func TestDetectQuarkusDevfileUsingLanguages(t *testing.T) {
	detectDevFileUsingLanguages(t, "quarkus", []model.Language{}, "java-quarkus")
}

func TestDetectMicronautDevfileUsingLanguages(t *testing.T) {
	detectDevFileUsingLanguages(t, "micronaut", []model.Language{}, "java-maven")
}

func TestDetectNodeJSDevfileUsingLanguages(t *testing.T) {
	detectDevFileUsingLanguages(t, "nodejs-ex", []model.Language{}, "nodejs")
}

func TestDetectGoDevfile(t *testing.T) {
	detectDevFile(t, "golang-gin-app", "go")
}

func TestDetectAngularDevfile(t *testing.T) {
	detectDevFile(t, "angularjs", "Angular")
}

func detectDevFile(t *testing.T, projectName string, devFileName string) {
	detectDevFileFunc := func(devFileTypes []model.DevFileType) (int, error) {
		testingProjectPath := GetTestProjectPath(projectName)
		return recognizer.SelectDevFileFromTypes(testingProjectPath, devFileTypes)
	}
	detectDevFileInner(t, devFileName, detectDevFileFunc)
}

func detectDevFileUsingLanguages(t *testing.T, projectName string, languages []model.Language, devFileName string) {
	if projectName != "" {
		testingProjectPath := GetTestProjectPath(projectName)
		var err error
		languages, err = recognizer.Analyze(testingProjectPath)
		if err != nil {
			t.Error(err)
		}
	}
	detectDevFileFunc := func(devFileTypes []model.DevFileType) (int, error) {
		return recognizer.SelectDevFileUsingLanguagesFromTypes(languages, devFileTypes)
	}
	detectDevFileInner(t, devFileName, detectDevFileFunc)
}

func detectDevFileInner(t *testing.T, devFileName string, detectFuncInner func([]model.DevFileType) (int, error)) {
	devFileTypes := getDevFileTypes()
	devFileTarget, err := detectFuncInner(devFileTypes)
	if err != nil {
		t.Error(err)
	}

	if devFileTypes[devFileTarget].Name != devFileName {
		t.Error("Expected value " + devFileName + " but it was" + devFileTypes[devFileTarget].Name)
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
	}
}
