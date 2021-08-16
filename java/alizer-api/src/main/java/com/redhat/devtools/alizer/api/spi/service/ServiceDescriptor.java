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

import com.redhat.devtools.alizer.api.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceDescriptor {

    private Service service;
    private Map<String, List<DependencyDescriptor>> dependencies;

    public ServiceDescriptor(String name, List<String> operators, Map<String, List<DependencyDescriptor>> dependencies) {
        this.service = new Service(name, operators);
        this.dependencies = dependencies;
    }

    public Service getService() {
        return service;
    }

    public List<String> getOperators() {
        return service.getOperators();
    }

    public Map<String, List<DependencyDescriptor>> getDependencies() {
        return dependencies;
    }

    public List<DependencyDescriptor> getAllDependenciesDescriptor() {
        List<DependencyDescriptor> dependencyDescriptors = new ArrayList<>();
        dependencies.values().forEach(dependencyDescriptors::addAll);
        return dependencyDescriptors;
    }

}
