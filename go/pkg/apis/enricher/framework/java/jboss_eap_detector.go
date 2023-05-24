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
	"path/filepath"

	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	"github.com/redhat-developer/alizer/go/pkg/utils"
)

type JBossEAPDetector struct{}

func (o JBossEAPDetector) GetSupportedFrameworks() []string {
	return []string{"JBoss EAP"}
}

// DoFrameworkDetection uses the groupId and artifactId to check for the framework name
func (o JBossEAPDetector) DoFrameworkDetection(language *model.Language, config string) {
	if hasFwk, _ := hasFramework(config, "org.jboss.eap.plugins", "eap-maven-plugin"); hasFwk {
		language.Frameworks = append(language.Frameworks, "JBoss EAP")
	}
}

func (o JBossEAPDetector) DoPortsDetection(component *model.Component, ctx *context.Context) {
	var ports []int
	jbossCli := filepath.Join(component.Path, "src/main/resources/bin/jboss-cli.xml")
	jbossConfig, err := utils.GetJbossCLIFileContent(jbossCli)
	if err != nil {
		return
	}
	// Check if default controller is declared
	if port, err := utils.GetValidPort(jbossConfig.DefaultController.Port); err == nil {
		ports = append(ports, port)
	}
	for _, controller := range jbossConfig.Controllers.Controller {
		if port, err := utils.GetValidPort(controller.Port); err == nil {
			ports = append(ports, port)
		}
	}
	if len(ports) > 0 {
		component.Ports = ports
		return
	}
}
