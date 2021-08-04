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
import java.util.List;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class ComponentRecognizerTest extends AbstractRecognizerTest {
    private ComponentRecognizerImpl recognizer;

    @Before
    public void setup() {
        recognizer = new RecognizerBuilder().componentRecognizer();
    }

    @Test
    public void testSingleComponent() throws IOException {
        List<Component> components = recognizer.analyze(".", devfileTypes);
        assertEquals(1, components.size());
    }

    @Test
    public void testMultipleComponents() throws IOException {
        List<Component> components = recognizer.analyze(new File("../../resources/projects").getCanonicalPath(), devfileTypes);
        assertEquals(5, components.size());
    }
}
