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
package com.redhat.devtools.alizer.registry.support;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DevfileRegistryMetadataProvider implements DevfileMetadataProvider {
    private final List<String> urls;
    private List<DevfileMetadata> devfileMetadata;

    public DevfileRegistryMetadataProvider(List<String> urls) {
        this.urls = urls;
    }

    private void load() {
        devfileMetadata = new ArrayList<>();
        for(String url : urls) {
            try {
                new DevfileRegistryProxy(url).collect(devfileMetadata);
            } catch (IOException e) {}
        }
    }

    @Override
    public List<DevfileMetadata> getDevfileMetada() {
        if (devfileMetadata == null) {
            load();
        }
        return devfileMetadata;
    }
}
