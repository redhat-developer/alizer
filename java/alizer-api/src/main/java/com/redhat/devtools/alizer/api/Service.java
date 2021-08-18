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

    private String name;
    private List<String> operators;

    public Service(String name, List<String> operators) {
        this.name = name;
        this.operators = operators;
    }

    public String getName() {
        return name;
    }

    public List<String> getOperators() {
        return operators;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Service
                && ((Service)obj).getName().equals(this.name);
    }
}
