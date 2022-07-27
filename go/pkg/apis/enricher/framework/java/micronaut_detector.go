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
	"os"

	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	utils "github.com/redhat-developer/alizer/go/pkg/utils"
	"gopkg.in/yaml.v3"
)

type MicronautDetector struct{}

type MicronautApplicationProps struct {
	Micronaut struct {
		Server struct {
			Port int `yaml:"port,omitempty"`
		} `yaml:"server,omitempty"`
	} `yaml:"micronaut,omitempty"`
}

func (m MicronautDetector) GetSupportedFrameworks() []string {
	return []string{"Micronaut"}
}

func (m MicronautDetector) DoFrameworkDetection(language *model.Language, config string) {
	if hasFwk, _ := hasFramework(config, "io.micronaut"); hasFwk {
		language.Frameworks = append(language.Frameworks, "Micronaut")
	}
}

func (m MicronautDetector) DoPortsDetection(component *model.Component) {
	// check if port is set on env var
	portValue := os.Getenv("MICRONAUT_SERVER_PORT")
	if port, err := utils.GetValidPort(portValue); err == nil {
		component.Ports = []int{port}
		return
	}

	bytes, err := utils.ReadAnyApplicationFile(component.Path, []model.ApplicationFileInfo{
		{
			Dir:  "src/main/resources",
			File: "application.yml",
		},
		{
			Dir:  "src/main/resources",
			File: "application.yaml",
		},
	})
	if err != nil {
		return
	}
	var data MicronautApplicationProps
	yaml.Unmarshal(bytes, &data)
	if utils.IsValidPort(data.Micronaut.Server.Port) {
		component.Ports = []int{data.Micronaut.Server.Port}
	}
}
