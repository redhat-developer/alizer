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

type NuxtDetector struct{}

func (n NuxtDetector) GetSupportedFrameworks() []string {
	return []string{"Nuxt"}
}

func (n NuxtDetector) DoFrameworkDetection(language *model.Language, config string) {
	if hasFramework(config, "nuxt") {
		language.Frameworks = append(language.Frameworks, "Nuxt", "Nuxt.js")
	}
}

func (n NuxtDetector) DoPortsDetection(component *model.Component) {
	// check if port is set in start script in package.json
	port := getPortFromStartScript(component.Path, `--port=(\d*)`)
	if utils.IsValidPort(port) {
		component.Ports = []int{port}
	}

	// check if port is set in dev script in package.json
	port = getPortFromDevScript(component.Path, `--port=(\d*)`)
	if utils.IsValidPort(port) {
		component.Ports = []int{port}
	}
}
