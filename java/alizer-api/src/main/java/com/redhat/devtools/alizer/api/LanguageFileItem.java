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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LanguageFileItem {
    private String name;
    private List<String> aliases;
    private String type;
    private String group;
    private List<String> configurationFiles, excludeFolders;
    private boolean canBeComponent;
    private boolean disabled;

    public LanguageFileItem(String name, List<String> aliases, String type, String group) {
        this.name = name;
        this.aliases = aliases;
        this.type = type;
        this.group = group;
        this.configurationFiles = new ArrayList<>();
        this.excludeFolders = new ArrayList<>();
        this.canBeComponent = false;
        this.disabled = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAliases() { return aliases; }

    public void addAliases(List<String> aliasesToBeAdded) {
        if (aliasesToBeAdded.isEmpty()) {
            return;
        }

        if (aliases == null) {
            aliases = new ArrayList<>();
        }

        Set<String> aliasesAsSet = new HashSet<>(aliases);
        aliasesAsSet.addAll(aliasesToBeAdded);
        aliases = new ArrayList<>(aliasesAsSet);
    }

    public String getGroup() {
        return group;
    }

    public String getType() {
        return type;
    }

    public List<String> getConfigurationFiles() {
        return configurationFiles;
    }

    public void setConfigurationFiles(List<String> configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    public List<String> getExcludeFolders() {
        return excludeFolders;
    }

    public void setExcludeFolders(List<String> excludeFolders) {
        this.excludeFolders = excludeFolders;
    }

    public boolean canBeComponent() {
        return canBeComponent;
    }

    public void setCanBeComponent(boolean canBeComponent) {
        this.canBeComponent = canBeComponent;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
