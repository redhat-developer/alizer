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
	"strings"

	"github.com/redhat-developer/alizer/go/pkg/utils"
)

type ApplicationPropertiesFile struct {
	Dir  string
	File string
}

// hasFramework uses the build.gradle, groupId, and artifactId to check for framework
func hasFramework(configFile, groupId, artifactId string) (bool, error) {
	if utils.IsPathOfWantedFile(configFile, "build.gradle") {
		return utils.IsTagInFile(configFile, groupId)
	} else if artifactId != "" {
		return utils.IsTagInPomXMLFileArtifactId(configFile, groupId, artifactId)
	} else {
		return utils.IsTagInPomXMLFile(configFile, groupId)
	}
}

// hasPackageScripts checks the pom.xml for any packaging scripts under profiles section. Returns
// a bolean whether this script was found or not and the relative path of this file
func hasPackageScripts(pomXML, groupId, artifactId string) (hasPkgScript bool, filepath string) {
	pom, err := utils.GetPomFileContent(pomXML)
	if err != nil {
		return false, ""
	}
	for _, profile := range pom.Profiles.Profile {
		for _, plugin := range profile.Build.Plugins.Plugin {
			if !(strings.Contains(plugin.ArtifactId, artifactId) && strings.Contains(plugin.GroupId, groupId)) {
				continue
			}
			for _, packagingScript := range plugin.Configuration.PackagingScripts.PackagingScript {
				for _, script := range packagingScript.Scripts.Script {
					if strings.Contains(script, "${basedir}/") {
						return true, strings.ReplaceAll(script, "${basedir}/", "")
					}
				}
			}
		}
	}
	return false, ""
}
