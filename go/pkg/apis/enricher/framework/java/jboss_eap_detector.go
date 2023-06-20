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
	"regexp"
	"strings"

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
	ports := []int{}
	portPlaceholder := ""
	// Fetch the content of xml for this component
	paths, err := utils.GetCachedFilePathsFromRoot(component.Path, ctx)
	if err != nil {
		return
	}
	pomXML := utils.GetFile(&paths, "pom.xml")
	pom, err := utils.GetPomFileContent(pomXML)
	if err != nil {
		return
	}

	re := regexp.MustCompile(`jboss.http.port=\d*`)
	// Check for port configuration inside profiles
	for _, profile := range pom.Profiles.Profile {
		for _, plugin := range profile.Build.Plugins.Plugin {
			if !(strings.Contains(plugin.ArtifactId, "eap-maven-plugin") && strings.Contains(plugin.GroupId, "org.jboss.eap.plugins")) {
				continue
			}
			matchIndexesSlice := re.FindAllStringSubmatchIndex(plugin.Configuration.JavaOpts, -1)
			for _, matchIndexes := range matchIndexesSlice {
				if len(matchIndexes) > 1 {
					tempPortPlaceholder := plugin.Configuration.JavaOpts[matchIndexes[0]:matchIndexes[1]]
					portPlaceholder = strings.Replace(tempPortPlaceholder, `jboss.http.port=`, "", -1)
				}
			}
		}
	}

	if portPlaceholder == "" {
		return
	}

	if port, err := utils.GetValidPort(portPlaceholder); err == nil {
		ports = append(ports, port)
	}

	if len(ports) > 0 {
		component.Ports = ports
		return
	}
}
