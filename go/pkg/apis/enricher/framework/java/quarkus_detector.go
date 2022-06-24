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
	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	utils "github.com/redhat-developer/alizer/go/pkg/utils"
)

type QuarkusDetector struct{}

func (q QuarkusDetector) GetSupportedFrameworks() []string {
	return []string{"Quarkus"}
}

func (q QuarkusDetector) DoFrameworkDetection(language *model.Language, config string) {
	if hasFwk, _ := hasFramework(config, "io.quarkus"); hasFwk {
		language.Frameworks = append(language.Frameworks, "Quarkus")
	}
}

func (q QuarkusDetector) DoPortsDetection(component *model.Component) {
	bytes, err := utils.ReadAnyApplicationFile(component.Path, []model.ApplicationFileInfo{
		{
			Dir:  "src/main/resources",
			File: "application.properties",
		},
	})
	if err != nil {
		return
	}
	props, err := utils.ConvertPropertiesFileToMap(bytes)
	if err != nil {
		return
	}
	if portValue, exists := props["quarkus.http.port"]; exists {
		if port, err := utils.GetValidPort(portValue); err == nil {
			component.Ports = []int{port}
		}
	}
}
