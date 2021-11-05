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
package com.redhat.devtools.alizer.api.spi.framework.nodejs;

import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorWithConfigFileProvider;
import com.redhat.devtools.alizer.api.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


import static com.redhat.devtools.alizer.api.Constants.JAVASCRIPT;
import static com.redhat.devtools.alizer.api.Constants.TYPESCRIPT;

public abstract class NodeJsFrameworkDetectorProvider implements FrameworkDetectorWithConfigFileProvider {
    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(JAVASCRIPT, TYPESCRIPT);
    }

    protected abstract String getFrameworkTag();

    @Override
    public boolean hasFramework(File file) throws IOException {
        return getDependenciesFromPackageJson(file).keySet().stream().anyMatch(dependency -> dependency.equals(getFrameworkTag()));
    }

    private Map<?, ?> getDependenciesFromPackageJson(File file) throws IOException {
        Map<?, ?> packageJsonContent = Utils.getJsonFileAsMap(file);
        if (!packageJsonContent.containsKey("dependencies")) {
            return Collections.emptyMap();
        }
        return (Map<?, ?>) packageJsonContent.get("dependencies");
    }
}