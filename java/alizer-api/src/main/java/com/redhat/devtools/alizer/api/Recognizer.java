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
package com.redhat.devtools.alizer.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Recognizer {
    public Recognizer(RecognizerBuilder builder) {}

    protected List<File> getFiles(Path rootDirectory) throws IOException {
        return Files.walk(rootDirectory, Integer.MAX_VALUE).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
    }
}
