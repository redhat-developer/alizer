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
package enricher

import (
	"os"
	"path/filepath"
	"regexp"

	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	"github.com/redhat-developer/alizer/go/pkg/utils"
)

type ReactJsDetector struct{}

func (r ReactJsDetector) GetSupportedFrameworks() []string {
	return []string{"React"}
}

func (r ReactJsDetector) DoFrameworkDetection(language *model.Language, config string) {
	if hasFramework(config, "react") {
		language.Frameworks = append(language.Frameworks, "React")
	}
}

func (r ReactJsDetector) DoPortsDetection(component *model.Component) {
	// check if port is set on env var
	portValue := os.Getenv("PORT")
	if port, err := utils.GetValidPort(portValue); err == nil {
		component.Ports = []int{port}
		return
	}
	// check if port is set on .env file
	port := getPortFromEnvFile(component.Path)
	if utils.IsValidPort(port) {
		component.Ports = []int{port}
		return
	}
	// check if port is set in start script in package.json
	port = getPortFromStartScript(component.Path)
	if utils.IsValidPort(port) {
		component.Ports = []int{port}
		return
	}
}

func getPortFromEnvFile(root string) int {
	envPath := filepath.Join(root, ".env")
	bytes, err := os.ReadFile(envPath)
	if err != nil {
		return -1
	}
	return getReactPortSubmatch(string(bytes))
}

func getPortFromStartScript(root string) int {
	packageJsonPath := filepath.Join(root, "package.json")
	packageJson, err := utils.GetPackageJsonSchemaFromFile(packageJsonPath)
	if err != nil {
		return -1
	}
	return getReactPortSubmatch(packageJson.Scripts.Start)
}

func getReactPortSubmatch(text string) int {
	re := regexp.MustCompile(`PORT=(\d*)`)
	return utils.FindPortSubmatch(re, text, 1)
}
