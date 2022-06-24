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
	"fmt"
	"io"
	"os"
	"path/filepath"
	"strconv"
	"strings"

	"github.com/moby/buildkit/frontend/dockerfile/parser"
	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	"github.com/redhat-developer/alizer/go/pkg/utils/langfiles"
)

type Enricher interface {
	GetSupportedLanguages() []string
	DoEnrichLanguage(language *model.Language, files *[]string)
	DoEnrichComponent(component *model.Component)
	IsConfigValidForComponentDetection(language string, configFile string) bool
}

type FrameworkDetectorWithConfigFile interface {
	GetSupportedFrameworks() []string
	DoFrameworkDetection(language *model.Language, config string)
	DoPortsDetection(component *model.Component)
}

type FrameworkDetectorWithoutConfigFile interface {
	GetSupportedFrameworks() []string
	DoFrameworkDetection(language *model.Language, files *[]string)
	DoPortsDetection(component *model.Component)
}

/*
	IsConfigurationValidForLanguage check whether the configuration file is valid for current language.
									For example when analyzing a nodejs project, we could find a package.json
									within the node_modules folder. That is not to be considered valid
									for component detection.
	Paramenters:
		language: language name
		file: configuration file name
	Returns:
		bool: true if config file is valid for current language

*/
func IsConfigurationValidForLanguage(language string, file string) bool {
	languageItem, err := langfiles.Get().GetLanguageByName(language)
	if err != nil {
		return false
	}
	for _, excludeFolder := range languageItem.ExcludeFolders {
		if isFolderNameIncludedInPath(file, excludeFolder) {
			return false
		}
	}
	return true
}

/*
	isFolderNameIncludedInPath check if fullpath contains potentialSubFolderName
	Parameters:
		fullPath: 				complete path of a file
		potentialSubFolderName: folder name
	Returns:
		bool: true if potentialSubFolderName is included in fullPath
*/
func isFolderNameIncludedInPath(fullPath string, potentialSubFolderName string) bool {
	pathSeparator := fmt.Sprintf("%c", os.PathSeparator)
	dir, _ := filepath.Split(fullPath)

	subDirectories := strings.Split(dir, pathSeparator)
	for _, subDir := range subDirectories {
		if strings.EqualFold(subDir, potentialSubFolderName) {
			return true
		}
	}
	return false
}

func getEnrichers() []Enricher {
	return []Enricher{
		&JavaEnricher{},
		&JavaScriptEnricher{},
		&PythonEnricher{},
		&DotNetEnricher{},
		&GoEnricher{},
	}
}

func GetEnricherByLanguage(language string) Enricher {
	for _, enricher := range getEnrichers() {
		if isLanguageSupportedByEnricher(language, enricher) {
			return enricher
		}
	}
	return nil
}

func isLanguageSupportedByEnricher(nameLanguage string, enricher Enricher) bool {
	for _, language := range enricher.GetSupportedLanguages() {
		if strings.EqualFold(language, nameLanguage) {
			return true
		}
	}
	return false
}

func GetDefaultProjectName(path string) string {
	return filepath.Base(path)
}

func GetPortsFromDockerFile(root string) []int {
	file, err := os.Open(filepath.Join(root, "Dockerfile"))
	if err == nil {
		return getPortsFromReader(file)
	}
	defer file.Close()
	return []int{}
}

func getPortsFromReader(file io.Reader) []int {
	ports := []int{}
	res, err := parser.Parse(file)
	if err != nil {
		return ports
	}

	for _, child := range res.AST.Children {
		if strings.ToLower(child.Value) == "expose" {
			for n := child.Next; n != nil; n = n.Next {
				if port, err := strconv.Atoi(n.Value); err == nil {
					ports = append(ports, port)
				}

			}
		}
	}
	return ports
}
