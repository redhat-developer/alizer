/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc.
 ******************************************************************************/
package com.redhat.devtools.recognizer.api;

import java.util.Collections;
import java.util.List;

public class Language {

    private String name;
    private List<String> aliases;
    private double usageInPercentage;
    private List<String> frameworks;
    private List<String> tools;

    public Language(String name, List<String> aliases, double usageInPercentage) {
        this(name, aliases, usageInPercentage, Collections.emptyList(), Collections.emptyList());
    }

    public Language(String name, List<String> aliases, double usageInPercentage, List<String> frameworks, List<String> tools) {
        this.name = name;
        this.aliases = aliases;
        this.usageInPercentage = usageInPercentage;
        this.frameworks = frameworks;
        this.tools = tools;
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() { return aliases; }

    public double getUsageInPercentage() {
        return usageInPercentage;
    }

    public List<String> getFrameworks() {
        return frameworks;
    }

    public void setFrameworks(List<String> frameworks) {
        this.frameworks = frameworks;
    }

    public List<String> getTools() {
        return tools;
    }

    public void setTools(List<String> tools) {
        this.tools = tools;
    }
}
