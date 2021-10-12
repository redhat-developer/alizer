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
package com.redhat.devtools.alizer.api.spi.framework.python;

import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorWithoutConfigFileProvider;
import com.redhat.devtools.alizer.api.utils.DocumentParser;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import com.redhat.devtools.alizer.api.Service;
import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorWithoutConfigFileProvider;
import com.redhat.devtools.alizer.api.model.service.DependencyDescriptor;
import com.redhat.devtools.alizer.api.model.service.ServiceDescriptor;
import com.redhat.devtools.alizer.api.utils.DocumentParser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


import static com.redhat.devtools.alizer.api.Constants.DJANGO;
import static com.redhat.devtools.alizer.api.Constants.PYTHON;

public class DjangoFrameworkDetectorProviderImpl extends FrameworkDetectorWithoutConfigFileProvider {
    private static final String DJANGO_TAG = "from django.";

    @Override
    public FrameworkDetectorWithoutConfigFileProvider create() {
        return new DjangoFrameworkDetectorProviderImpl();
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(PYTHON);
    }

    @Override
    public List<String> getFrameworks() {
        return Arrays.asList(DJANGO);
    }

    @Override
    public boolean hasFramework(List<File> files) throws IOException {
        Optional<File> manage = files.stream().filter(file -> file.getName().equalsIgnoreCase("manage.py")).findFirst();
        Optional<File> urls = files.stream().filter(file -> file.getName().equalsIgnoreCase("urls.py")).findFirst();
        Optional<File> wsgi = files.stream().filter(file -> file.getName().equalsIgnoreCase("wsgi.py")).findFirst();
        Optional<File> asgi = files.stream().filter(file -> file.getName().equalsIgnoreCase("asgi.py")).findFirst();

        boolean manageIsDjango = manage.isPresent() && DocumentParser.isTagInFile(manage.get(), DJANGO_TAG);
        boolean urlsIsDjango = urls.isPresent() && DocumentParser.isTagInFile(urls.get(), DJANGO_TAG);
        boolean wsgiIsDjango = wsgi.isPresent() && DocumentParser.isTagInFile(wsgi.get(), DJANGO_TAG);
        boolean asgiIsDjango = asgi.isPresent() && DocumentParser.isTagInFile(asgi.get(), DJANGO_TAG);

        return manageIsDjango || urlsIsDjango || wsgiIsDjango || asgiIsDjango;
    }

    @Override
    public List<Service> getServices(Path root) throws IOException {
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
