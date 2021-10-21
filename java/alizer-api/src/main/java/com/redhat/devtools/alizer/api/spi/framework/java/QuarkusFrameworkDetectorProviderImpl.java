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
package com.redhat.devtools.alizer.api.spi.framework.java;

import com.redhat.devtools.alizer.api.Service;
import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorWithConfigFileProvider;
import com.redhat.devtools.alizer.api.model.service.ServiceDescriptor;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;


import static com.redhat.devtools.alizer.api.Constants.QUARKUS;

public class QuarkusFrameworkDetectorProviderImpl extends JavaFrameworkDetectorProvider {
    private static final String QUARKUS_TAG = "io.quarkus";

    @Override
    public FrameworkDetectorWithConfigFileProvider create() {
        return new QuarkusFrameworkDetectorProviderImpl();
    }

    @Override
    public List<String> getFrameworks() {
        return Arrays.asList(QUARKUS);
    }

    @Override
    protected String getFrameworkTag() {
        return QUARKUS_TAG;
    }

    @Override
    public List<Service> getServices(Path root, Path configFile) throws IOException {
        List<ServiceDescriptor> descriptors = getServicesDescriptor(getFrameworks());
        Set<Service> services = getServiceFromQuarkusConfigFile(
                root.resolve(Paths.get("src", "main", "resources", "application.properties")),
                descriptors);

        return getServices(configFile, getFrameworks(), services);
    }

    private Set<Service> getServiceFromQuarkusConfigFile(Path file, List<ServiceDescriptor> descriptors) throws IOException {
        return getServiceFromConfigFileInner(file, (line) -> getServiceByTag(descriptors, (configuration) ->
                !configuration.isEmpty() && (line.contains(configuration))));
    }

    private Service getServiceByTag(List<ServiceDescriptor> serviceDescriptors, Function<String, Boolean> isService) {
        return getServiceByTagInner(serviceDescriptors, (attributes) -> {
            String configuration = attributes.getOrDefault("configuration", "");
            return isService.apply(configuration);
        });
    }
}
