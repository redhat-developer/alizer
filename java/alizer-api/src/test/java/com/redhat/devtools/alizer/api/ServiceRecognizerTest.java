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

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class ServiceRecognizerTest extends AbstractRecognizerTest {
    private ServiceRecognizerImpl recognizer;

    @Before
    public void setup() {
        recognizer = new RecognizerFactory().createServiceRecognizer();
    }

    @Test
    public void testDjangoComponent() throws IOException {
        Map<Path, List<Service>> services = recognizer.analyze("/home/luca/Public/github.com/labelmatter/lm-api");
        assertEquals(1, services.size());
    }

    @Test
    public void testNodejsComponent() throws IOException {
        Map<Path, List<Service>> services = recognizer.analyze("/home/luca/Public/github.com/other/test-services");
        assertEquals(1, services.size());
    }

    @Test
    public void testSelfComponent() throws IOException {
        Map<Path, List<Service>> components = recognizer.analyze("/home/luca/Public/github.com/other/vertx-postgresql-starter");
        assertEquals(1, 1);
    }
}
