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
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertTrue;

public class LanguageRecognizerTest {
    private LanguageRecognizer recognizer;
    private List<DevfileType> devfileTypes;
    private List<File> devfiles;

    @Before
    public void setup() {
        recognizer = new LanguageRecognizerBuilder().build();
        devfileTypes = Arrays.asList(
                new DevfileType("devfile1", "java", Arrays.asList("Java", "Maven")),
                new DevfileType("devfile2", "java", Collections.emptyList()),
                new DevfileType("devfile3", "java", Arrays.asList("Java", "Quarkus")),
                new DevfileType("devfile4", "java", Arrays.asList("Java", "Spring")),
                new DevfileType("devfile5", "java", Arrays.asList("Java", "Vert.x")),
                new DevfileType("devfile6", "java", Arrays.asList("RHEL8", "Java", "OpenJDK", "Maven", "WildFly", "Microprofile", "WildFly Bootable")),
                new DevfileType("devfile7", "java", Arrays.asList("Java", "WildFly")),
                new DevfileType("devfile8", "nodejs", Arrays.asList("NodeJS", "Express", "ubi8")),
                new DevfileType("devfile9", "python", Arrays.asList("Python", "pip", "Django")),
                new DevfileType("devfile10", "python", Arrays.asList("Python", "pip"))
        );
        devfiles = Arrays.asList(
                new File("src/test/resources/devfiles/quarkus.yaml"),
                new File("src/test/resources/devfiles/java.yaml"),
                new File("src/test/resources/devfiles/nodejs.yaml"),
                new File("src/test/resources/devfiles/django.yaml"),
                new File("src/test/resources/devfiles/python.yaml")
        );
    }

    @Test
    public void testMySelf() throws IOException {
        List<Language> status = recognizer.analyze(".");
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("JAVA")));
    }

    @Test
    public void testJAVAMetaDevFile() throws IOException {
        String devFile = recognizer.selectDevFile("src/test/resources/projects/micronaut", Collections.emptyList(), devfiles);
        assertTrue(devFile.equalsIgnoreCase("java-maven"));
    }

    @Test
    public void testQuarkus() throws IOException {
        List<Language> status = recognizer.analyze("src/test/resources/projects/quarkus");
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("JAVA")));
    }

    @Test
    public void testQuarkusDevFileType() throws IOException {
        String devFile = recognizer.selectDevFileFromTypes("src/test/resources/projects/quarkus", devfileTypes);
        assertTrue(devFile.equalsIgnoreCase("devfile3"));
    }

    @Test
    public void testQuarkusMetaDevFile() throws IOException {
        String devFile = recognizer.selectDevFile("src/test/resources/projects/quarkus", Collections.emptyList(), devfiles);
        assertTrue(devFile.equalsIgnoreCase("java-quarkus"));
    }

    @Test
    public void testMicronaut() throws IOException {
        List<Language> status = recognizer.analyze("src/test/resources/projects/micronaut");
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("JAVA")));
    }

    @Test
    public void testMicronautDevFile() throws IOException {
        String devFile = recognizer.selectDevFileFromTypes("src/test/resources/projects/micronaut", devfileTypes);
        assertTrue(devFile.equalsIgnoreCase("devfile1"));
    }

    @Test
    public void testNode() throws IOException {
        List<Language> status = recognizer.analyze("src/test/resources/projects/nodejs-ex");
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("JavaScript")));
    }

    @Test
    public void testNodeDevFile() throws IOException {
        String devFile = recognizer.selectDevFileFromTypes("src/test/resources/projects/nodejs-ex", devfileTypes);
        assertTrue(devFile.equalsIgnoreCase("devfile8"));
    }

    @Test
    public void testNodejsMetaDevFile() throws IOException {
        String devFile = recognizer.selectDevFile("src/test/resources/projects/nodejs-ex", Collections.emptyList(), devfiles);
        assertTrue(devFile.equalsIgnoreCase("nodejs"));
    }

    @Test
    public void testDjango() throws IOException {
        List<Language> status = recognizer.analyze("src/test/resources/projects/django");
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("Python")));
    }

    @Test
    public void testDjangoDevFile() throws IOException {
        String devFile = recognizer.selectDevFileFromTypes("src/test/resources/projects/django", devfileTypes);
        assertTrue(devFile.equalsIgnoreCase("devfile9"));
    }

    @Test
    public void testDjangoMetaDevFile() throws IOException {
        String devFile = recognizer.selectDevFile("src/test/resources/projects/django", Collections.emptyList(), devfiles);
        assertTrue(devFile.equalsIgnoreCase("python-django"));
    }
}
