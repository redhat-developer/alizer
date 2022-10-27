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
package enricher

import (
	"path/filepath"
	"regexp"

	"github.com/redhat-developer/alizer/go/pkg/schema"
	utils "github.com/redhat-developer/alizer/go/pkg/utils"
)

func hasFramework(configFile string, tag string) bool {
	return utils.IsTagInPackageJsonFile(configFile, tag)
}

func getPortFromStartScript(root string, regex string) int {
	packageJson, err := getValueFromPackageJson(root)
	if err != nil {
		return -1
	}
	re := regexp.MustCompile(regex)
	return utils.FindPortSubmatch(re, packageJson.Scripts.Start, 1)
}

func getPortFromDevScript(root string, regex string) int {
	packageJson, err := getValueFromPackageJson(root)
	if err != nil {
		return -1
	}
	re := regexp.MustCompile(regex)
	return utils.FindPortSubmatch(re, packageJson.Scripts.Dev, 1)
}

func getValueFromPackageJson(root string) (schema.PackageJson, error) {
	packageJsonPath := filepath.Join(root, "package.json")
	return utils.GetPackageJsonSchemaFromFile(packageJsonPath)
}
