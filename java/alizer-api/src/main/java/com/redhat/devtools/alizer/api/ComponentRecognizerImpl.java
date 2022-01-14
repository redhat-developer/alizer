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
import com.redhat.devtools.alizer.api.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ComponentRecognizerImpl extends Recognizer implements ComponentRecognizer {

    public ComponentRecognizerImpl(RecognizerFactory builder) {
        super(builder);
    }

    public List<Component> analyze(String path) throws IOException {
        List<File> files = getFiles(Paths.get(path));
        List<Component> components = detectComponents(files);

        // it may happen that a language has no a specific configuration file (e.g opposite to JAVA -> pom.xml and Nodejs -> package.json)
        // we then rely on the language recognizer
        List<Path> directoriesPathsWithoutConfigFile = getDirectoriesPathsWithoutConfigFile(Paths.get(path), components);
        components.addAll(getComponentsWithoutConfigFile(directoriesPathsWithoutConfigFile));
        return components;
    }

    private List<Path> getDirectoriesPathsWithoutConfigFile(Path root, List<Component> components) throws IOException {
        if (components.isEmpty()) {
            return Collections.singletonList(root);
        }

        AtomicReference<List<Path>> directoriesWithoutConfigFile = new AtomicReference<>(new ArrayList<>());
        Files.walk(root, Integer.MAX_VALUE).filter(Files::isDirectory).skip(1)
                .forEach(directory -> {
                    if (!hasDirectoryAnyComponent(directory, components)){
                        directoriesWithoutConfigFile.set(getParentFolders(directory, directoriesWithoutConfigFile.get()));
                    }
                });
        return directoriesWithoutConfigFile.get();
    }

    /**
     * Check if a directory is part of a component (root path or any subfolder)
     * E.g root component app/component then app/component/folder -> true app/otherfolder -> false
     *
     * @param directory directory path to check
     * @param components list of components
     * @return true if directory contains a component or it is a subfolder of a folder containing a component, false otherwise
     */
    private boolean hasDirectoryAnyComponent(Path directory, List<Component> components) {
        return components.stream().map(Component::getPath)
                .anyMatch(directoryWithComponent -> directoryWithComponent.equals(directory) ||
                        isFirstPathParentOfSecond(directoryWithComponent, directory));
    }

    private List<Component> getComponentsWithoutConfigFile(List<Path> directories) throws IOException {
        List<Component> components = new ArrayList<>();
        for (Path directory: directories) {
            Component component = detectComponent(directory, Collections.emptyList());
            // only takes component with languages that have no config file
            // E.g if the directory consists of javascript files but it doesn't contain a package.json, something is wrong
            // and we do not consider it as an actual component
            if (component != null
                && isValidNoConfigComponent(component)) {
                components.add(component);
            }
        }
        return components;
    }

    /**
     * Return true if it is a component with a language that does not have any config file
     * E.g if the directory (and the component itself) consists of javascript files without a package.json, something is wrong
     * and we do not consider it as an actual no config component
     *
     * @param component component
     * @return true if it is a valid component, false otherwise
     */
    private boolean isValidNoConfigComponent(Component component) {
        if (component.getLanguages().isEmpty()) {
            return false;
        }
        String language = component.getLanguages().get(0).getName();
        LanguageFileItem languageFileItem = LanguageFileHandler.get().getLanguageByNameOrAlias(language);
        return languageFileItem.getConfigurationFiles().isEmpty();
    }

    private List<Component> detectComponents(List<File> files) throws IOException {
        Map<String, List<String>> configurationPerLanguages = LanguageFileHandler.get().getConfigurationPerLanguageMapping();
        List<Component> components = new ArrayList<>();
        for (File file: files) {
            Optional<String> configurationMatched = getConfigurationByFile(configurationPerLanguages.keySet(), file);
            if (configurationMatched.isPresent()) {
                List<String> languagesPerConfiguration = getLanguagesWithWhichConfigurationIsValid(configurationPerLanguages.get(configurationMatched.get()), file);
                if (!languagesPerConfiguration.isEmpty()) {
                    Component component = detectComponent(file.getParentFile().toPath(), configurationPerLanguages.get(configurationMatched.get()));
                    if (component != null
                        && components.stream().noneMatch(comp -> comp.getPath().equals(component.getPath()))) {
                        components.add(component);
                    }
                }
            }
        }
        return components;
    }

    private Optional<String> getConfigurationByFile(Set<String> regexes, File file) {
        return regexes.stream().filter(regex -> Pattern.matches(regex, file.getName())).findFirst();
    }

    private List<String> getLanguagesWithWhichConfigurationIsValid(List<String> languages, File file) {
        return languages.stream().filter(language -> isConfigurationValid(language, file)).collect(Collectors.toList());
    }

    private boolean isConfigurationValid(String language, File file) {
        LanguageEnricherProvider enricher = Utils.getEnricherByLanguage(language);
        if (enricher != null) {
            return enricher.create().isConfigurationValidForComponent(language, file);
        }
        return false;
    }

    /**
     * Create a new component from root folder
     *
     * @param root folder where the component is stored
     * @param configurationLanguages languages which uses this config file (e.g pom.xml -> java)
     * @return new component or null if folder doesn't contain anything valid
     * @throws IOException if errored while detecting languages/framework used
     */
    private Component detectComponent(Path root, List<String> configurationLanguages) throws IOException {
        RecognizerFactory recognizerFactory = new RecognizerFactory();
        LanguageRecognizer languageRecognizer = recognizerFactory.createLanguageRecognizer();

        List<Language> languages = getLanguagesWeightedByConfigFile(languageRecognizer.analyze(root.toString()), configurationLanguages);
        if (isLanguageSupported(languages)) {
            return new Component(root, languages);
        }
        return null;
    }

    private boolean isLanguageSupported(List<Language> languages) {
        if (languages.isEmpty()) {
            return false;
        }
        Language mainLanguage = languages.get(0);
        return mainLanguage.canBeComponent() &&
                !mainLanguage.getFrameworks().isEmpty();
    }

    /**
     * Return a list of all languages ordered by using the configurationLanguage, if any
     * E.g it may happen that a java project has many javascript files in it and then the returned language list is
     * 1. Javascript 2. Java. But as we are tracing a java component (found by using a pom.xml or build.gradle) the order
     * of languages needs to be swapped -> 1. JAVA 2. Javascript
     *
     * @param languages list of all languages
     * @param configurationLanguages languages that uses that configuration file
     * @return an ordered language list based on the configuration file, original language list if configLanguage is empty
     */
    private List<Language> getLanguagesWeightedByConfigFile(List<Language> languages, List<String> configurationLanguages) {
        if (configurationLanguages.isEmpty()) {
            return languages;
        }

        Optional<Language> language = languages.stream()
                .filter(lang -> configurationLanguages.stream().anyMatch(l -> l.equalsIgnoreCase(lang.getName()))).findFirst();
        if (language.isPresent()) {
            languages.remove(language.get());
            languages.add(0, language.get());
        }
        return languages;
    }

    /**
     * Return true if first path is parent of the second
     *
     * @param path path that should be parent
     * @param potentialSubFolder path that should be a sub-folder
     * @return true if path contain potentialSubFolder, false otherwise
     */
    private boolean isFirstPathParentOfSecond(Path path, Path potentialSubFolder) {
        return isIncludedInPath(potentialSubFolder, path::equals);
    }

    private boolean isIncludedInPath(Path path, Function<Path, Boolean> isIncluded) {
        boolean containSubFolder = false;
        while (path != null && !containSubFolder) {
            if (isIncluded.apply(path)) {
                containSubFolder = true;
            }
            path = path.getParent();
        }
        return containSubFolder;
    }

    /**
     * Return all paths which are not sub-folders of some other path within the list
     * Target will be added to the list if it is not a sub-folder of any other path within the list
     * If a path in the list is sub-folder of Target, that path will be removed.
     *
     * @param target new path to be added
     * @param directories list of all previously added paths
     * @return the list containing all paths which are not sub-folders of any other
     */
    private List<Path> getParentFolders(Path target, List<Path> directories) {
        List<Path> support = new ArrayList<>();
        for (Path directory: directories) {
            Path newPath = getParentFolder(target, directory);
            if (newPath != null) {
                target = newPath;
            } else {
                support.add(directory);
            }
        }
        support.add(target);
        return support;
    }

    /**
     * Return the parent folder between the two directories, null if there is no sub-path
     *
     * @param directory1 first directory to compare
     * @param directory2 second directory to compare
     * @return path of the parent folder or null if no directory is parent of the other
     */
    private Path getParentFolder(Path directory1, Path directory2) {
        if (directory1.toString().length() > directory2.toString().length()) {
            if (isFirstPathParentOfSecond(directory2, directory1)) {
                return directory2;
            }
        } else {
            if (isFirstPathParentOfSecond(directory1, directory2)) {
                return directory1;
            }
        }
        return null;
    }
}
