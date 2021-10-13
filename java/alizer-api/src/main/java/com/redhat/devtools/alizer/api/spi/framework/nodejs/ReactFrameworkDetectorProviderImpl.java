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
import java.util.Arrays;
import java.util.List;


import static com.redhat.devtools.alizer.api.Constants.REACT;

public class ReactFrameworkDetectorProviderImpl extends NodeJsFrameworkDetectorProvider {
    private static final String REACT_TAG = "react";

    @Override
    public List<String> getFrameworks() {
        return Arrays.asList(REACT);
    }

    protected String getFrameworkTag() {
        return REACT_TAG;
    }

    @Override
    public FrameworkDetectorWithConfigFileProvider create() {
        return new ReactFrameworkDetectorProviderImpl();
    }
}