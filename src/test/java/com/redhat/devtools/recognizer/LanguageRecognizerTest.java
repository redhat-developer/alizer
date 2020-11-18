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

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertTrue;

public class LanguageRecognizerTest {

    @Test
    public void testMySelf() {
        String status = LanguageRecognizer.analyze(".");
        assertTrue(status.contains("JAVA"));
    }

    @Test
    public void testQuarkus() {
        String status = LanguageRecognizer.analyze("src/test/resources/projects/quarkus");
        assertTrue(status.contains("JAVA"));
    }

    @Test
    public void testMicronaut() {
        String status = LanguageRecognizer.analyze("src/test/resources/projects/micronaut");
        assertTrue(status.contains("JAVA"));
    }

    @Test
    public void testNode() {
        String status = LanguageRecognizer.analyze("src/test/resources/projects/nodejs-ex");
        assertTrue(status.contains("JavaScript"));
    }
}
