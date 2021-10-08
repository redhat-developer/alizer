/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.redhat.devtools.alizer.api;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertTrue;

public class LanguageScoreTest extends AbstractRecognizerTest {
    @Test
    public void checkUnknownLanguageHasZero() {
        Language lang = new Language("Unknown", Collections.emptyList(), 1.0, false);
        LanguageScore score = new LanguageScore(lang, JAVA);
        Assert.assertEquals(0, score.getScore());
    }

    @Test
    public void checkFinestDevfilehAsHighestScore() {
        Language lang = new Language("java", Collections.emptyList(), 1.0, false);
        lang.setFrameworks(Collections.singletonList("quarkus"));
        lang.setTools(Collections.singletonList("maven"));
        LanguageScore javaScore = new LanguageScore(lang, JAVA);
        LanguageScore quarkusScore = new LanguageScore(lang, JAVA_QUARKUS);
        assertTrue(quarkusScore.getScore() > javaScore.getScore());
    }
}
