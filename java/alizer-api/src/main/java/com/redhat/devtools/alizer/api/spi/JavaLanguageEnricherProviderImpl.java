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
package com.redhat.devtools.alizer.api.spi;

import com.redhat.devtools.alizer.api.Language;
import com.redhat.devtools.alizer.api.LanguageRecognizerImpl;
import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorWithConfigFileProvider;
import com.redhat.devtools.alizer.api.spi.framework.java.JavaFrameworkDetectorProvider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;


import static com.redhat.devtools.alizer.api.Constants.JAVA;

public class JavaLanguageEnricherProviderImpl implements LanguageEnricherProvider {
    @Override
    public LanguageEnricherProvider create() {
        return new JavaLanguageEnricherProviderImpl();
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(JAVA);
    }

    @Override
    public Language getEnrichedLanguage(Language language, List<File> files) throws IOException {
        // find builder
        Optional<File> gradle = files.stream().filter(file -> file.getName().equalsIgnoreCase("build.gradle")).findFirst();
        Optional<File> maven = files.stream().filter(file -> file.getName().equalsIgnoreCase("pom.xml")).findFirst();
        Optional<File> ant = files.stream().filter(file -> file.getName().equalsIgnoreCase("build.xml")).findFirst();

        if (gradle.isPresent()) {
            language.setTools(Arrays.asList("Gradle"));
            language.setFrameworks(getFrameworks(gradle.get()));
        } else if (maven.isPresent()) {
            language.setTools(Arrays.asList("Maven"));
            language.setFrameworks(getFrameworks(maven.get()));
        } else if (ant.isPresent()) {
            language.setTools(Arrays.asList("Ant"));
        }

        return language;
    }

    private List<String> getFrameworks(File file) throws IOException {
        List<String> frameworks = new ArrayList<>();
        ServiceLoader<JavaFrameworkDetectorProvider> loader = ServiceLoader.load(JavaFrameworkDetectorProvider.class, LanguageRecognizerImpl.class.getClassLoader());
        for (FrameworkDetectorWithConfigFileProvider provider : loader) {
            provider = provider.create();
            if (provider.hasFramework(file)) {
                frameworks.addAll(provider.getFrameworks());
            }
        }
        return frameworks;
    }
}
