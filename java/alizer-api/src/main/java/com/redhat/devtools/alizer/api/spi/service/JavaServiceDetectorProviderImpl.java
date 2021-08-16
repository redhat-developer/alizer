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
import com.redhat.devtools.alizer.api.utils.Utils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JavaServiceDetectorProviderImpl implements ServiceDetectorProvider {
    @Override
    public ServiceDetectorProvider create() {
        return new JavaServiceDetectorProviderImpl();
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList("JAVA");
    }

    @Override
    public List<String> getServices(Path root, Language language) {

        try {
            JsonNode node = Utils.getResourceAsJsonNode("/services.yml");
            String field = getFieldName(language);
            Map<String, ArrayNode> dependencies = Utils.getDependenciesByLanguage(node, field);
            List<String> services = getServices(root, language, dependencies);
            if (field.equals("quarkus")) {
               // dependencies = Utils.getJsonFieldAsStringMap(node, field + "-config");
                // services.addAll(getServices(root, language, dependencies)); //TODO check quarkus config file
            }
            return services;
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private String getFieldName(Language language) {
        String field = "";
        if (!language.getFrameworks().isEmpty()) {
            if (language.getFrameworks().contains("Quarkus")) {
                field = "quarkus";
            } else if (language.getFrameworks().contains("Vertx")) {
                field = "vertx";
            }
        }
        if (field.isEmpty()) {
            field = "java";
        }
        return field;
    }

    private List<String> getServices(Path root, Language language, Map<String, ArrayNode> dependencies) throws IOException, ParserConfigurationException, SAXException {
        //TODO if vertx or quarkus also check for simple java dep

        if (language.getTools().contains("Gradle")) {
            return getTagsInFile(root.resolve("build.gradle"), dependencies);
        } else if (language.getTools().contains("Maven")) {
            return getGroupIdsMaven(root.resolve("pom.xml"), dependencies);
        }
        return Collections.emptyList();
    }

    private List<String> getGroupIdsMaven(Path file, Map<String, ArrayNode> tags) throws ParserConfigurationException, IOException, SAXException {
        if (!file.toFile().exists()) {
            return Collections.emptyList();
        }
        List<String> tagsFound = new ArrayList<>();
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
            Optional<String> tag = tags.entrySet().stream().filter(t -> {
                for (JsonNode item : t.getValue()) {
                    String groupId = item.has("groupId") ? item.get("groupId").asText() : "";
                    String artifactId = item.has("artifactId") ? item.get("artifactId").asText() : "";
                    if (!groupId.isEmpty() && !artifactId.isEmpty()
                            && finalDependencyGroupId.contains(groupId) && finalDependencyArtifactId.contains(artifactId)) {
                        return true;
                    }
                }
                return false;
            }).map(Map.Entry::getKey).findAny();
            tag.ifPresent(s -> tagsFound.add(tag.get()));
        }
        return tagsFound;
    }

    private List<String> getTagsInFile(Path file, Map<String, ArrayNode> tags) throws IOException {
        if (!file.toFile().exists()) {
            return Collections.emptyList();
        }
        List<String> tagsFound = new ArrayList<>();
        List<String> allLines = Files.readAllLines(file);
        allLines.stream().filter(line -> Pattern.matches("\\s*(implementation|compile).*", line))
                .forEach(line -> {
                    Optional<String> tag = tags.entrySet().stream().filter(t -> {
                        for (JsonNode item : t.getValue()) {
                            String groupId = item.has("groupId") ? item.get("groupId").asText() : "";
                            String artifactId = item.has("artifactId") ? item.get("artifactId").asText() : "";
                            if (!groupId.isEmpty() && !artifactId.isEmpty()
                                && (line.contains(groupId + ":" + artifactId)
                                    || Pattern.matches("group:\\s*'" + groupId + "'\\s*,\\s*name:\\s*'" + artifactId, line))) {
                                return true;
                            }
                        }
                        return false;
                    }).map(Map.Entry::getKey).findAny();
                    tag.ifPresent(s -> tagsFound.add(tag.get()));
                }
        );
        return tagsFound;
    }
}
