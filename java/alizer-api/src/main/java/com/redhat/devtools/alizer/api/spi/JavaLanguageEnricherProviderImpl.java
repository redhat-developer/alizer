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
import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorProvider;
import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorWithConfigFileProvider;
import com.redhat.devtools.alizer.api.spi.framework.java.JavaFrameworkDetectorProvider;
import com.redhat.devtools.alizer.api.utils.DocumentParser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import static com.redhat.devtools.alizer.api.Constants.JAVA;

public class JavaLanguageEnricherProviderImpl extends LanguageEnricherProvider {
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
        ServiceLoader<FrameworkDetectorProvider> loader = ServiceLoader.load(FrameworkDetectorProvider.class, JavaLanguageEnricherProviderImpl.class.getClassLoader());
        for (FrameworkDetectorProvider provider : loader) {
            if (provider instanceof JavaFrameworkDetectorProvider) {
                JavaFrameworkDetectorProvider configProvider = (JavaFrameworkDetectorProvider)provider.create();
                if (configProvider.hasFramework(file)) {
                    frameworks.addAll(configProvider.getFrameworks());
                }
            }
        }
        return frameworks;
    }


    @Override
    public boolean isConfigurationValidForComponent(String language, File file) {
        return super.isConfigurationValidForComponent(language, file) && !isParentModuleMaven(file);
    }

    private boolean isParentModuleMaven(File file) {
        if (!file.toPath().endsWith("pom.xml")) {
            return false;
        }
        try {
            NodeList modules = DocumentParser.getElementsByTag(file, "modules");
            return modules != null && modules.getLength() > 0;
        } catch (IOException | SAXException | ParserConfigurationException e) {
            return false;
        }
    }
}
