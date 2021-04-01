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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class DevfileRegistryProxy {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String INDEX_PATH = "index";
    private static final String DEVFILES_INDEX_JSON = "blob/HEAD/devfiles/index.json";
    private final String url;

    public DevfileRegistryProxy(String url) {
        this.url = url;
    }

    private static String append(String url, String path) {
        if (url.endsWith("/")) {
            return url + path;
        } else {
            return url + '/' + path;
        }
    }

    private static String adapt(String url) throws MalformedURLException {
        url = url.replaceFirst("^https://github.com/(.*)/blob/(.*)$",
                "https://raw.githubusercontent.com/$1/$2");

        url = url.replaceFirst("^https://gitlab.com/(.*)/-/blob/(.*)$",
                "https://gitlab.com/$1/-/raw/$2");

        url = url.replaceFirst("^https://bitbucket.org/(.*)/src/(.*)$",
                "https://bitbucket.org/$1/raw/$2");
        return url;
    }

    public void collect(List<DevfileMetadata> devfileMetadata) throws IOException {
        try {
            URL u = new URL(adapt(append(url, INDEX_PATH)));
            MAPPER.readValue(u.openStream(), new TypeReference<List<DevfileMetadata>>(){}).forEach(devfile -> devfileMetadata.add(devfile));
        } catch (IOException e) {
            URL u = new URL(adapt(append(url, DEVFILES_INDEX_JSON)));
            MAPPER.readValue(u.openStream(), new TypeReference<List<DevfileMetadata>>(){}).forEach(devfile -> devfileMetadata.add(devfile));
        }
    }
}
