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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ComponentRecognizerImpl extends Recognizer {

    public ComponentRecognizerImpl(RecognizerBuilder builder) {
        super(builder);
    }

    public <T extends DevfileType> List<Component> analyze(String path, List<T> devfileTypes) throws IOException {
        List<Path> files = getFilePaths(Paths.get(path));
        List<Component> components = getComponents(files, devfileTypes);

        // it may happen that a language has no a specific configuration file (e.g opposite to JAVA -> pom.xml and Nodejs -> package.json)
        // we then rely on the language recognizer and use the most used language to pick a devfile
        List<Path> directoriesPathsWithoutConfigFile = getDirectoriesPathsWithoutConfigFile(Paths.get(path), components);
        components.addAll(getComponentsWithoutConfigFile(directoriesPathsWithoutConfigFile, devfileTypes));
        return components;
    }

    private List<Path> getDirectoriesPathsWithoutConfigFile(Path root, List<Component> components) throws IOException {
        if (components.isEmpty()) {
            return Collections.singletonList(root);
        }

        List<Path> directoriesWithoutConfigFile = new ArrayList<>();
        List<Path> allDirectoriesFromRoot = Files.walk(root, Integer.MAX_VALUE).filter(Files::isDirectory).skip(1).collect(Collectors.toList());
        for (Path directory: allDirectoriesFromRoot) {
            if (!hasDirectoryAnyComponent(directory, components)){
                directoriesWithoutConfigFile = getParentFolders(directory, directoriesWithoutConfigFile);
            }
        }
        return directoriesWithoutConfigFile;
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
        List<Path> directoriesWithComponents = components.stream().map(Component::getPath).collect(Collectors.toList());
        if (directoriesWithComponents.isEmpty()) {
            return false;
        }
        for(Path directoryWithComponent: directoriesWithComponents) {
            if (directoriesWithComponents.equals(directory) ||
                    isFirstPathParentOfSecond(directoryWithComponent, directory)) {
                return true;
            }
        }
        return false;
    }

    private <T extends DevfileType> List<Component> getComponentsWithoutConfigFile(List<Path> directories, List<T> devfileTypes) throws IOException {
        List<Component> components = new ArrayList<>();
        for (Path directory: directories) {
            Component component = getComponent(directory, "", devfileTypes);
            // only takes component with languages that have no config file
            // E.g if the directory consists of javascript files but it doesn't contain a package.json, something is wrong
            // and we do not consider it as an actual component
            if (isValidNoConfigComponent(component)) {
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
        Language mainLanguage = component.getLanguages().get(0);
        LanguageFileItem languageFileItem = LanguageFileHandler.get().getLanguageByName(mainLanguage.getName());
        return languageFileItem.getConfigurationFiles().isEmpty();
    }

    private <T extends DevfileType> List<Component> getComponents(List<Path> files, List<T> devfileTypes) throws IOException {
        Map<String, String> configurationPerLanguage = LanguageFileHandler.get().getConfigurationPerLanguageMapping();
        List<Component> components = new ArrayList<>();
        for (Path filepath: files) {
            if (configurationPerLanguage.containsKey(filepath.getFileName().toString())
                    && isValidPathPerLanguage(filepath, configurationPerLanguage.get(filepath.getFileName().toString()))) {
                components.add(getComponent(filepath.getParent(), configurationPerLanguage.get(filepath.getFileName().toString()), devfileTypes));
            }
        }
        return components;
    }

    private <T extends DevfileType> Component getComponent(Path root, String configurationLanguage, List<T> devfileTypes) throws IOException {
        RecognizerBuilder recognizerBuilder = new RecognizerBuilder();
        LanguageRecognizer languageRecognizer = recognizerBuilder.languageRecognizer();
        List<Language> languages = getLanguagesWeightedByConfigFile(languageRecognizer.analyze(root.toString()), configurationLanguage);
        DevfileType devfileType = languageRecognizer.selectDevFileFromTypes(languages, devfileTypes);
        return new Component(root, languages, devfileType);
    }

    /**
     * Return a list of all languages ordered by using the configurationLanguage, if any
     * E.g it may happen that a java project has many javascript files in it and then the returned language list is
     * 1. Javascript 2. Java. But as we are tracing a java component (found by using a pom.xml or build.gradle) the order
     * of languages needs to be swapped -> 1. JAVA 2. Javascript
     *
     * @param languages list of all languages
     * @param configurationLanguage configuration file language
     * @return an ordered language list based on the configuration file, original language list if configLanguage is empty
     */
    private List<Language> getLanguagesWeightedByConfigFile(List<Language> languages, String configurationLanguage) {
        if (configurationLanguage.isEmpty()) {
            return languages;
        }

        Optional<Language> language = languages.stream().filter(lang -> lang.getName().equalsIgnoreCase(configurationLanguage)).findFirst();
        if (language.isPresent()) {
            languages.remove(language.get());
            languages.add(0, language.get());
        }
        return languages;
    }

    /**
     * Check if path does not contain any folder that is excluded by the language
     * E.g Nodejs -> node_modules
     *
     * @param path path to be verified
     * @param language language to find the folders to not being considered to find components
     * @return true if the path doesn't contain any excluded folder, false otherwise
     */
    private boolean isValidPathPerLanguage(Path path, String language) {
        LanguageFileItem languageFileItem = LanguageFileHandler.get().getLanguageByName(language);
        List<String> excludeFolders = languageFileItem.getExcludeFolders();
        if (excludeFolders.isEmpty()) {
            return true;
        }
        for (String excludeFolder: excludeFolders) {
            if (isFolderNameIncludedInPath(path, excludeFolder)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return true if first path contains a folder named potentialSubFolderName
     *
     * @param fullPath path that should be parent
     * @param potentialSubFolderName folder name that should be a sub-folder
     * @return true if fullPath contain potentialSubFolderName, false otherwise
     */
    private boolean isFolderNameIncludedInPath(Path fullPath, String potentialSubFolderName) {
        return isIncludedInPath(fullPath, (p) -> p.toFile().isDirectory()
                && p.getFileName() != null
                && p.getFileName().toString().equalsIgnoreCase(potentialSubFolderName));
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
