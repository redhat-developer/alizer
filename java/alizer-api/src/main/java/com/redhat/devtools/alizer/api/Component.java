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

public class Component {

    private String path;
    private List<Language> languages;
    private DevfileType devfileType;

    public Component(String path, List<Language> languages) {
        this.path = path;
        this.languages = languages;
    }

    public String getPath() {
        return path;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public DevfileType getDevfileType() {
        return devfileType;
    }

    public void setDevfileType(DevfileType devfileType) {
        this.devfileType = devfileType;
    }
}
