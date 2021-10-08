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
package com.redhat.devtools.alizer.api.spi;

import com.redhat.devtools.alizer.api.Language;
import com.redhat.devtools.alizer.api.LanguageFileHandler;
import com.redhat.devtools.alizer.api.LanguageFileItem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public abstract class LanguageEnricherProvider {

    public abstract LanguageEnricherProvider create();

    public abstract List<String> getSupportedLanguages();

    public abstract Language getEnrichedLanguage(Language language, List<File> files) throws IOException;

    public boolean isConfigurationValidForComponent(String language, File file) {
        return isValidPathPerLanguage(file.toPath(), language);
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
}
