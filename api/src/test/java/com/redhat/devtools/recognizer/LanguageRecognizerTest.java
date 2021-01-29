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

import com.redhat.devtools.recognizer.api.Language;
import com.redhat.devtools.recognizer.api.LanguageRecognizer;
import java.util.List;
import org.junit.Test;


import static org.junit.Assert.assertTrue;

public class LanguageRecognizerTest {

    @Test
    public void testMySelf() {
        List<Language> status = LanguageRecognizer.analyze(".");
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("JAVA")));
    }

    @Test
    public void testQuarkus() {
        List<Language> status = LanguageRecognizer.analyze("src/test/resources/projects/quarkus");
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("JAVA")));
    }

    @Test
    public void testMicronaut() {
        List<Language> status = LanguageRecognizer.analyze("src/test/resources/projects/micronaut");
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("JAVA")));
    }

    @Test
    public void testNode() {
        List<Language> status = LanguageRecognizer.analyze("src/test/resources/projects/nodejs-ex");
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("JavaScript")));
    }

    @Test
    public void testDjango() {
        List<Language> status = LanguageRecognizer.analyze("src/test/resources/projects/django");
        assertTrue(status.stream().anyMatch(lang -> lang.getName().equalsIgnoreCase("Python")));
    }
}
