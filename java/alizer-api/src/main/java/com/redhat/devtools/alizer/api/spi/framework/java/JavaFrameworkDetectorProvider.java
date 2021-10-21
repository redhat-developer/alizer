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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.redhat.devtools.alizer.api.Service;
import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorWithConfigFileProvider;
import com.redhat.devtools.alizer.api.model.service.DependencyDescriptor;
import com.redhat.devtools.alizer.api.model.service.ServiceDescriptor;
import com.redhat.devtools.alizer.api.utils.DocumentParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import static com.redhat.devtools.alizer.api.Constants.JAVA;

public abstract class JavaFrameworkDetectorProvider extends FrameworkDetectorWithConfigFileProvider {

    protected abstract String getFrameworkTag();

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(JAVA);
    }

    @Override
    public List<DependencyDescriptor> extractDependenciesDescriptor(String language, ArrayNode dependenciesNode) {
        return extractDependenciesWithAttributes(dependenciesNode);
    }

    @Override
    public boolean hasFramework(Path file) throws IOException {
        if (file.toFile().getName().equalsIgnoreCase("build.gradle")) {
            return DocumentParser.isTagInFile(file, getFrameworkTag());
        } else {
            try {
                return hasGroupIdMaven(file, getFrameworkTag());
            } catch (ParserConfigurationException | SAXException e) {
                throw new IOException(e.getMessage());
            }
        }
    }

    protected boolean hasGroupIdMaven(Path file, String groupId) throws ParserConfigurationException, IOException, SAXException {
        NodeList nodeList = DocumentParser.getElementsByTag(file, "groupId");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getTextContent().startsWith(groupId)) {
                return true;
            }
        }
        return false;
    }

    protected List<Service> getServices(Path file, List<String> frameworks) throws IOException {
        return getServices(file, frameworks, Collections.emptySet());
    }

    protected List<Service> getServices(Path file, List<String> frameworks, Set<Service> services) throws IOException {
        List<String> frameworksList = new ArrayList<>();
        frameworksList.addAll(frameworks);
        frameworksList.addAll(getSupportedLanguages());

        List<ServiceDescriptor> descriptors = getServicesDescriptor(frameworksList);
        Set<Service> servicesSet = getServicesInner(file, descriptors);
        servicesSet.addAll(services);
        return new ArrayList<>(servicesSet);
    }

    private Set<Service> getServicesInner(Path file, List<ServiceDescriptor> descriptors) throws IOException {
        Set<Service> services = new HashSet<>();
        if (file.toFile().getName().equalsIgnoreCase("build.gradle")) {
            services.addAll(getServiceByTagsInConfigFile(file, descriptors));
        } else {
            try {
                services.addAll(getServiceByXMLTagsInConfigFile(file, descriptors));
            } catch (ParserConfigurationException | SAXException e) {
                throw new IOException(e.getLocalizedMessage(), e);
            }
        }
        return services;
    }

    private Set<Service> getServiceByXMLTagsInConfigFile(Path file, List<ServiceDescriptor> serviceDescriptors) throws ParserConfigurationException, IOException, SAXException {
        if (!file.toFile().exists()) {
            return Collections.emptySet();
        }
        Set<Service> services = new HashSet<>();
        NodeList nodeList = DocumentParser.getElementsByTag(file, "dependency");
        for (int i = 0; i < nodeList.getLength(); i++) {
            NodeList dependencyNodes = nodeList.item(i).getChildNodes();
            String dependencyGroupId = "", dependencyArtifactId = "";
            for (int j= 0; j<dependencyNodes.getLength(); j++) {
                Node dependencyNode = dependencyNodes.item(j);
                String nodeName = dependencyNode.getNodeName();
                if (nodeName.equalsIgnoreCase("groupid")) {
                    dependencyGroupId = dependencyNode.getFirstChild().getTextContent();
                } else if (nodeName.equalsIgnoreCase("artifactid")) {
                    dependencyArtifactId = dependencyNode.getFirstChild().getTextContent();
                }
            }

            if (dependencyGroupId == null || dependencyArtifactId == null
                    || dependencyGroupId.isEmpty() || dependencyArtifactId.isEmpty()) {
                continue;
            }

            String finalDependencyGroupId = dependencyGroupId;
            String finalDependencyArtifactId = dependencyArtifactId;
            Service service = getServiceByTag(serviceDescriptors, (groupId, artifactId) -> !groupId.isEmpty() && !artifactId.isEmpty()
                    && finalDependencyGroupId.contains(groupId) && finalDependencyArtifactId.contains(artifactId));
            if (service != null) {
                services.add(service);
            }
        }
        return services;
    }

    private Set<Service> getServiceByTagsInConfigFile(Path file, List<ServiceDescriptor> serviceDescriptors) throws IOException {
        return getServiceFromConfigFileInner(file, (line) -> getServiceByTag(serviceDescriptors, (groupId, artifactId) ->
                !groupId.isEmpty() && !artifactId.isEmpty()
                        && (line.contains(groupId + ":" + artifactId)
                        || Pattern.matches("group:\\s*'" + groupId + "'\\s*,\\s*name:\\s*'" + artifactId, line))));
    }

    protected Set<Service> getServiceFromConfigFileInner(Path file, Function<String, Service> getService) throws IOException {
        if (!file.toFile().exists()) {
            return Collections.emptySet();
        }
        Set<Service> services = new HashSet<>();
        List<String> allLines = Files.readAllLines(file);
        allLines.forEach(line -> {
                    Service service = getService.apply(line);
                    if (service != null) {
                        services.add(service);
                    }
                }
        );
        return services;
    }

    private Service getServiceByTag(List<ServiceDescriptor> serviceDescriptors, BiFunction<String, String, Boolean> isService) {
        return getServiceByTagInner(serviceDescriptors, (attributes) -> {
            String groupId = attributes.getOrDefault("groupId", "");
            String artifactId = attributes.getOrDefault("artifactId", "");
            return isService.apply(groupId, artifactId);
        });
    }

    protected Service getServiceByTagInner(List<ServiceDescriptor> serviceDescriptors, Function<Map<String, String>, Boolean> isService) {
        for (ServiceDescriptor serviceDescriptor: serviceDescriptors) {
            for (DependencyDescriptor dependencyDescriptor: serviceDescriptor.getAllDependenciesDescriptor()) {
                if (isService.apply(dependencyDescriptor.getAttributes())) {
                    return serviceDescriptor.getService();
                }
            }
        }
        return null;
    }
}
