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
import static org.junit.Assert.assertTrue;

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
    public void testMultipleServicesNodeComponent() throws IOException {
        Map<Path, List<Service>> services = recognizer.analyze(new File("../../resources/projects/nodejs-ex").getCanonicalPath());
        assertEquals(1, services.size());
        Map.Entry<Path, List<Service>> entry = services.entrySet().iterator().next();
        assertEquals(2, entry.getValue().size());
        assertTrue(entry.getValue().stream().anyMatch(service -> service.getName().equalsIgnoreCase("mongodb")));
        assertTrue(entry.getValue().stream().anyMatch(service -> service.getName().equalsIgnoreCase("kafka")));
    }

    @Test
    public void testServiceJavaComponent() throws IOException {
        Map<Path, List<Service>> services = recognizer.analyze(new File("../../resources/projects/micronaut").getCanonicalPath());
        assertEquals(1, services.size());
        Map.Entry<Path, List<Service>> entry = services.entrySet().iterator().next();
        assertEquals(1, entry.getValue().size());
        assertTrue(entry.getValue().stream().anyMatch(service -> service.getName().equalsIgnoreCase("mysql")));
    }

    @Test
    public void testServiceQuarkusComponent() throws IOException {
        Map<Path, List<Service>> services = recognizer.analyze(new File("../../resources/projects/quarkus").getCanonicalPath());
        assertEquals(1, services.size());
        Map.Entry<Path, List<Service>> entry = services.entrySet().iterator().next();
        assertEquals(1, entry.getValue().size());
        assertTrue(entry.getValue().stream().anyMatch(service -> service.getName().equalsIgnoreCase("mariadb")));
    }

    @Test
    public void testMultipleServicesPythonComponent() throws IOException {
        Map<Path, List<Service>> services = recognizer.analyze(new File("../../resources/projects/django").getCanonicalPath());
        assertEquals(1, services.size());
        Map.Entry<Path, List<Service>> entry = services.entrySet().iterator().next();
        assertEquals(2, entry.getValue().size());
        assertTrue(entry.getValue().stream().anyMatch(service -> service.getName().equalsIgnoreCase("postgresql")));
        assertTrue(entry.getValue().stream().anyMatch(service -> service.getName().equalsIgnoreCase("mysql")));
    }
}
