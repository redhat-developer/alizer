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
package com.redhat.devtools.recognizer.api;

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
    private Map<String, List<LanguageFileItem>> filenameXLanguage = new HashMap<>();

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
                LanguageFileItem language = new LanguageFileItem(nameLanguage, type, group);
                languages.put(nameLanguage, language);
                if (languageAttributes.has("extensions")) {
                    JsonNode extensions = languageAttributes.get("extensions");
                    for (JsonNode extension: extensions) {
                        if (!extension.asText("").isEmpty()) {
                            List<LanguageFileItem> languagesPerExtension = extensionXLanguage.getOrDefault(extension.asText(), new ArrayList<>());
                            languagesPerExtension.add(language);
                            extensionXLanguage.put(extension.asText(), languagesPerExtension);
                        }
                    }
                }

                if (languageAttributes.has("filenames")) {
                    JsonNode filenames = languageAttributes.get("filenames");
                    for (JsonNode filename: filenames) {
                        if (!filename.asText("").isEmpty()) {
                            List<LanguageFileItem> languagesPerFilename = filenameXLanguage.getOrDefault(filename.asText(), new ArrayList<>());
                            languagesPerFilename.add(language);
                            filenameXLanguage.put(filename.asText(), languagesPerFilename);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public List<LanguageFileItem> getLanguagesByExtension(String extension) {
        return extensionXLanguage.getOrDefault(extension, Collections.emptyList());
    }

    public LanguageFileItem getLanguageByName(String name) {
        return languages.get(name);
    }
}
