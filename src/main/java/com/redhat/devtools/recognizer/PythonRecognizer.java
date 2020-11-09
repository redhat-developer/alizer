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
package com.redhat.devtools.recognizer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FilenameUtils;

public class PythonRecognizer {

    private final static String DJANGO_TAG = "from django.";

    public static String getPython(List<String> files) throws IOException {
        String result = "PYTHON ";

        // check django
        Optional<String> manage = files.stream().filter(file -> FilenameUtils.getName(file).equalsIgnoreCase("manage.py")).findFirst();
        Optional<String> urls = files.stream().filter(file -> FilenameUtils.getName(file).equalsIgnoreCase("urls.py")).findFirst();
        Optional<String> wsgi = files.stream().filter(file -> FilenameUtils.getName(file).equalsIgnoreCase("wsgi.py")).findFirst();
        Optional<String> asgi = files.stream().filter(file -> FilenameUtils.getName(file).equalsIgnoreCase("asgi.py")).findFirst();

        boolean manageIsDjango = manage.isPresent() && Utils.IsTagInFile(manage.get(), DJANGO_TAG);
        boolean urlsIsDjango = urls.isPresent() && Utils.IsTagInFile(urls.get(), DJANGO_TAG);
        boolean wsgiIsDjango = wsgi.isPresent() && Utils.IsTagInFile(wsgi.get(), DJANGO_TAG);
        boolean asgiIsDjango = asgi.isPresent() && Utils.IsTagInFile(asgi.get(), DJANGO_TAG);

        if (manageIsDjango || urlsIsDjango || wsgiIsDjango || asgiIsDjango) {
            result += "Django ";
        }

        return result;
    }
}
