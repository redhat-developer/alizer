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
package com.redhat.devtools.alizer.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.devtools.alizer.api.spi.LanguageEnricherProvider;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.ServiceLoader;

public class Utils {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public static LanguageEnricherProvider getEnricherByLanguage(String language) {
        ServiceLoader<LanguageEnricherProvider> loader = ServiceLoader.load(LanguageEnricherProvider.class, Utils.class.getClassLoader());
        for (LanguageEnricherProvider provider : loader) {
            if (provider.create().getSupportedLanguages().stream().anyMatch(supported -> supported.equalsIgnoreCase(language))) {
                return provider;
            }
        }
        return null;
    }

    public static Map getJsonFileAsMap(File file) throws IOException {
        return JSON_MAPPER.readValue(file, Map.class);
    }
}
