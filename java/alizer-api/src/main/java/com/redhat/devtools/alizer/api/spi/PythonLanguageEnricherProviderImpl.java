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
import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorWithoutConfigFileProvider;
import com.redhat.devtools.alizer.api.spi.framework.java.JavaFrameworkDetectorProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.ServiceLoader;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.xml.sax.SAXException;

public class PythonLanguageEnricherProviderImpl implements LanguageEnricherProvider {
    @Override
    public LanguageEnricherProvider create() {
        return new PythonLanguageEnricherProviderImpl();
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList("PYTHON");
    }

    @Override
    public Language getEnrichedLanguage(Language language, List<File> files) throws IOException {
        language.setFrameworks(getFrameworks(files));
        return language;
    }

    private List<String> getFrameworks(List<File> files) throws IOException {
        List<String> frameworks = new ArrayList<>();
        ServiceLoader<FrameworkDetectorWithoutConfigFileProvider> loader = ServiceLoader.load(FrameworkDetectorWithoutConfigFileProvider.class, LanguageRecognizerImpl.class.getClassLoader());
        for (FrameworkDetectorWithoutConfigFileProvider provider : loader) {
            provider = provider.create();
            if (provider.hasFramework(files)) {
                frameworks.addAll(provider.getFrameworks());
            }
        }
        return frameworks;
    }

}
