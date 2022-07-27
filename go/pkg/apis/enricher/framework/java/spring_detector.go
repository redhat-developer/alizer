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
	"errors"
	"io/ioutil"
	"os"
	"path/filepath"

	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	utils "github.com/redhat-developer/alizer/go/pkg/utils"
	"gopkg.in/yaml.v3"
)

type SpringDetector struct{}

type ApplicationProsServer struct {
	Server Server `yaml:"server,omitempty"`
}

type Server struct {
	Port int `yaml:"port,omitempty"`
}

func (s SpringDetector) GetSupportedFrameworks() []string {
	return []string{"Spring"}
}

func (s SpringDetector) DoFrameworkDetection(language *model.Language, config string) {
	if hasFwk, _ := hasFramework(config, "org.springframework"); hasFwk {
		language.Frameworks = append(language.Frameworks, "Spring")
	}
}

func (s SpringDetector) DoPortsDetection(component *model.Component) {
	// check if port is set on env var
	portValue := os.Getenv("SERVER_PORT")
	if port, err := utils.GetValidPort(portValue); err == nil {
		component.Ports = []int{port}
		return
	}

	applicationFile := utils.GetAnyApplicationFilePath(component.Path, []model.ApplicationFileInfo{
		{
			Dir:  "src/main/resources",
			File: "application.properties",
		},
		{
			Dir:  "src/main/resources",
			File: "application.yml",
		},
		{
			Dir:  "src/main/resources",
			File: "application.yaml",
		},
	})
	if applicationFile == "" {
		return
	}
	var err error
	var port int
	if filepath.Ext(applicationFile) == ".yml" || filepath.Ext(applicationFile) == ".yaml" {
		port, err = getServerPortFromYamlFile(applicationFile)
	} else {
		port, err = getServerPortFromPropertiesFile(applicationFile)
	}
	if err != nil {
		return
	}
	component.Ports = []int{port}

}

func getServerPortFromPropertiesFile(file string) (int, error) {
	props, err := utils.ConvertPropertiesFileAsPathToMap(file)
	if err != nil {
		return -1, err
	}
	if portValue, exists := props["server.port"]; exists {
		if port, err := utils.GetValidPort(portValue); err == nil {
			return port, nil
		}
	}
	return -1, errors.New("no port found")
}

func getServerPortFromYamlFile(file string) (int, error) {
	yamlFile, err := ioutil.ReadFile(file)
	if err != nil {
		return -1, err
	}
	var data ApplicationProsServer
	yaml.Unmarshal(yamlFile, &data)
	if data.Server.Port > 0 {
		return data.Server.Port, nil
	}
	return -1, errors.New("no port found")
}
