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
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;


import static com.redhat.devtools.alizer.api.Constants.PYTHON;

public class PythonLanguageEnricherProviderImpl extends LanguageEnricherProvider {
    @Override
    public LanguageEnricherProvider create() {
        return new PythonLanguageEnricherProviderImpl();
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(PYTHON);
    }

    @Override
    public Language getEnrichedLanguage(Language language, List<Path> files) throws IOException {
        language.setFrameworks(getFrameworks(files));
        return language;
    }

    private List<String> getFrameworks(List<Path> files) throws IOException {
        List<String> frameworks = new ArrayList<>();
        ServiceLoader<FrameworkDetectorProvider> loader = ServiceLoader.load(FrameworkDetectorProvider.class, PythonLanguageEnricherProviderImpl.class.getClassLoader());
        for (FrameworkDetectorProvider provider : loader) {
            if (provider instanceof FrameworkDetectorWithoutConfigFileProvider) {
                FrameworkDetectorWithoutConfigFileProvider noConfigProvider = (FrameworkDetectorWithoutConfigFileProvider) provider.create();
                if (noConfigProvider.getSupportedLanguages().contains(PYTHON) && noConfigProvider.hasFramework(files)) {
                    frameworks.addAll(noConfigProvider.getFrameworks());
                }
            }
        }
        return frameworks;
    }

}
