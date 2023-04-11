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
	utils "github.com/redhat-developer/alizer/go/pkg/utils"
)

type ApplicationPropertiesFile struct {
	Dir  string
	File string
}

func hasFramework(configFile string, tag string) (bool, error) {
	if utils.IsPathOfWantedFile(configFile, "build.gradle") {
		return utils.IsTagInFile(configFile, tag)
	} else if tagIsIn(tag) {
		return utils.IsTagInPomXMLFileArtifactId(configFile, tag)
	} else {
		return utils.IsTagInPomXMLFile(configFile, tag)
	}
}

func tagIsIn(tag string) bool {
	artifactIds := [2]string{"eap-maven-plugin", "wildfly-maven-plugin"}
	for _, artifactId := range artifactIds {
		if artifactId == tag {
			return true
		}
	}
	return false
}
