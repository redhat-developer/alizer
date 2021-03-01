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
package com.redhat.devtools.recognizer.api;

import java.util.List;

public class DevfileType {

    private String name, language;
    private List<String> tags;

    public DevfileType() {

    }

    public DevfileType(String name, String language, List<String> tags) {
        this.name = name;
        this.language = language;
        this.tags = tags;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public String getLanguage() {
        return language;
    }

    public List<String> getTags() {
        return tags;
    }
}

