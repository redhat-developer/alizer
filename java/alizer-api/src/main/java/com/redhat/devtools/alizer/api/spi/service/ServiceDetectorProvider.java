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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.redhat.devtools.alizer.api.Language;
import com.redhat.devtools.alizer.api.Service;
import com.redhat.devtools.alizer.api.utils.Utils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ServiceDetectorProvider {
    public abstract ServiceDetectorProvider create();

    public abstract List<String> getSupportedLanguages();

    public abstract Set<Service> getServices(Path root, Language language);

    protected List<ServiceDescriptor> getServicesDescriptor(List<String> languagesAndFrameworks) throws IOException {
        JsonNode node = Utils.getResourceAsJsonNode("/services.yml");

        List<ServiceDescriptor> descriptors = new ArrayList<>();

        for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();

            String name = entry.getKey();
            List<String> operators = new ArrayList<>();
            Map<String, List<DependencyDescriptor>> dependencies = new HashMap<>();

            JsonNode descriptorNode = entry.getValue();
            if (descriptorNode.has("operators")) {
                for(JsonNode operatorNode: descriptorNode.get("operators")) {
                    operators.add(operatorNode.asText());
                }
            }
            if (descriptorNode.has("dependencies")) {
                for (Iterator<Map.Entry<String, JsonNode>> jt = descriptorNode.get("dependencies").fields(); jt.hasNext(); ) {
                    Map.Entry<String, JsonNode> dependencyEntry = jt.next();
                    String language = dependencyEntry.getKey();
                    if (languagesAndFrameworks.stream().anyMatch(field -> field.equalsIgnoreCase(language))) {
                        ArrayNode dependenciesNode = (ArrayNode) dependencyEntry.getValue();
                        List<DependencyDescriptor> dependencyDescriptors  = extractDependenciesDescriptor(language, dependenciesNode);
                        dependencies.put(language, dependencyDescriptors);
                    }
                }
            }

            descriptors.add(new ServiceDescriptor(name, operators, dependencies));
        }
        return descriptors;
    }

    private List<DependencyDescriptor> extractDependenciesDescriptor(String language, ArrayNode dependenciesNode) {
        switch(language) {
            case "java":
            case "quarkus":
            case "vertx": {
                return extractDependenciesWithAttributes(dependenciesNode);
            }
            default: {
                return extractDependenciesWithName(dependenciesNode);
            }
        }
    }

    private List<DependencyDescriptor> extractDependenciesWithAttributes(ArrayNode dependenciesNode) {
        List<DependencyDescriptor> dependencyDescriptors = new ArrayList<>();
        for (JsonNode dependencyNode: dependenciesNode) {
            DependencyDescriptor dependencyDescriptor = new DependencyDescriptor();
            for (Iterator<Map.Entry<String, JsonNode>> it = dependencyNode.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                dependencyDescriptor.addAttribute(entry.getKey(), entry.getValue().asText());
            }
            dependencyDescriptors.add(dependencyDescriptor);
        }
        return dependencyDescriptors;
    }

    private List<DependencyDescriptor> extractDependenciesWithName(ArrayNode dependenciesNode) {
        List<DependencyDescriptor> dependencyDescriptors = new ArrayList<>();
        for (JsonNode dependencyNode: dependenciesNode) {
            dependencyDescriptors.add(new DependencyDescriptor(dependencyNode.asText()));
        }
        return dependencyDescriptors;
    }
}


