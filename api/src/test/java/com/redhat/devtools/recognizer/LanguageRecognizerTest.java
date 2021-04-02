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
package com.redhat.devtools.recognizer;

import com.redhat.devtools.recognizer.api.DevfileType;
import com.redhat.devtools.recognizer.api.Language;
import com.redhat.devtools.recognizer.api.LanguageRecognizer;
import com.redhat.devtools.recognizer.api.LanguageRecognizerBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class LanguageRecognizerTest extends AbstractRecognizerTest {
    private LanguageRecognizer recognizer;


    @Before
    public void setup() {
        recognizer = new LanguageRecognizerBuilder().build();
    }

    @Test
    public void testMySelf() throws IOException {
        List<Language> status = recognizer.analyze(".");
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("JAVA")));
    }

    @Test
    public void testJAVAMetaDevFile() throws IOException {
        DevfileType devFile = recognizer.selectDevFileFromTypes("src/test/resources/projects/micronaut", devfileTypes);
        assertTrue(devFile.getName().equalsIgnoreCase("java-maven"));
    }

    @Test
    public void testQuarkus() throws IOException {
        List<Language> status = recognizer.analyze("src/test/resources/projects/quarkus");
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("JAVA")));
    }

    @Test
    public void testQuarkusDevFileType() throws IOException {
        DevfileType devFile = recognizer.selectDevFileFromTypes("src/test/resources/projects/quarkus", devfileTypes);
        assertTrue(devFile.getName().equalsIgnoreCase("java-quarkus"));
    }

    @Test
    public void testMicronaut() throws IOException {
        List<Language> status = recognizer.analyze("src/test/resources/projects/micronaut");
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("JAVA")));
    }

    @Test
    public void testMicronautDevFile() throws IOException {
        DevfileType devFile = recognizer.selectDevFileFromTypes("src/test/resources/projects/micronaut", devfileTypes);
        assertTrue(devFile.getName().equalsIgnoreCase("java-maven"));
    }

    @Test
    public void testNode() throws IOException {
        List<Language> status = recognizer.analyze("src/test/resources/projects/nodejs-ex");
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("JavaScript")));
    }

    @Test
    public void testNodeDevFile() throws IOException {
        DevfileType devFile = recognizer.selectDevFileFromTypes("src/test/resources/projects/nodejs-ex", devfileTypes);
        assertTrue(devFile.getName().equalsIgnoreCase("nodejs"));
    }


    @Test
    public void testDjango() throws IOException {
        List<Language> status = recognizer.analyze("src/test/resources/projects/django");
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("Python")));
    }

    @Test
    public void testDjangoDevFile() throws IOException {
        DevfileType devFile = recognizer.selectDevFileFromTypes("src/test/resources/projects/django", devfileTypes);
        assertTrue(devFile.getName().equalsIgnoreCase("python-django"));
    }
}
