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
	"os"
	"path/filepath"
	"regexp"

	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	"github.com/redhat-developer/alizer/go/pkg/utils"
)

type WildFlyDetector struct{}

func (o WildFlyDetector) GetSupportedFrameworks() []string {
	return []string{"WildFly"}
}

// DoFrameworkDetection uses the groupId and artifactId to check for the framework name
func (o WildFlyDetector) DoFrameworkDetection(language *model.Language, config string) {
	if hasFwk, _ := hasFramework(config, "org.wildfly.plugins", "wildfly-maven-plugin"); hasFwk {
		language.Frameworks = append(language.Frameworks, "WildFly")
	}
}

// DoPortsDetection for wildfly fetches the pom.xml and tries to find any packaging-script under
// the wildfly-maven-plugin. If there is one it looks for a new-socket-binding where a port is defined.
func (o WildFlyDetector) DoPortsDetection(component *model.Component, ctx *context.Context) {
	paths, err := utils.GetCachedFilePathsFromRoot(component.Path, ctx)
	if err != nil {
		return
	}
	pomXML := utils.GetFile(&paths, "pom.xml")
	if hasPkgScript, filePath := hasPackageScripts(pomXML, "org.wildfly.plugins", "wildfly-maven-plugin"); hasPkgScript {
		configScript := utils.GetFile(&paths, filePath)
		cleanConfigScript := filepath.Clean(configScript)
		bytes, err := os.ReadFile(cleanConfigScript)
		if err != nil {
			return
		}
		matchIndexRegexes := []model.PortMatchRule{
			{
				Regex:     regexp.MustCompile(`new-socket-binding:add\([^)]*`),
				ToReplace: "new-socket-binding:add",
			},
		}
		if err != nil {
			return
		}
		ports := utils.GetPortFromFilePackagingScript(matchIndexRegexes, string(bytes))
		if len(ports) > 0 {
			component.Ports = ports
			return
		}
	}
}
