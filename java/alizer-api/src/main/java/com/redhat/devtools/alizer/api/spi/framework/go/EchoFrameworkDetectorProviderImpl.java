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

import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorProvider;

import java.util.Arrays;
import java.util.List;

import static com.redhat.devtools.alizer.api.Constants.ECHO;

public class EchoFrameworkDetectorProviderImpl extends GoFrameworkDetectorProvider {
    private static final String ECHO_TAG = "github.com/labstack/echo";
    @Override
    public FrameworkDetectorProvider create() {
        return new EchoFrameworkDetectorProviderImpl();
    }

    @Override
    public List<String> getFrameworks() {
        return Arrays.asList(ECHO);
    }

    @Override
    protected String getFrameworkTag() {
        return ECHO_TAG;
    }
}
