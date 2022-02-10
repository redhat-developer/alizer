/*******************************************************************************
 * Copyright (c) 2022 Red Hat, Inc.
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
import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorProvider;
import com.redhat.devtools.alizer.api.spi.framework.go.GoFrameworkDetectorProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.regex.Pattern;

import static com.redhat.devtools.alizer.api.Constants.GO;

public class GoLanguageEnricherProviderImpl extends LanguageEnricherProvider{
    @Override
    public LanguageEnricherProvider create() {
        return new GoLanguageEnricherProviderImpl();
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(GO);
    }

    @Override
    public Language getEnrichedLanguage(Language language, List<File> files) throws IOException {
        Optional<File> goMod = files.stream().filter(file -> file.getName().equalsIgnoreCase("go.mod")).findFirst();

        if (goMod.isPresent()) {
            String goVersion = getGoVersion(goMod.get());
            if (!goVersion.isEmpty()) {
                language.setTools(Arrays.asList(goVersion));
            }
            language.setFrameworks(getFrameworks(goMod.get()));
        }

        return language;
    }

    private String getGoVersion(File goMod) throws IOException {
        Pattern goVersionLine = Pattern.compile("^go\\s+");
        try (FileReader fr = new FileReader(goMod)) {
            try(BufferedReader br = new BufferedReader(fr)) {
                String line;
                while ((line = br.readLine()) != null) {
                    if(goVersionLine.matcher(line).find()) {
                        String[] version = goVersionLine.split(line);
                        return version.length > 0 ? version[1] : "";
                    }
                }
            }
        }
        return "";
    }

    private List<String> getFrameworks(File file) throws IOException {
        List<String> frameworks = new ArrayList<>();
        ServiceLoader<FrameworkDetectorProvider> loader = ServiceLoader.load(FrameworkDetectorProvider.class, GoLanguageEnricherProviderImpl.class.getClassLoader());
        for (FrameworkDetectorProvider provider : loader) {
            if (provider instanceof GoFrameworkDetectorProvider) {
                GoFrameworkDetectorProvider configProvider = (GoFrameworkDetectorProvider) provider.create();
                if (configProvider.hasFramework(file)) {
                    frameworks.addAll(configProvider.getFrameworks());
                }
            }
        }
        return frameworks;
    }
}
