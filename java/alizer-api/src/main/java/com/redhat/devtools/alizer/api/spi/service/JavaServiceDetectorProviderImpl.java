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
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JavaServiceDetectorProviderImpl extends ServiceDetectorProvider {
    @Override
    public ServiceDetectorProvider create() {
        return new JavaServiceDetectorProviderImpl();
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList("JAVA");
    }

    @Override
    public Set<Service> getServices(Path root, Language language) {

        try {
            List<String> languagesAndFrameworks = getLanguagesAndFrameworks(language);
            List<ServiceDescriptor> descriptors = getServicesDescriptor(languagesAndFrameworks);
            return getServices(root, language, descriptors);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

        return Collections.emptySet();
    }

    private List<String> getLanguagesAndFrameworks(Language language) {
        List<String> languagesAndFrameworks = new ArrayList<>();
        if (!language.getFrameworks().isEmpty()) {
            if (language.getFrameworks().contains("Quarkus")) {
                languagesAndFrameworks.add("quarkus");
            } else if (language.getFrameworks().contains("Vertx")) {
                languagesAndFrameworks.add("vertx");
            }
        }
        languagesAndFrameworks.add("java");
        return languagesAndFrameworks;
    }

    private Set<Service> getServices(Path root, Language language, List<ServiceDescriptor> descriptors) throws IOException, ParserConfigurationException, SAXException {
        //TODO if vertx or quarkus also check for simple java dep

        if (language.getTools().contains("Gradle")) {
            return getServiceByTagsInConfigFile(root.resolve("build.gradle"), descriptors);
        } else if (language.getTools().contains("Maven")) {
            return getServiceByXMLTagsInConfigFile(root.resolve("pom.xml"), descriptors);
        }
        return Collections.emptySet();
    }

    private Set<Service> getServiceByXMLTagsInConfigFile(Path file, List<ServiceDescriptor> serviceDescriptors) throws ParserConfigurationException, IOException, SAXException {
        if (!file.toFile().exists()) {
            return Collections.emptySet();
        }
        Set<Service> services = new HashSet<>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file.toFile());
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("dependency");
        for (int i = 0; i < nodeList.getLength(); i++) {
            NodeList dependencyNodes = nodeList.item(i).getChildNodes();
            String dependencyGroupId = "";
            String dependencyArtifactId = "";
            for (int j= 0; j<dependencyNodes.getLength(); j++) {
                Node dependencyNode = dependencyNodes.item(j);
                String nodeName = dependencyNode.getNodeName();
                if (nodeName.equalsIgnoreCase("groupid")) {
                    dependencyGroupId = dependencyNode.getNodeValue();
                } else if (nodeName.equalsIgnoreCase("artifactid")) {
                    dependencyArtifactId = dependencyNode.getNodeValue();
                }
            }

            if (dependencyGroupId.isEmpty() || dependencyArtifactId.isEmpty()) {
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
        if (!file.toFile().exists()) {
            return Collections.emptySet();
        }
        Set<Service> services = new HashSet<>();
        List<String> allLines = Files.readAllLines(file);
        allLines.stream().filter(line -> Pattern.matches("\\s*(implementation|compile).*", line))
                .forEach(line -> {
                    Service service = getServiceByTag(serviceDescriptors, (groupId, artifactId) ->
                            !groupId.isEmpty() && !artifactId.isEmpty()
                                && (line.contains(groupId + ":" + artifactId)
                                    || Pattern.matches("group:\\s*'" + groupId + "'\\s*,\\s*name:\\s*'" + artifactId, line)));
                    if (service != null) {
                        services.add(service);
                    }
                }
        );
        return services;
    }

    private Service getServiceByTag(List<ServiceDescriptor> serviceDescriptors, BiFunction<String, String, Boolean> isService) {
        for (ServiceDescriptor serviceDescriptor: serviceDescriptors) {
            for (DependencyDescriptor dependencyDescriptor: serviceDescriptor.getAllDependenciesDescriptor()) {
                Map<String, String> attributes = dependencyDescriptor.getAttributes();
                String groupId = attributes.getOrDefault("groupId", "");
                String artifactId = attributes.getOrDefault("artifactId", "");
                if (isService.apply(groupId, artifactId)) {
                    return serviceDescriptor.getService();
                }
                //TODO check quarkus configuration file if quarkus is used
            }
        }
        return null;
    }
}
