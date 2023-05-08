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
import static org.junit.Assert.assertTrue;

public class ComponentRecognizerTest extends AbstractRecognizerTest {
    private ComponentRecognizer recognizer;

    @Before
    public void setup() {
        recognizer = new RecognizerFactory().createComponentRecognizer();
    }

    @Test
    public void testSelfComponent() throws IOException {
        List<Component> components = recognizer.analyze(".");
        assertEquals(1, components.size());
    }

    @Test
    public void testDjangoComponent() throws IOException {
        List<Component> components = recognizer.analyze(new File("../../resources/projects/django").getCanonicalPath());
        assertEquals(1, components.size());
        assertEquals("Python", components.get(0).getLanguages().get(0).getName());
    }

    @Test
    public void testMicronautComponent() throws IOException {
        List<Component> components = recognizer.analyze(new File("../../resources/projects/micronaut").getCanonicalPath());
        assertEquals(1, components.size());
        assertEquals("Java", components.get(0).getLanguages().get(0).getName());
    }

    @Test
    public void testQuarkusComponent() throws IOException {
        List<Component> components = recognizer.analyze(new File("../../resources/projects/quarkus").getCanonicalPath());
        assertEquals(1, components.size());
        assertEquals("Java", components.get(0).getLanguages().get(0).getName());
        assertTrue(components.get(0).getLanguages().get(0).getFrameworks().contains("Quarkus"));
    }

    @Test
    public void testNodeComponent() throws IOException {
        List<Component> components = recognizer.analyze(new File("../../resources/projects/nodejs-ex").getCanonicalPath());
        assertEquals(1, components.size());
        assertEquals("JavaScript", components.get(0).getLanguages().get(0).getName());
    }

    @Test
    public void testSingleComponentWithExistingDevfile() throws IOException {
        List<Component> components = recognizer.analyze(new File("../../resources/projects/nodejs-ex-w-devfile").getCanonicalPath());
        assertEquals(1, components.size());
    }

    @Test
    public void testNoComponent() throws IOException {
        List<Component> components = recognizer.analyze(new File("../../resources/projects/simple").getCanonicalPath());
        assertEquals(0, components.size());
    }

    @Test
    public void testDoubleComponents() throws IOException {
        List<Component> components = recognizer.analyze(new File("../../resources/projects/double-components").getCanonicalPath());
        assertEquals(2, components.size());
        assertEquals("JavaScript", components.get(0).getLanguages().get(0).getName());
        assertEquals("JavaScript", components.get(1).getLanguages().get(0).getName());
    }

    @Test
    public void testWrappedComponents() throws IOException {
        List<Component> components = recognizer.analyze(new File("../../resources/projects/component-wrapped-in-folder").getCanonicalPath());
        assertEquals(1, components.size());
        assertEquals("Java", components.get(0).getLanguages().get(0).getName());
        assertEquals(new File("../../resources/projects/component-wrapped-in-folder/wrapper/quarkus").getCanonicalPath(), components.get(0).getPath().toString());
    }

    @Test
    public void testMultipleComponents() throws IOException {
        List<Component> components = recognizer.analyze(new File("../../resources/projects").getCanonicalPath());
        assertEquals(29, components.size());
        assertEquals(1, components.stream().filter(component -> "python".equalsIgnoreCase(component.getLanguages().get(0).getName())).count());
        assertEquals(2, components.stream().filter(component -> component.getLanguages().get(0).getFrameworks().contains("Quarkus")).count());
        assertEquals(5, components.stream().filter(component -> "javascript".equalsIgnoreCase(component.getLanguages().get(0).getName())).count());
        assertEquals(9, components.stream().filter(component -> "java".equalsIgnoreCase(component.getLanguages().get(0).getName())).count());
        assertEquals(5, components.stream().filter(component -> "c#".equalsIgnoreCase(component.getLanguages().get(0).getName())).count());
        assertEquals(3, components.stream().filter(component -> "go".equalsIgnoreCase(component.getLanguages().get(0).getName())).count());
    }

    @Test
    public void testDotNetComponent() throws IOException {
        List<Component> components = recognizer.analyze(new File("../../resources/projects/s2i-dotnetcore-ex").getCanonicalPath());
        assertEquals(1, components.size());
        assertEquals(1, components.stream().filter(component -> "c#".equalsIgnoreCase(component.getLanguages().get(0).getName())).count());
    }

    @Test
    public void testFSharpComponent() throws IOException {
        List<Component> components = recognizer.analyze(new File("../../resources/projects/net-fsharp").getCanonicalPath());
        assertEquals(1, components.size());
        assertEquals(1, components.stream().filter(component -> "F#".equalsIgnoreCase(component.getLanguages().get(0).getName())).count());
    }

    @Test
    public void testVBComponent() throws IOException {
        List<Component> components = recognizer.analyze(new File("../../resources/projects/net-vb").getCanonicalPath());
        assertEquals(1, components.size());
        assertEquals(1, components.stream().filter(component -> "Visual Basic .NET".equalsIgnoreCase(component.getLanguages().get(0).getName())).count());
    }

    @Test
    public void testGoComponent() throws IOException {
        List<Component> components = recognizer.analyze(new File("../../resources/projects/golang-gin-app").getCanonicalPath());
        assertEquals(1, components.size());
        assertEquals(1, components.stream().filter(component -> "go".equalsIgnoreCase(component.getLanguages().get(0).getName())).count());
    }
}
