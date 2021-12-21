/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.redhat.devtools.alizer.api;

import java.io.File;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class LanguageRecognizerTest extends AbstractRecognizerTest {
    private LanguageRecognizer recognizer;

    @Before
    public void setup() {
        recognizer = new RecognizerFactory().createLanguageRecognizer();
    }

    @Test
    public void testMySelf() throws IOException {
        List<Language> status = recognizer.analyze(".");
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("JAVA")));
    }

    @Test
    public void testQuarkus() throws IOException {
        List<Language> status = recognizer.analyze(new File("../../resources/projects/quarkus").getCanonicalPath());
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("JAVA")));
    }

    @Test
    public void testMicronaut() throws IOException {
        List<Language> status = recognizer.analyze(new File("../../resources/projects/micronaut").getCanonicalPath());
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("JAVA")));
    }

    @Test
    public void testNode() throws IOException {
        List<Language> status = recognizer.analyze(new File("../../resources/projects/nodejs-ex").getCanonicalPath());
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("JavaScript")));
    }

    @Test
    public void testDjango() throws IOException {
        List<Language> status = recognizer.analyze(new File("../../resources/projects/django").getCanonicalPath());
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("Python")));
    }
}
