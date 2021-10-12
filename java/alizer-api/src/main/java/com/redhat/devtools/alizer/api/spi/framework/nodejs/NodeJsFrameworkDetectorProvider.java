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
package com.redhat.devtools.alizer.api.spi.framework.nodejs;

import com.redhat.devtools.alizer.api.Service;
import com.redhat.devtools.alizer.api.model.service.DependencyDescriptor;
import com.redhat.devtools.alizer.api.model.service.ServiceDescriptor;
import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorWithConfigFileProvider;
import com.redhat.devtools.alizer.api.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


import static com.redhat.devtools.alizer.api.Constants.JAVASCRIPT;
import static com.redhat.devtools.alizer.api.Constants.NODEJS;
import static com.redhat.devtools.alizer.api.Constants.TYPESCRIPT;

public abstract class NodeJsFrameworkDetectorProvider extends FrameworkDetectorWithConfigFileProvider {
    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(JAVASCRIPT, TYPESCRIPT);
    }

    protected abstract String getFrameworkTag();

    @Override
    public boolean hasFramework(File file) throws IOException {
        return getDependenciesFromPackageJson(file).keySet().stream().anyMatch(dependency -> dependency.equals(getFrameworkTag()));
    }

    private Map<?, ?> getDependenciesFromPackageJson(File file) throws IOException {
        Map<?, ?> packageJsonContent = Utils.getJsonFileAsMap(file);
        if (!packageJsonContent.containsKey("dependencies")) {
            throw new IOException("No package.json found");
        }
        return (Map<?, ?>) packageJsonContent.get("dependencies");
    }

    @Override
    public List<Service> getServices(Path root, File config) throws IOException {
        List<Service> services = new ArrayList<>();
        List<ServiceDescriptor> descriptors = getServicesDescriptor(Collections.singletonList(NODEJS));
        Map<?, ?> dependenciesMap = getDependenciesFromPackageJson(config);
        dependenciesMap.keySet().forEach(dependency -> {
            Service service = getServiceByDependency(dependency.toString(), descriptors);
            if (service != null) {
                services.add(service);
            }
        });

        return services;
    }

    private Service getServiceByDependency(String dependency, List<ServiceDescriptor> descriptors) {
        for (ServiceDescriptor serviceDescriptor: descriptors) {
            for (DependencyDescriptor dependencyDescriptor: serviceDescriptor.getAllDependenciesDescriptor()) {
                if (dependencyDescriptor.getName().equalsIgnoreCase(dependency)) {
                    return serviceDescriptor.getService();
                }
            }
        }
        return null;
    }
}
