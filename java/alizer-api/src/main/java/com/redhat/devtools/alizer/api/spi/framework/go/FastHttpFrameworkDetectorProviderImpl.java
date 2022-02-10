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

import static com.redhat.devtools.alizer.api.Constants.FAST_HTTP;

public class FastHttpFrameworkDetectorProviderImpl extends GoFrameworkDetectorProvider {
    private static final String FAST_HTTP_TAG = "github.com/valyala/fasthttp";
    @Override
    public FrameworkDetectorProvider create() {
        return new FastHttpFrameworkDetectorProviderImpl();
    }

    @Override
    public List<String> getFrameworks() {
        return Arrays.asList(FAST_HTTP);
    }

    @Override
    protected String getFrameworkTag() {
        return FAST_HTTP_TAG;
    }
}
