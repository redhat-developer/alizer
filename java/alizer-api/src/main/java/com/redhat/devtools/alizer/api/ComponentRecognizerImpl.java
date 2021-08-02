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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ComponentRecognizerImpl extends Recognizer {

    public ComponentRecognizerImpl(RecognizerBuilder builder) {
        super(builder);
    }

    public List<Component> analyze(String path) throws IOException {
        List<String> files = getFiles(Paths.get(path));
        Map<String, String> configurationPerLanguage = LanguageFileHandler.get().getConfigurationPerLanguageMapping();
        List<Component> results = new ArrayList<>();
        for (String file: files) {
            Path filepath = Paths.get(file);
            if (configurationPerLanguage.containsKey(filepath.getFileName().toString())
                && isValidPath(file, configurationPerLanguage.get(filepath.getFileName().toString()))) {
                results.add(getComponent(filepath, configurationPerLanguage.get(filepath.getFileName().toString())));
            }
        }
        return results;
    }

    private Component getComponent(Path configurationFile, String configurationLanguage) throws IOException {
        Path directory = configurationFile.getParent();
        LanguageRecognizer languageRecognizer = new RecognizerBuilder().languageRecognizer();
        List<Language> languages = getLanguagesWeightedByConfigFile(languageRecognizer.analyze(directory.toString()), configurationLanguage);
        return new Component(directory.toString(), languages);
    }

    private List<Language> getLanguagesWeightedByConfigFile(List<Language> languages, String configurationLanguage) {
        Optional<Language> language = languages.stream().filter(lang -> lang.getName().equalsIgnoreCase(configurationLanguage)).findFirst();
        if (language.isPresent()) {
            languages.remove(language.get());
            languages.add(0, language.get());
        }
        return languages;
    }

    private boolean isValidPath(String path, String language) {
        LanguageFileItem languageFileItem = LanguageFileHandler.get().getLanguageByName(language);
        List<String> excludeFolders = languageFileItem.getExcludeFolders();
        if (excludeFolders.isEmpty()) {
            return true;
        }
        for (String excludeFolder: excludeFolders) {
            if (path.contains("/" + excludeFolder + "/")) {
                return false;
            }
        }
        return true;
    }
}
