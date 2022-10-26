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
	"github.com/redhat-developer/alizer/go/pkg/utils"
)

type NextDetector struct{}

func (n NextDetector) GetSupportedFrameworks() []string {
	return []string{"Next"}
}

func (n NextDetector) DoFrameworkDetection(language *model.Language, config string) {
	if hasFramework(config, "next") {
		language.Frameworks = append(language.Frameworks, "Next")
	}
}

func (n NextDetector) DoPortsDetection(component *model.Component) {
	// check if port is set in start script in package.json
	port := getPortFromStartScript(component.Path, `-p (\d*)`)
	if utils.IsValidPort(port) {
		component.Ports = []int{port}
	}
}