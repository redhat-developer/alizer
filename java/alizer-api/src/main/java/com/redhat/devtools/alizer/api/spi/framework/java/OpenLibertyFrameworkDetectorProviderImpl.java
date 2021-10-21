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
package com.redhat.devtools.alizer.api.spi.framework.java;

import com.redhat.devtools.alizer.api.Service;
import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorWithConfigFileProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;


import static com.redhat.devtools.alizer.api.Constants.LIBERTY;

public class OpenLibertyFrameworkDetectorProviderImpl extends JavaFrameworkDetectorProvider {
    private static final String LIBERTY_TAG = "io.openliberty";

    @Override
    public FrameworkDetectorWithConfigFileProvider create() {
        return new OpenLibertyFrameworkDetectorProviderImpl();
    }

    @Override
    public List<String> getFrameworks() {
        return Arrays.asList(LIBERTY);
    }

    @Override
    protected String getFrameworkTag() {
        return LIBERTY_TAG;
    }

    @Override
    public List<Service> getServices(Path root, Path configFile) throws IOException {
        return getServices(configFile, getFrameworks());
    }
}
