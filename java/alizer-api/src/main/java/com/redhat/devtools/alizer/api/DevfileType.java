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
<<<<<<< HEAD:java/alizer-api/src/main/java/com/redhat/devtools/alizer/api/DevfileType.java
package com.redhat.devtools.alizer.api;

import java.util.List;

public interface DevfileType {


    String getName();

    String getLanguage();

    String getProjectType();

    List<String> getTags();
}

=======
package model

import "regexp"

type Language struct {
	Name              string
	Aliases           []string
	UsageInPercentage float64
	Frameworks        []string
	Tools             []string
	CanBeComponent    bool
}

type Component struct {
	Path      string
	Languages []Language
	Ports     []int
}

type DevFileType struct {
	Name        string
	Language    string
	ProjectType string
	Tags        []string
}

type ApplicationFileInfo struct {
	Dir  string
	File string
}

type PortMatchRules struct {
	MatchIndexRegexes []PortMatchRule
	MatchRegexes      []PortMatchSubRule
}

type PortMatchRule struct {
	Regex     *regexp.Regexp
	ToReplace string
}

type PortMatchSubRule struct {
	Regex    *regexp.Regexp
	SubRegex *regexp.Regexp
}
>>>>>>> feat: add port detection (#81):go/pkg/apis/model/model.go
