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

import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorWithConfigFileProvider;
import java.util.Arrays;
import java.util.List;


import static com.redhat.devtools.alizer.api.Constants.QUARKUS;

public class QuarkusFrameworkDetectorProviderImpl extends JavaFrameworkDetectorProvider {
    private static final String QUARKUS_TAG = "io.quarkus";

    @Override
    public FrameworkDetectorWithConfigFileProvider create() {
        return new QuarkusFrameworkDetectorProviderImpl();
    }

    @Override
    public List<String> getFrameworks() {
        return Arrays.asList(QUARKUS);
    }

    @Override
    protected String getFrameworkTag() {
        return QUARKUS_TAG;
    }
}
