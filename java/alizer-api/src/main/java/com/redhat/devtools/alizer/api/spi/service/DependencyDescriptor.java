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
package com.redhat.devtools.alizer.api.spi.service;

import java.util.HashMap;
import java.util.Map;

public class DependencyDescriptor {

    private String name;
    private Map<String, String> attributes;

    public DependencyDescriptor() {
        this.attributes = new HashMap<>();
    }

    public DependencyDescriptor(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }
}
