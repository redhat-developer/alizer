/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc.
 ******************************************************************************/
package com.redhat.devtools.recognizer;

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

public class LanguageHandler {

    private static final String YAML_PATH = "languages.yml";
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
    private static LanguageHandler INSTANCE;
    private Map<String, Language> languages = new HashMap<>();
    private Map<String, List<Language>> extensionXLanguage = new HashMap<>();
    private Map<String, List<Language>> filenameXLanguage = new HashMap<>();

    private LanguageHandler(){
        initLanguages();
    }

    public static LanguageHandler get() {
        if (INSTANCE == null) {
            INSTANCE = new LanguageHandler();
        }
        return INSTANCE;
    }

    private void initLanguages() {

        try {
            String yamlAsString = IOUtils.toString(LanguageHandler.class.getResource("/" + YAML_PATH).openStream());
            JsonNode node = YAML_MAPPER.readTree(yamlAsString);
            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                String nameLanguage = entry.getKey();
                JsonNode languageAttributes = entry.getValue();
                String type = languageAttributes.get("type").asText();
                String group = languageAttributes.has("group") ? languageAttributes.get("group").asText() : "";
                Language language = new Language(nameLanguage, type, group);
                languages.put(nameLanguage, language);
                if (languageAttributes.has("extensions")) {
                    JsonNode extensions = languageAttributes.get("extensions");
                    for (JsonNode extension: extensions) {
                        if (!extension.asText("").isEmpty()) {
                            List<Language> languagesPerExtension = extensionXLanguage.getOrDefault(extension.asText(), new ArrayList<>());
                            languagesPerExtension.add(language);
                            extensionXLanguage.put(extension.asText(), languagesPerExtension);
                        }
                    }
                }

                if (languageAttributes.has("filenames")) {
                    JsonNode filenames = languageAttributes.get("filenames");
                    for (JsonNode filename: filenames) {
                        if (!filename.asText("").isEmpty()) {
                            List<Language> languagesPerFilename = filenameXLanguage.getOrDefault(filename.asText(), new ArrayList<>());
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

    public List<Language> getLanguagesByExtension(String extension) {
        return extensionXLanguage.getOrDefault(extension, Collections.emptyList());
    }

    public Language getLanguageByName(String name) {
        return languages.get(name);
    }

}
