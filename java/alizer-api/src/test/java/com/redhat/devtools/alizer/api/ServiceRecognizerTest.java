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
import static org.junit.Assert.assertNull;

public class ServiceRecognizerTest {
    private ServiceRecognizerImpl recognizer;

    @Before
    public void setup() {
        recognizer = new RecognizerFactory().createServiceRecognizer();
    }

    @Test
    public void testDjangoComponent() throws IOException {
        List<Component> components = recognizer.analyze("/home/luca/Public/github.com/labelmatter/lm-api");
        assertEquals(1, components.size());
    }

    @Test
    public void testNodejsComponent() throws IOException {
        List<Component> components = recognizer.analyze("/home/luca/Public/github.com/other/test-services");
        assertEquals(1, components.size());
    }
}
