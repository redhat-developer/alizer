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

import com.redhat.devtools.alizer.api.spi.LanguageEnricherProvider;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class LanguageRecognizerImpl extends Recognizer implements LanguageRecognizer {

    LanguageRecognizerImpl(RecognizerBuilder builder) {
        super(builder);
    }

    public <T extends DevfileType> T selectDevFileFromTypes(String srcPath, List<T> devfileTypes) throws IOException {
        List<Language> languages = analyze(srcPath);
        return selectDevFileFromTypes(languages, devfileTypes);
    }

    public <T extends DevfileType> T selectDevFileFromTypes(List<Language> languages, List<T> devfileTypes) {
        for (Language language: languages) {
            Optional<LanguageScore> score = devfileTypes.stream().map(devfileType -> new LanguageScore(language, devfileType)).sorted().findFirst();
            if (score.isPresent() && score.get().getScore() > 0) {
                return (T) score.get().getDevfileType();
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
        extensions.keySet().forEach(extension -> {
            List<LanguageFileItem> languages = handler.getLanguagesByExtension(extension);
            if (languages.isEmpty()) return;
            languages.forEach(language -> {
                LanguageFileItem tmpLanguage = language.getGroup().isEmpty() ? language : handler.getLanguageByName(language.getGroup());
                long percentage = languagesDetected.getOrDefault(tmpLanguage, 0) + extensions.get(extension);
                languagesDetected.put(tmpLanguage, (int) percentage);
            });
        });

        // get only programming language and calculate percentage
        int totalProgrammingOccurences = (int) languagesDetected.keySet().stream().
                filter(lang -> lang.getType().equalsIgnoreCase("programming")).
                mapToLong(languagesDetected::get).sum();

        // only keep programming language which consists of atleast the 2% of the project

        return languagesDetected.keySet().stream().
                filter(lang -> lang.getType().equalsIgnoreCase("programming")).
                filter(lang -> (double)languagesDetected.get(lang) / totalProgrammingOccurences > 0.02).
                map(lang -> new Language(lang.getName(), lang.getAliases(), (double)languagesDetected.get(lang) / totalProgrammingOccurences * 100)).
                map(lang -> getDetailedLanguage(lang, files)).
                sorted(Comparator.comparingDouble(Language::getUsageInPercentage).reversed()).
                collect(Collectors.toList());
    }

    private static Language getDetailedLanguage(Language language, List<String> files) {
        LanguageEnricherProvider enricher = getEnricherByLanguage(language.getName());
        if (enricher != null) {
            return enricher.create().getEnrichedLanguage(language, files);
        }
        return language;
    }

    public static LanguageEnricherProvider getEnricherByLanguage(String language) {
        ServiceLoader<LanguageEnricherProvider> loader = ServiceLoader.load(LanguageEnricherProvider.class, LanguageRecognizerImpl.class.getClassLoader());
        for (LanguageEnricherProvider provider : loader) {
            if (provider.create().getSupportedLanguages().stream().anyMatch(supported -> supported.equalsIgnoreCase(language))) {
                return provider;
            }
        }
        return null;
    }
}
