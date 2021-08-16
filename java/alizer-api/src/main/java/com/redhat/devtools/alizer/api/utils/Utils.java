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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.redhat.devtools.alizer.api.spi.LanguageEnricherProvider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import org.apache.commons.io.IOUtils;

public class Utils {
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
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

    public static JsonNode getResourceAsJsonNode(String name) throws IOException {
        String yamlAsString = IOUtils.toString(Utils.class.getResourceAsStream(name), Charset.defaultCharset());
        return YAML_MAPPER.readTree(yamlAsString);
    }

    public static Map getJsonFileAsMap(File file) throws IOException {
        return JSON_MAPPER.readValue(file, Map.class);
    }

    public static Map<String, ArrayNode> getDependenciesByLanguage(JsonNode node, String field) {
        if (!node.has(field)) {
            return Collections.emptyMap();
        }
        JsonNode fieldNode = node.get(field);
        Map<String, ArrayNode> tags = new HashMap<>();
        for (Iterator<Map.Entry<String, JsonNode>> it = fieldNode.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            String service = entry.getKey();
            JsonNode attribute = entry.getValue();
            if (attribute.has("dependencies")) {
                ArrayNode anode = (ArrayNode) attribute.get("dependencies");
                tags.put(service, anode);
            }
        }
        return tags;
    }
}
