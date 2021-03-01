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
import com.redhat.devtools.recognizer.api.spi.LanguageEnricherProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;


import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class LanguageRecognizerImpl implements LanguageRecognizer {

    LanguageRecognizerImpl(LanguageRecognizerBuilder builder) {}

    public String selectDevFile(String srcPath,  List<DevfileType> devfileTypes, List<File> devfiles) throws IOException {
        if (devfileTypes.isEmpty()) {
            devfileTypes = new ArrayList<>();
        }
        devfileTypes.addAll(getTypesFromFiles(devfiles));
        return selectDevFileFromTypes(srcPath, devfileTypes);
    }

    public String selectDevFileFromTypes(String srcPath, List<DevfileType> devfileTypes) throws IOException {
        List<Language> languages = analyze(srcPath);
        for (Language language: languages) {
            Optional<String> devFile = getDevFileByLanguage(language, devfileTypes);
            if (devFile.isPresent()) {
                return devFile.get();
            }
        }
        return null;
    }

    public List<Language> analyze(String path) throws IOException {
        Map<LanguageFileItem, Integer> languagesDetected = new HashMap<>();
        // init dictionary with languages file
        LanguageFileHandler handler = LanguageFileHandler.get();

        List<String> files = getFiles(Paths.get(path));

        // save all extensions extracted from files + their occurrences
        Map<String, Long> extensions = files.stream().collect(groupingBy(file -> "." + FilenameUtils.getExtension(file), counting()));

        // get languages belonging to extensions found
        extensions.keySet().stream().forEach(extension -> {
            List<LanguageFileItem> languages = handler.getLanguagesByExtension(extension);
            if (languages.isEmpty()) return;
            languages.stream().forEach(language -> {
                LanguageFileItem tmpLanguage = language.getGroup().isEmpty() ? language : handler.getLanguageByName(language.getGroup());
                long percentage = languagesDetected.getOrDefault(tmpLanguage, 0) + extensions.get(extension);
                languagesDetected.put(tmpLanguage, (int) percentage);
            });
        });

        // get only programming language and calculate percentage
        int totalProgrammingOccurences = (int) languagesDetected.keySet().stream().
                filter(lang -> lang.getType().equalsIgnoreCase("programming")).
                mapToLong(lang -> languagesDetected.get(lang)).sum();

        // only keep programming language which consists of atleast the 2% of the project
        List<String> finalFiles = files;
        List<Language> programmingLanguagesDetected = languagesDetected.keySet().stream().
                filter(lang -> lang.getType().equalsIgnoreCase("programming")).
                filter(lang -> (double)languagesDetected.get(lang) / totalProgrammingOccurences > 0.02).
                map(lang -> new Language(lang.getName(), lang.getAliases(), (double)languagesDetected.get(lang) / totalProgrammingOccurences * 100)).
                map(lang -> getDetailedLanguage(lang, finalFiles)).
                sorted(Comparator.comparingDouble(Language::getUsageInPercentage).reversed()).
                collect(Collectors.toList());

        return programmingLanguagesDetected;
    }

    private static Language getDetailedLanguage(Language language, List<String> files) {
        LanguageEnricherProvider enricher = getEnricherByLanguage(language.getName());
        if (enricher != null) {
            return enricher.create().getEnrichedLanguage(language, files);
        }
        return language;
    }

    private static List<String> getFiles(Path rootDirectory) throws IOException {
        return Files.walk(rootDirectory, Integer.MAX_VALUE).filter(Files::isRegularFile).map(String::valueOf)
                .collect(Collectors.toList());

    }

    private Optional<String> getDevFileByLanguage(Language language, List<DevfileType> devfileTypes) {
        List<DevfileType> devFiles;
        List<DevfileType> devFilesByLanguage = devfileTypes
                .stream()
                .filter(devfile -> devfile.getLanguage().equalsIgnoreCase(language.getName()) ||
                                        language.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(devfile.getLanguage())))
                .collect(Collectors.toList());
        if (devFilesByLanguage.isEmpty()) {
            return Optional.empty();
        }

        List<DevfileType> devFilesByFrameworks = filterDevFilesByProperties(devFilesByLanguage, language.getFrameworks());
        if (devFilesByFrameworks.isEmpty()) {
            devFiles = filterDevFilesByProperties(devFilesByLanguage, language.getTools());
            if (devFiles.isEmpty()) {
                devFiles = devFilesByLanguage;
            }
        } else {
            devFiles = filterDevFilesByProperties(devFilesByFrameworks, language.getTools());
            if (devFiles.isEmpty()) {
                devFiles = devFilesByFrameworks;
            }
        }
        return devFiles.stream().map(devfile -> devfile.getName()).findFirst();
    }

    private List<DevfileType> filterDevFilesByProperties(List<DevfileType> devFiles, List<String> properties) {
        return devFiles
                .stream()
                .filter(devfile -> devfile.getTags().stream().anyMatch(tag -> properties.stream().anyMatch(property -> property.equalsIgnoreCase(tag))))
                .collect(Collectors.toList());
    }

    private List<DevfileType> getTypesFromFiles(List<File> devfiles) {
        List<DevfileType> devfileTypes = new ArrayList<>();
        ObjectMapper om = new ObjectMapper(new YAMLFactory());

        devfiles.forEach(devfile -> {
            try {
                JsonNode node = om.readTree(devfile);
                if (!node.has("name") || !node.has("language")) {
                    return;
                }
                String name = node.get("name").asText();
                String language = node.get("language").asText();
                List<String> tags = new ArrayList<>();
                if (node.has("tags")) {
                    JsonNode tagsNode = node.get("tags");
                    for (JsonNode tagNode: tagsNode) {
                        tags.add(tagNode.asText());
                    }
                }
                devfileTypes.add(new DevfileType(name, language, tags));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return devfileTypes;
    }

    public static LanguageEnricherProvider getEnricherByLanguage(String language) {
        ServiceLoader<LanguageEnricherProvider> loader = ServiceLoader.load(LanguageEnricherProvider.class);
        Iterator<LanguageEnricherProvider> it = loader.iterator();
        while (it.hasNext()) {
            LanguageEnricherProvider provider = it.next();
            if (provider.create().getSupportedLanguages().stream().anyMatch(supported -> supported.equalsIgnoreCase(language))) {
                return provider;
            }
        }
        return null;
    }
}
