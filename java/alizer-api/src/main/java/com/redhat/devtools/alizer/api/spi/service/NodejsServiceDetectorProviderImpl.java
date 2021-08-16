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

import com.redhat.devtools.alizer.api.Language;
import com.redhat.devtools.alizer.api.Service;
import com.redhat.devtools.alizer.api.utils.Utils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NodejsServiceDetectorProviderImpl extends ServiceDetectorProvider {
    @Override
    public ServiceDetectorProvider create() {
        return new NodejsServiceDetectorProviderImpl();
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList("Javascript", "Typescript");
    }


    @Override
    public Set<Service> getServices(Path root, Language language) {
        try {
            List<ServiceDescriptor> descriptors = getServicesDescriptor(Collections.singletonList("nodejs"));
            return getServices(root.resolve("package.json"), descriptors);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptySet();
    }

    private Set<Service> getServices(Path packageJsonFile, List<ServiceDescriptor> descriptors) throws IOException {
        Set<Service> services = new HashSet<>();
        Map packageJsonContent = Utils.getJsonFileAsMap(packageJsonFile.toFile());
        if (packageJsonContent.containsKey("dependencies")) {
            ((Map)packageJsonContent.get("dependencies")).keySet().forEach(dependency -> {
                Service service = getServiceByDependency(dependency.toString(), descriptors);
                if (service != null) {
                    services.add(service);
                }
            });
        }
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
