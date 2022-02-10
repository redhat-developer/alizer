/*******************************************************************************
 * Copyright (c) 2022 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc.
 ******************************************************************************/
package com.redhat.devtools.alizer.api.spi.framework.go;

import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorWithConfigFileProvider;
import com.redhat.devtools.alizer.api.utils.DocumentParser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.redhat.devtools.alizer.api.Constants.GO;

public abstract class GoFrameworkDetectorProvider implements FrameworkDetectorWithConfigFileProvider {

    protected abstract String getFrameworkTag();

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(GO);
    }

    @Override
    public boolean hasFramework(File file) throws IOException {
        return DocumentParser.isTagInFile(file, getFrameworkTag());
    }
}
