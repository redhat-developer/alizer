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

import java.util.ArrayList;
import java.util.List;

public class DevfileRegistryMetadataProviderBuilder {
    private List<String> urls = new ArrayList<>();

    public DevfileRegistryMetadataProviderBuilder withURL(String url) {
        urls.add(url);
        return this;
    }

    public DevfileRegistryMetadataProviderBuilder withURLs(List<String> urls) {
        this.urls = urls;
        return this;
    }

    public DevfileMetadataProvider build() {
        return new DevfileRegistryMetadataProvider(urls);
    }
}
