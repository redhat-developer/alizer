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
	"context"
	"os"
	"path/filepath"
	"strconv"
	"strings"
	"testing"

	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	"github.com/redhat-developer/alizer/go/pkg/apis/recognizer"
	"github.com/redhat-developer/alizer/go/pkg/utils"
)

func TestComponentDetectionOnMicronaut(t *testing.T) {
	isComponentsInProject(t, "micronaut", 1, "java", "myMicronautProject")
}

func TestComponentDetectionOnWildFly(t *testing.T) {
	isComponentsInProject(t, "wildfly", 1, "java", "wildfly")
}

func TestComponentDetectionOnJBossEAP(t *testing.T) {
	isComponentsInProject(t, "jboss-eap", 1, "java", "jboss-eap")
}

func TestComponentDetectionOnQuarkus(t *testing.T) {
	isComponentsInProject(t, "quarkus", 1, "java", "code-with-quarkus-maven")
}

func TestComponentDetectionOnJavascript(t *testing.T) {
	isComponentsInProject(t, "nodejs-ex", 1, "javascript", "nodejs-starter")
}

func TestComponentDetectionOnDjango(t *testing.T) {
	isComponentsInProject(t, "django", 1, "python", "django")
}

func TestComponentDetectionOnDotNet(t *testing.T) {
	isComponentsInProject(t, "s2i-dotnetcore-ex", 1, "c#", "app")
}

func TestComponentDetectionOnFSharp(t *testing.T) {
	isComponentsInProject(t, "net-fsharp", 1, "f#", "net-fsharp")
}

func TestComponentDetectionOnVBNet(t *testing.T) {
	isComponentsInProject(t, "net-vb", 1, "Visual Basic .NET", "net-vb")
}

func TestComponentDetectionOnGoLang(t *testing.T) {
	isComponentsInProject(t, "golang-gin-app", 1, "Go", "golang-gin-app")
}

func TestComponentDetectionOnAngular(t *testing.T) {
	isComponentsInProject(t, "angularjs", 1, "typescript", "angularjs")
}

func TestComponentDetectionOnNextJs(t *testing.T) {
	isComponentsInProject(t, "nextjs-app", 1, "typescript", "nextjs-app")
}

func TestComponentDetectionOnNuxtJs(t *testing.T) {
	isComponentsInProject(t, "nuxt-app", 1, "typescript", "nuxt-app")
}

func TestComponentDetectionOnSvelteJs(t *testing.T) {
	isComponentsInProject(t, "svelte-app", 1, "javascript", "svelte-app")
}

func TestComponentDetectionOnVue(t *testing.T) {
	isComponentsInProject(t, "vue-app", 1, "typescript", "vue-app")
}

func TestComponentDetectionNoResult(t *testing.T) {
	components := getComponentsFromProject(t, "simple")
	if len(components) > 0 {
		t.Errorf("Expected 0 components but found " + strconv.Itoa(len(components)))
	}
}

func TestComponentDetectionOnDoubleComponents(t *testing.T) {
	isComponentsInProject(t, "double-components", 2, "javascript", "")
}

func TestComponentDetectionWithGitIgnoreRule(t *testing.T) {
	testingProjectPath := GetTestProjectPath("component-wrapped-in-folder")
	settings := model.DetectionSettings{
		BasePath: testingProjectPath,
	}
	ctx := context.Background()
	files, err := utils.GetCachedFilePathsFromRoot(testingProjectPath, &ctx)
	if err != nil {
		t.Error(err)
	}

	components := getComponentsFromFiles(t, files, settings)

	if len(components) != 1 {
		t.Errorf("Expected 1 components but found " + strconv.Itoa(len(components)))
	}

	//now add a gitIgnore with a rule to exclude the only component found
	gitIgnorePath := filepath.Join(testingProjectPath, ".gitignore")
	err = updateContent(gitIgnorePath, []byte("**/quarkus/"))
	if err != nil {
		t.Error(err)
	}
	files, err = utils.GetFilePathsFromRoot(testingProjectPath)
	if err != nil {
		t.Error(err)
	}
	componentsWithUpdatedGitIgnore := getComponentsFromFiles(t, files, settings)
	//delete gitignore file
	os.Remove(gitIgnorePath)

	if len(componentsWithUpdatedGitIgnore) != 0 {
		t.Errorf("Expected 0 components but found " + strconv.Itoa(len(componentsWithUpdatedGitIgnore)))
	}
}

func updateContent(filePath string, data []byte) error {
	f, err := os.OpenFile(filePath, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		return err
	}
	defer f.Close()
	if _, err := f.Write(data); err != nil {
		return err
	}
	return nil
}

func TestComponentDetectionMultiProjects(t *testing.T) {
	components := getComponentsFromProject(t, "")
	nComps := 21
	if len(components) != nComps {
		t.Errorf("Expected " + strconv.Itoa(nComps) + " components but found " + strconv.Itoa(len(components)))
	}
}

func getComponentsFromProject(t *testing.T, project string) []model.Component {
	testingProjectPath := GetTestProjectPath(project)

	return getComponentsFromProjectInner(t, testingProjectPath)
}

func getComponentsFromPortProject(t *testing.T, project string) []model.Component {
	testingProjectPath := GetTestPortProjectPath(project)

	return getComponentsFromProjectInner(t, testingProjectPath)
}

func getComponentsFromProjectInner(t *testing.T, testingProjectPath string) []model.Component {
	components, err := recognizer.DetectComponents(testingProjectPath)
	if err != nil {
		t.Error(err)
	}

	return components
}

func getComponentsFromFiles(t *testing.T, files []string, settings model.DetectionSettings) []model.Component {
	ctx := context.Background()
	return recognizer.DetectComponentsFromFilesList(files, settings, &ctx)
}

func isComponentsInProject(t *testing.T, project string, expectedNumber int, expectedLanguage string, expectedProjectName string) {
	components := getComponentsFromProject(t, project)
	verifyComponents(t, components, expectedNumber, expectedLanguage, expectedProjectName)
}

func verifyComponents(t *testing.T, components []model.Component, expectedNumber int, expectedLanguage string, expectedProjectName string) {
	hasComponents := len(components) == expectedNumber
	if hasComponents {
		isExpectedComponent := strings.EqualFold(expectedLanguage, components[0].Languages[0].Name)
		if !isExpectedComponent {
			t.Errorf("Project does not use " + expectedLanguage + " language")
		}
		if expectedProjectName != "" {
			isExpectedProjectName := strings.EqualFold(expectedProjectName, components[0].Name)
			if !isExpectedProjectName {
				t.Errorf("Main component has a different project name. Expected " + expectedProjectName + " but it was " + components[0].Name)
			}
		}
	} else {
		t.Errorf("Expected " + strconv.Itoa(expectedNumber) + " of components but it was " + strconv.Itoa(len(components)))
	}
}

func TestPortDetectionWithDockerComposeExpose(t *testing.T) {
	testPortDetectionInProject(t, "projectDockerComposeExpose", []int{3000, 8000})
}

func TestPortDetectionWithDockerComposeShortSyntaxPorts(t *testing.T) {
	testPortDetectionInProject(t, "projectDockerComposePortsShortSyntax", []int{3000, 1234})
}

func TestPortDetectionWithDockerComposeLongSyntaxPorts(t *testing.T) {
	testPortDetectionInProject(t, "projectDockerComposePortsLongSyntax", []int{6060})
}

func TestPortDetectionWithDockerFile(t *testing.T) {
	testPortDetectionInProject(t, "projectDockerFile", []int{8085})
}

func TestPortDetectionWithContainerFile(t *testing.T) {
	testPortDetectionInProject(t, "projectContainerFile", []int{8085})
}

func TestPortDetectionJavaMicronaut(t *testing.T) {
	testPortDetectionInProject(t, "projectMicronaut", []int{4444})
}

func TestPortDetectionJavaQuarkus(t *testing.T) {
	testPortDetectionInProject(t, "projectQuarkus", []int{9898})
}

func TestPortDetectionJavaQuarkusDefaultPort(t *testing.T) {
	testPortDetectionInProject(t, "projectQuarkusDefaultPort", []int{8080})
}

func TestPortDetectionJavaSpring(t *testing.T) {
	testPortDetectionInProject(t, "projectSpring", []int{9091})
}

func TestPortDetectionJavaSpringDefaultPort(t *testing.T) {
	testPortDetectionInProject(t, "projectSpringDefaultPort", []int{8080})
}

func TestPortDetectionJavaVertxHttpPort(t *testing.T) {
	testPortDetectionInProject(t, "projectVertxHttpPort", []int{2321})
}

func TestPortDetectionJavaVertxServerPort(t *testing.T) {
	testPortDetectionInProject(t, "projectVertxServerPort", []int{5555})
}

func TestPortDetectionJavascriptExpressClear(t *testing.T) {
	testPortDetectionInProject(t, "projectExpressClear", []int{7777})
}

func TestPortDetectionJavascriptExpressEnv(t *testing.T) {
	os.Setenv("TEST_EXPRESS_ENV", "1111")
	testPortDetectionInProject(t, "projectExpressEnv", []int{1111})
	os.Unsetenv("TEST_EXPRESS_ENV")
}

func TestPortDetectionJavascriptExpressVariable(t *testing.T) {
	testPortDetectionInProject(t, "projectExpressVariable", []int{3000})
}

func TestPortDetectionJavascriptReactEnvVariable(t *testing.T) {
	oldValue := os.Getenv("PORT")
	os.Setenv("PORT", "2121")
	testPortDetectionInProject(t, "projectReactEnv", []int{2121})
	if oldValue == "" {
		os.Unsetenv("PORT")
	} else {
		os.Setenv("PORT", oldValue)
	}
}

func TestPortDetectionJavascriptReactEnvFile(t *testing.T) {
	testPortDetectionInProject(t, "projectReactEnv", []int{1231})
}

func TestPortDetectionJavascriptReactScript(t *testing.T) {
	testPortDetectionInProject(t, "projectReactScript", []int{5353})
}

func TestPortDetectionDjango(t *testing.T) {
	testPortDetectionInProject(t, "projectDjango", []int{3543})
}

func TestPortDetectionGoGin(t *testing.T) {
	testPortDetectionInProject(t, "projectGin", []int{8789})
}

func TestPortDetectionGoFiber(t *testing.T) {
	testPortDetectionInProject(t, "projectGoFiber", []int{3000})
}

func TestPortDetectionGoMux(t *testing.T) {
	testPortDetectionInProject(t, "projectGoMux", []int{8000})
}

func TestPortDetectionAngularPortInStartScript(t *testing.T) {
	testPortDetectionInProject(t, "projectAngularjs", []int{8780})
}

func TestPortDetectionNextJsPortInStartScript(t *testing.T) {
	testPortDetectionInProject(t, "projectNextjs", []int{8610})
}

func TestPortDetectionNuxtJsPortInConfigFile(t *testing.T) {
	testPortDetectionInProject(t, "projectNuxt", []int{8787})
}

func TestPortDetectionSvelteJsPortInStartScript(t *testing.T) {
	testPortDetectionInProject(t, "projectSvelte", []int{8282})
}

func TestPortDetectionVuePortInStartScript(t *testing.T) {
	testPortDetectionInProject(t, "projectVue", []int{8282})
}

func testPortDetectionInProject(t *testing.T, project string, ports []int) {
	components := getComponentsFromPortProject(t, project)
	if len(components) == 0 {
		t.Errorf("No component detected")
	}

	portsDetected := components[0].Ports
	if len(portsDetected) != len(ports) {
		t.Errorf("Length of found ports and expected ports is different")
	}

	found := false
	for _, port := range ports {
		for _, portDetected := range portsDetected {
			if port == portDetected {
				found = true
				break
			}
		}
		if !found {
			t.Errorf("Port " + strconv.Itoa(port) + " have not been detected")
		}
		found = false
	}
}
