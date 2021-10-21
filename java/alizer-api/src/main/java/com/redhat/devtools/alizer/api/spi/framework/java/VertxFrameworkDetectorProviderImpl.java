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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static com.redhat.devtools.alizer.api.Constants.VERTX;

public class VertxFrameworkDetectorProviderImpl extends JavaFrameworkDetectorProvider {
    private static final String VERTX_TAG = "io.vertx";

    @Override
    public FrameworkDetectorWithConfigFileProvider create() {
        return new VertxFrameworkDetectorProviderImpl();
    }

    @Override
    public List<String> getFrameworks() {
        return Arrays.asList(VERTX);
    }

    @Override
    protected String getFrameworkTag() {
        return VERTX_TAG;
    }

    @Override
    public List<Service> getServices(Path root, Path configFile) throws IOException {
        return getServices(configFile, getFrameworks());
    }
}
