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
package com.redhat.devtools.alizer.registry.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistryProxyTest {
    @Test
    void checkDevfileRegistry() throws IOException {
        DevfileRegistryProxy proxy = new DevfileRegistryProxy("https://registry.devfile.io");
        List<DevfileMetadata> devfiles = new ArrayList<>();
        proxy.collect(devfiles);
        assertFalse(devfiles.isEmpty());
    }

    @Test
    void checkQuarkusDevfileRegistry() throws IOException {
        DevfileRegistryProxy proxy = new DevfileRegistryProxy("https://registry.devfile.io");
        List<DevfileMetadata> devfiles = new ArrayList<>();
        proxy.collect(devfiles);
        DevfileMetadata devfile = devfiles.stream().filter(file -> file.getName().equals("java-quarkus")).findFirst().get();
        assertFalse(devfile.getVersion().isEmpty());
        Assertions.assertEquals("Quarkus Java", devfile.getDisplayName());
        Assertions.assertFalse(devfile.getDescription().isEmpty());
        assertFalse(devfile.getTags().isEmpty());
        assertEquals("quarkus", devfile.getProjectType());
        assertEquals("java", devfile.getLanguage());
        assertFalse(devfile.getLinks().isEmpty());
        assertTrue(devfile.getLinks().containsKey("self"));
        assertFalse(devfile.getResources().isEmpty());
        assertFalse(devfile.getStarterProjects().isEmpty());
    }

    @Test
    void checkOdoRegistry() throws IOException {
        DevfileRegistryProxy proxy = new DevfileRegistryProxy("https://github.com/odo-devfiles/registry");
        List<DevfileMetadata> devfiles = new ArrayList<>();
        proxy.collect(devfiles);
        assertFalse(devfiles.isEmpty());
    }

    @Test
    void checkQuarkusOdoRegistry() throws IOException {
        DevfileRegistryProxy proxy = new DevfileRegistryProxy("https://github.com/odo-devfiles/registry");
        List<DevfileMetadata> devfiles = new ArrayList<>();
        proxy.collect(devfiles);
        DevfileMetadata devfile = devfiles.stream().filter(file -> file.getName().equals("java-quarkus")).findFirst().get();
        assertNull(devfile.getVersion());
        Assertions.assertEquals("Quarkus Java", devfile.getDisplayName());
        Assertions.assertFalse(devfile.getDescription().isEmpty());
        assertFalse(devfile.getTags().isEmpty());
        assertEquals("quarkus", devfile.getProjectType());
        assertEquals("java", devfile.getLanguage());
        assertFalse(devfile.getLinks().isEmpty());
        assertTrue(devfile.getLinks().containsKey("self"));
        assertTrue(devfile.getResources().isEmpty());
        assertTrue(devfile.getStarterProjects().isEmpty());
    }
}
