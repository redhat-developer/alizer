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
package com.redhat.devtools.alizer.api.spi.framework.python;

import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorWithoutConfigFileProvider;
import com.redhat.devtools.alizer.api.utils.DocumentParser;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static com.redhat.devtools.alizer.api.Constants.DJANGO;
import static com.redhat.devtools.alizer.api.Constants.PYTHON;

public class DjangoFrameworkDetectorProviderImpl implements FrameworkDetectorWithoutConfigFileProvider {
    private static final String DJANGO_TAG = "from django.";

    @Override
    public FrameworkDetectorWithoutConfigFileProvider create() {
        return new DjangoFrameworkDetectorProviderImpl();
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(PYTHON);
    }

    @Override
    public List<String> getFrameworks() {
        return Arrays.asList(DJANGO);
    }

    @Override
    public boolean hasFramework(List<File> files) throws IOException {
        Optional<File> manage = files.stream().filter(file -> file.getName().equalsIgnoreCase("manage.py")).findFirst();
        Optional<File> urls = files.stream().filter(file -> file.getName().equalsIgnoreCase("urls.py")).findFirst();
        Optional<File> wsgi = files.stream().filter(file -> file.getName().equalsIgnoreCase("wsgi.py")).findFirst();
        Optional<File> asgi = files.stream().filter(file -> file.getName().equalsIgnoreCase("asgi.py")).findFirst();

        boolean manageIsDjango = manage.isPresent() && DocumentParser.isTagInFile(manage.get(), DJANGO_TAG);
        boolean urlsIsDjango = urls.isPresent() && DocumentParser.isTagInFile(urls.get(), DJANGO_TAG);
        boolean wsgiIsDjango = wsgi.isPresent() && DocumentParser.isTagInFile(wsgi.get(), DJANGO_TAG);
        boolean asgiIsDjango = asgi.isPresent() && DocumentParser.isTagInFile(asgi.get(), DJANGO_TAG);

        return manageIsDjango || urlsIsDjango || wsgiIsDjango || asgiIsDjango;
    }
}
