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
package com.redhat.devtools.alizer.api;

import java.util.List;

public class LanguageFileItem {
    private String name;
    private List<String> aliases;
    private String type;
    private String group;
    private List<String> configurationFiles, excludeFolders;

    public LanguageFileItem(String name, List<String> aliases, String type, String group, List<String> configurationFiles, List<String> excludeFolders) {
        this.name = name;
        this.aliases = aliases;
        this.type = type;
        this.group = group;
        this.configurationFiles = configurationFiles;
        this.excludeFolders = excludeFolders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAliases() { return aliases; }

    public String getGroup() {
        return group;
    }

    public String getType() {
        return type;
    }

    public List<String> getConfigurationFiles() {
        return configurationFiles;
    }

    public List<String> getExcludeFolders() {
        return excludeFolders;
    }
}
