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
package com.redhat.devtools.recognizer.python;

import com.redhat.devtools.recognizer.api.Language;
import com.redhat.devtools.recognizer.api.psi.LanguageEnricher;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FilenameUtils;

public class PythonLanguageEnricherImpl implements LanguageEnricher {

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList("PYTHON");
    }

    @Override
    public Language getEnrichedLanguage(Language language, List<String> files) {
        // check django
        Optional<String> manage = files.stream().filter(file -> FilenameUtils.getName(file).equalsIgnoreCase("manage.py")).findFirst();
        Optional<String> urls = files.stream().filter(file -> FilenameUtils.getName(file).equalsIgnoreCase("urls.py")).findFirst();
        Optional<String> wsgi = files.stream().filter(file -> FilenameUtils.getName(file).equalsIgnoreCase("wsgi.py")).findFirst();
        Optional<String> asgi = files.stream().filter(file -> FilenameUtils.getName(file).equalsIgnoreCase("asgi.py")).findFirst();

        String DJANGO_TAG = "from django.";
        try {
            boolean manageIsDjango = manage.isPresent() && IsTagInFile(manage.get(), DJANGO_TAG);
            boolean urlsIsDjango = urls.isPresent() && IsTagInFile(urls.get(), DJANGO_TAG);
            boolean wsgiIsDjango = wsgi.isPresent() && IsTagInFile(wsgi.get(), DJANGO_TAG);
            boolean asgiIsDjango = asgi.isPresent() && IsTagInFile(asgi.get(), DJANGO_TAG);

            if (manageIsDjango || urlsIsDjango || wsgiIsDjango || asgiIsDjango) {
                language.setFrameworks(Arrays.asList("Django"));
            }

        } catch (IOException e) {}

        return language;
    }

    public boolean IsTagInFile(String file, String tag) throws IOException {
        return Files.readAllLines(Paths.get(file)).stream().anyMatch(line -> line.contains(tag));
    }
}
