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
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import static com.redhat.devtools.alizer.api.Constants.SPRING;

public class SpringFrameworkDetectorProviderImpl extends JavaFrameworkDetectorProvider {
    private static final String SPRING_TAG = "org.springframework";

    @Override
    public FrameworkDetectorWithConfigFileProvider create() {
        return new SpringFrameworkDetectorProviderImpl();
    }

    @Override
    public List<String> getFrameworks() {
        return Arrays.asList(SPRING);
    }

    @Override
    protected String getFrameworkTag() {
        return SPRING_TAG;
    }

    @Override
    public List<Service> getServices(Path root, File config) throws IOException {
        return getServices(config, getFrameworks());
    }
}
