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

type QuarkusDetector struct{}

type QuarkusApplicationYaml struct {
	Quarkus QuarkusHttp `yaml:"quarkus,omitempty"`
}

type QuarkusHttp struct {
	Http QuarkusHttpPort `yaml:"http,omitempty"`
}

type QuarkusHttpPort struct {
	Port int `yaml:"port,omitempty"`
}

func (q QuarkusDetector) GetSupportedFrameworks() []string {
	return []string{"Quarkus"}
}

func (q QuarkusDetector) DoFrameworkDetection(language *model.Language, config string) {
	if hasFwk, _ := hasFramework(config, "io.quarkus"); hasFwk {
		language.Frameworks = append(language.Frameworks, "Quarkus")
	}
}

func (q QuarkusDetector) DoPortsDetection(component *model.Component) {
	// check if port is set on env var
	portValue := os.Getenv("QUARKUS_HTTP_PORT")
	if port, err := utils.GetValidPort(portValue); err == nil {
		component.Ports = []int{port}
		return
	}
	// check if port is set on .env file
	port := utils.GetValueFromEnvFile(component.Path, `QUARKUS_HTTP_PORT=(\d*)`)
	if utils.IsValidPort(port) {
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
	if filepath.Ext(applicationFile) == ".yml" || filepath.Ext(applicationFile) == ".yaml" {
		port, err = getServerPortFromQuarkusApplicationYamlFile(applicationFile)
	} else {
		port, err = getServerPortFromQuarkusPropertiesFile(applicationFile)
	}
	if err != nil {
		return
	}
	component.Ports = []int{port}
}

func getServerPortFromQuarkusPropertiesFile(file string) (int, error) {
	props, err := utils.ConvertPropertiesFileAsPathToMap(file)
	if err != nil {
		return -1, err
	}
	if portValue, exists := props["quarkus.http.port"]; exists {
		if port, err := utils.GetValidPort(portValue); err == nil {
			return port, nil
		}
	}
	return -1, errors.New("no port found")
}

func getServerPortFromQuarkusApplicationYamlFile(file string) (int, error) {
	yamlFile, err := ioutil.ReadFile(file)
	if err != nil {
		return -1, err
	}
	var data QuarkusApplicationYaml
	yaml.Unmarshal(yamlFile, &data)
	if data.Quarkus.Http.Port > 0 {
		return data.Quarkus.Http.Port, nil
	}
	return -1, errors.New("no port found")
}
