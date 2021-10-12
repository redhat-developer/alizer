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

import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorProvider;
import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorWithConfigFileProvider;
import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorWithoutConfigFileProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class ServiceRecognizerImpl extends Recognizer {
    public ServiceRecognizerImpl(RecognizerFactory builder) {
        super(builder);
    }

    public Map<Path, List<Service>> analyze(String path) throws IOException {
        ComponentRecognizer componentRecognizer = new RecognizerFactory().createComponentRecognizer();
        List<Component> components = componentRecognizer.analyze(path);

        Map<Path, List<Service>> services = new HashMap<>();
        for(Component component: components) {
            List<Service> componentServices = getServices(component);
            services.put(component.getPath(), componentServices);
        }
        return services;
    }

    private List<Service> getServices(Component component) throws IOException {
        Language language = component.getLanguages().get(0);

        FrameworkDetectorProvider detector = getServiceDetector(language);
        if (detector != null) {
            if (detector instanceof FrameworkDetectorWithConfigFileProvider) {
                return ((FrameworkDetectorWithConfigFileProvider)detector.create()).getServices(component.getPath(), component.getConfig());
            } else {
                return ((FrameworkDetectorWithoutConfigFileProvider)detector.create()).getServices(component.getPath());
            }
        }
        return Collections.emptyList();
    }

    public static FrameworkDetectorProvider getServiceDetector(Language language) throws IOException {
        ServiceLoader<FrameworkDetectorProvider> loader = ServiceLoader.load(FrameworkDetectorProvider.class, ServiceRecognizerImpl.class.getClassLoader());
        for (FrameworkDetectorProvider provider : loader) {
            if (provider.create().hasFramework(language)) {
                return provider;
            }
        }
        return null;
    }
}
