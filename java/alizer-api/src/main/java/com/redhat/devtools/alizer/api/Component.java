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

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class Component {

    private Path path;
    private File config;
    private List<Language> languages;

    public Component(Path path, List<Language> languages) {
        this(path, null, languages);
    }

    public Component(Path path, File config, List<Language> languages) {
        this.path = path;
        this.config = config;
        this.languages = languages;
    }

    public Path getPath() {
        return path;
    }

    public File getConfig() {
        return config;
    }

    public List<Language> getLanguages() {
        return languages;
    }
}
