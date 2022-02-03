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

	"github.com/redhat-developer/alizer/pkg/apis/recognizer"
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

func detectDevFile(t *testing.T, projectName string, devFileName string) {
	devFileTypes := getDevFileTypes()

	testingProjectPath := GetTestProjectPath(projectName)

	devFileType, err := recognizer.SelectDevFileFromTypes(testingProjectPath, devFileTypes)
	if err != nil {
		t.Error(err)
	}

	if devFileType.Name != devFileName {
		t.Error("Expected value " + devFileName + " but it was" + devFileType.Name)
	}
}

func getDevFileTypes() []recognizer.DevFileType {
	return []recognizer.DevFileType{
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
	}
}
