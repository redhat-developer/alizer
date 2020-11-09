/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc.
 ******************************************************************************/
package com.redhat.devtools.recognizer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JavaRecognizer {

    public static String getJava(List<String> files) throws ParserConfigurationException, SAXException, IOException {
        String result = "JAVA ";

        // find builder
        Optional<String> gradle = files.stream().filter(file -> FilenameUtils.getName(file).equalsIgnoreCase("build.gradle")).findFirst();
        Optional<String> maven = files.stream().filter(file -> FilenameUtils.getName(file).equalsIgnoreCase("pom.xml")).findFirst();
        Optional<String> ant = files.stream().filter(file -> FilenameUtils.getName(file).equalsIgnoreCase("build.xml")).findFirst();

        if (gradle.isPresent()) {
            result += "Gradle " + getJavaFramework(gradle.get(), true);
        } else if (maven.isPresent()) {
            result += "Maven " + getJavaFramework(maven.get(), false);
        } else if (ant.isPresent()) {
            result += "Ant ";
        }

        return result;
    }

    private static String getJavaFramework(String file, boolean isGradle) throws ParserConfigurationException, SAXException, IOException {
        String result = "";
        boolean isMaven = !isGradle;
        boolean hasQuarkus = hasDependency(file, "io.quarkus", isGradle, isMaven);
        if (hasQuarkus) {
            result += "Quarkus ";
        }
        boolean hasSpring = hasDependency(file, "org.springframework", isGradle, isMaven);
        if (hasSpring) {
            result += "Spring ";
        }
        boolean hasOpenLiberty = hasDependency(file, "io.openliberty", isGradle, isMaven);
        if (hasOpenLiberty) {
            result += "OpenLiberty ";
        }
        boolean hasMicronaut = hasDependency(file, "io.micronaut", isGradle, isMaven);
        if (hasMicronaut) {
            result += "Micronaut ";
        }
        return result;
    }

    private static boolean hasDependency(String file, String tag, boolean isGradle, boolean isMaven) throws IOException, ParserConfigurationException, SAXException {
        if (isGradle) {
            return Utils.IsTagInFile(file, tag);
        } else if (isMaven) {
            return hasGroupIdMaven(Paths.get(file), tag);
        }
        return false;
    }

    private static boolean hasGroupIdMaven(Path file, String groupId) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file.toFile());
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("groupId");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getTextContent().startsWith(groupId)) {
                return true;
            }
        }
        return false;
    }
}
