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
	"context"
	"encoding/xml"
	"strings"

	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	utils "github.com/redhat-developer/alizer/go/pkg/utils"
)

type WildFlyDetector struct{}

type Wildfly_Standalone_Xml struct {
	SocketBindingGroup struct {
		SocketBinding []struct {
			Name string `xml:"name,attr"`
			Port string `xml:"port,attr"`
		} `xml:"socket-binding"`
	} `xml:"socket-binding-group"`
}

func (o WildFlyDetector) GetSupportedFrameworks() []string {
	return []string{"WildFly"}
}

func (o WildFlyDetector) DoFrameworkDetection(language *model.Language, config string) {
	if hasFwk, _ := hasFramework(config, "org.wildfly.plugins"); hasFwk {
		language.Frameworks = append(language.Frameworks, "WildFly")
	}
}

func (o WildFlyDetector) DoPortsDetection(component *model.Component, ctx *context.Context) {
	bytes, err := utils.ReadAnyApplicationFile(component.Path, []model.ApplicationFileInfo{
		{
			Dir:  "",
			File: "standalone.xml",
		},
	}, ctx)
	if err != nil {
		return
	}
	var (
		data       Wildfly_Standalone_Xml
		foundPorts []string
	)
	xml.Unmarshal(bytes, &data)
	filters := [3]string{"${jboss.http.port:", "${jboss.https.port:", "}"}
	for _, potentialPort := range data.SocketBindingGroup.SocketBinding {
		if potentialPort.Name == "https" || potentialPort.Name == "http" {
			tmpPort := potentialPort.Port
			for _, filter := range filters {
				tmpPort = strings.ReplaceAll(tmpPort, filter, "")
			}
			foundPorts = append(foundPorts, tmpPort)
		}
	}
	ports := utils.GetValidPorts(foundPorts)
	if len(ports) > 0 {
		component.Ports = ports
	}
}
