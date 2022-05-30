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
package recognizer

import (
	"strconv"
	"strings"
	"testing"

	"github.com/redhat-developer/alizer/go/pkg/apis/recognizer"
)

func TestComponentDetectionOnMicronaut(t *testing.T) {
	isComponentsInProject(t, "micronaut", 1, "java")
}

func TestComponentDetectionOnQuarkus(t *testing.T) {
	isComponentsInProject(t, "quarkus", 1, "java")
}

func TestComponentDetectionOnJavascript(t *testing.T) {
	isComponentsInProject(t, "nodejs-ex", 1, "javascript")
}

func TestComponentDetectionOnDjango(t *testing.T) {
	isComponentsInProject(t, "django", 1, "python")
}

func TestComponentDetectionNoResult(t *testing.T) {
	components := getComponentsFromProject(t, "simple")
	if len(components) > 0 {
		t.Errorf("Expected 0 components but found " + strconv.Itoa(len(components)))
	}
}

func TestComponentDetectionOnDoubleComponents(t *testing.T) {
	isComponentsInProject(t, "double-components", 2, "javascript")
}

func TestComponentDetectionMultiProjects(t *testing.T) {
	components := getComponentsFromProject(t, "")
	nComps := 13
	if len(components) != nComps {
		t.Errorf("Expected " + strconv.Itoa(nComps) + " components but found " + strconv.Itoa(len(components)))
	}
}

func getComponentsFromProject(t *testing.T, project string) []recognizer.Component {
	testingProjectPath := GetTestProjectPath(project)

	components, err := recognizer.DetectComponents(testingProjectPath)
	if err != nil {
		t.Error(err)
	}

	return components
}

func isComponentsInProject(t *testing.T, project string, expectedNumber int, expectedLanguage string) {
	components := getComponentsFromProject(t, project)
	hasComponents := len(components) == expectedNumber
	if hasComponents {
		isExpectedComponent := strings.EqualFold(expectedLanguage, components[0].Languages[0].Name)
		if !isExpectedComponent {
			t.Errorf("Project does not use " + expectedLanguage + " language")
		}
	} else {
		t.Errorf("Expected " + strconv.Itoa(expectedNumber) + " of components but it was " + strconv.Itoa(len(components)))
	}
}
