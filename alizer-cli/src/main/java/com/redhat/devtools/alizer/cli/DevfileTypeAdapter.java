/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.redhat.devtools.alizer.cli;

import com.redhat.devtools.alizer.registry.support.DevfileMetadata;
import com.redhat.devtools.recognizer.api.DevfileType;

import java.util.List;

public class DevfileTypeAdapter implements DevfileType {
    private final DevfileMetadata delegate;

    public DevfileTypeAdapter(DevfileMetadata delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getLanguage() {
        return delegate.getLanguage();
    }

    @Override
    public String getProjectType() {
        return delegate.getProjectType();
    }

    @Override
    public List<String> getTags() {
        return delegate.getTags();
    }
}
