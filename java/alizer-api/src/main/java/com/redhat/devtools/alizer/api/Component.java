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

import java.nio.file.Path;
import java.util.List;

public class Component {

    private Path path;
    private List<Language> languages;
    private DevfileType devfileType;
    private Path devfile;

    public Component(Path path, List<Language> languages, DevfileType devfileType) {
        this(path, languages, null, devfileType);
    }

    public Component(Path path, List<Language> languages, Path devfile) {
        this(path, languages, devfile, null);
    }

    public Component(Path path, List<Language> languages, Path devfile, DevfileType devfileType) {
        this.path = path;
        this.languages = languages;
        this.devfile = devfile;
        this.devfileType = devfileType;
    }

    public Path getPath() {
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

    public Path getDevfile() {
        return devfile;
    }
}
