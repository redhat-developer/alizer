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
import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorProvider;
import com.redhat.devtools.alizer.api.spi.framework.nodejs.NodeJsFrameworkDetectorProvider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;


import static com.redhat.devtools.alizer.api.Constants.JAVASCRIPT;
import static com.redhat.devtools.alizer.api.Constants.TYPESCRIPT;

public class JavaScriptLanguageEnricherProviderImpl extends LanguageEnricherProvider {
    @Override
    public LanguageEnricherProvider create() {
        return new JavaScriptLanguageEnricherProviderImpl();
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(JAVASCRIPT, TYPESCRIPT);
    }

    @Override
    public Language getEnrichedLanguage(Language language, List<File> files) throws IOException {
        Optional<File> packageJson = files.stream().filter(file -> file.getName().equalsIgnoreCase("package.json")).findFirst();

        if (packageJson.isPresent()) {
            language.setTools(Arrays.asList("nodejs"));
            language.setFrameworks(getFrameworks(packageJson.get()));
        }

        return language;
    }

    private List<String> getFrameworks(File file) throws IOException {
        List<String> frameworks = new ArrayList<>();
        ServiceLoader<FrameworkDetectorProvider> loader = ServiceLoader.load(FrameworkDetectorProvider.class, JavaScriptLanguageEnricherProviderImpl.class.getClassLoader());
        for (FrameworkDetectorProvider provider : loader) {
            if (provider instanceof NodeJsFrameworkDetectorProvider) {
                NodeJsFrameworkDetectorProvider configProvider = (NodeJsFrameworkDetectorProvider) ((NodeJsFrameworkDetectorProvider) provider).create();
                if (configProvider.hasFramework(file)) {
                    frameworks.addAll(configProvider.getFrameworks());
                }
            }
        }
        return frameworks;
    }
}
