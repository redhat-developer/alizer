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
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


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
        return language;
    }
}
