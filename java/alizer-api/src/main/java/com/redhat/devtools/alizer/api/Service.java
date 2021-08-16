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

import java.util.List;

public class Service {

    String name;
    List<String> operators;
    String component;

    public Service(String name, List<String> operators) {
        this.name = name;
        this.operators = operators;
    }

    public Service(String name, String component, List<String> operators) {
        this.name = name;
        this.component = component;
        this.operators = operators;
    }

    public String getName() {
        return name;
    }

    public List<String> getOperators() {
        return operators;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    @Override
    public int hashCode() {
        return name.hashCode()
                + (component == null ? 0 : component.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Service
                && ((Service)obj).getName().equalsIgnoreCase(this.name)
                && ((Service)obj).getComponent().equalsIgnoreCase(this.component);
    }
}
