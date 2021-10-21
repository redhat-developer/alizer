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

    private Path root, configFile;
    private List<Language> languages;

    public Component(Path root, List<Language> languages) {
        this(root, null, languages);
    }

    public Component(Path root, Path configFile, List<Language> languages) {
        this.root = root;
        this.configFile = configFile;
        this.languages = languages;
    }

    public Path getRoot() {
        return root;
    }

    public Path getConfigFile() {
        return configFile;
    }

    public List<Language> getLanguages() {
        return languages;
    }
}
