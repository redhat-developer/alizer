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
package com.redhat.devtools.alizer.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;

public class LanguageFileHandler {
    private static final String YAML_PATH = "languages.yml";
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
    private static LanguageFileHandler INSTANCE;
    private Map<String, LanguageFileItem> languages = new HashMap<>();
    private Map<String, List<LanguageFileItem>> extensionXLanguage = new HashMap<>();

    private LanguageFileHandler(){
        initLanguages();
    }

    public static LanguageFileHandler get() {
        if (INSTANCE == null) {
            INSTANCE = new LanguageFileHandler();
        }
        return INSTANCE;
    }

    private void initLanguages() {
        try {
            String yamlAsString = IOUtils.toString(LanguageFileHandler.class.getResourceAsStream("/" + YAML_PATH));
            JsonNode node = YAML_MAPPER.readTree(yamlAsString);
            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                String nameLanguage = entry.getKey();
                JsonNode languageAttributes = entry.getValue();
                String type = languageAttributes.get("type").asText();
                String group = languageAttributes.has("group") ? languageAttributes.get("group").asText() : "";
                List<String> aliases = getValueAsList(languageAttributes, "aliases");
                LanguageFileItem languageFileItem = new LanguageFileItem(nameLanguage, aliases, type, group);
                languages.put(nameLanguage, languageFileItem);
                populateLanguageList(extensionXLanguage, languageAttributes, "extensions", languageFileItem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private List<String> getValueAsList(JsonNode languageAttributes, String field) {
        List<String> values = new ArrayList<>();
        if (languageAttributes.has(field)) {
            JsonNode fieldValues = languageAttributes.get(field);
            for (JsonNode node : fieldValues) {
                values.add(node.asText());
            }
        }
        return values;
    }

    private void populateLanguageList(Map<String, List<LanguageFileItem>> languageMap, JsonNode languageAttributes, String field, LanguageFileItem language) {
        if (languageAttributes.has(field)) {
            JsonNode fieldValues = languageAttributes.get(field);
            for (JsonNode value: fieldValues) {
                if (!value.asText("").isEmpty()) {
                    List<LanguageFileItem> languageMapValue = languageMap.getOrDefault(value.asText(), new ArrayList<>());
                    languageMapValue.add(language);
                    languageMap.put(value.asText(), languageMapValue);
                }
            }
        }
    }



    public List<LanguageFileItem> getLanguagesByExtension(String extension) {
        return extensionXLanguage.getOrDefault(extension, Collections.emptyList());
    }

    public LanguageFileItem getLanguageByName(String name) {
        return languages.get(name);
    }
}
