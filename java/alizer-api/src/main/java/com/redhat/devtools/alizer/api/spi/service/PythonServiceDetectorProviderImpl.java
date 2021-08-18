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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PythonServiceDetectorProviderImpl extends ServiceDetectorProvider {
    @Override
    public ServiceDetectorProvider create() {
        return new PythonServiceDetectorProviderImpl();
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList("PYTHON");
    }

    @Override
    public List<Service> getServices(Path root, Language language) {
        Set<Service> services = new HashSet<>();
        try {
            List<ServiceDescriptor> descriptors = getServicesDescriptor(Collections.singletonList("python"));
            List<Path> allPythonFiles = Files.walk(root, Integer.MAX_VALUE)
                    .filter(path -> Files.isRegularFile(path) && path.toString().toLowerCase().endsWith(".py"))
                    .collect(Collectors.toList());
            for (Path path: allPythonFiles) {
                services.addAll(getServicesFromFile(path, descriptors));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(services);
    }

    private Set<Service> getServicesFromFile(Path file, List<ServiceDescriptor> descriptors) throws IOException {
        if (!file.toFile().exists()) {
            return Collections.emptySet();
        }
        Set<Service> services = new HashSet<>();
        List<String> allLines = Files.readAllLines(file);
        if (isSettingFile(file)) {
            services.addAll(getServicesFromLines(allLines, descriptors));
        } else {
            List<String> allImportsLines = allLines.stream()
                    .filter(line -> Pattern.matches("^\\s*(import|from)\\s*.*", line))
                    .collect(Collectors.toList());
            services.addAll(getServicesFromLines(allImportsLines, descriptors));
        }
        return services;
    }

    private Set<Service> getServicesFromLines(List<String> lines, List<ServiceDescriptor> descriptors) {
        Set<Service> services = new HashSet<>();
        for(String line: lines) {
            Service service = getServiceFromLine(line, descriptors);
            if (service != null) {
                services.add(service);
            }
        }
        return services;
    }

    private Service getServiceFromLine(String line, List<ServiceDescriptor> descriptors) {
        for (ServiceDescriptor serviceDescriptor: descriptors) {
            for (DependencyDescriptor dependencyDescriptor: serviceDescriptor.getAllDependenciesDescriptor()) {
                if (line.toLowerCase().contains(dependencyDescriptor.getName())) {
                    return serviceDescriptor.getService();
                }
            }
        }
        return null;
    }

    private boolean isSettingFile(Path file) {
        return file.endsWith("database.py") || file.endsWith("settings.py");
    }
}
