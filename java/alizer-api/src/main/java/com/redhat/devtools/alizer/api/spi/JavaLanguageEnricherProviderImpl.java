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
package com.redhat.devtools.alizer.api.spi;

import com.redhat.devtools.alizer.api.Language;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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

public class JavaLanguageEnricherProviderImpl implements LanguageEnricherProvider {
    @Override
    public LanguageEnricherProvider create() {
        return new JavaLanguageEnricherProviderImpl();
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList("JAVA");
    }

    @Override
    public Language getEnrichedLanguage(Language language, List<String> files) {
        // find builder
        Optional<String> gradle = files.stream().filter(file -> FilenameUtils.getName(file).equalsIgnoreCase("build.gradle")).findFirst();
        Optional<String> maven = files.stream().filter(file -> FilenameUtils.getName(file).equalsIgnoreCase("pom.xml")).findFirst();
        Optional<String> ant = files.stream().filter(file -> FilenameUtils.getName(file).equalsIgnoreCase("build.xml")).findFirst();

        try {
            if (gradle.isPresent()) {
                language.setTools(Arrays.asList("Gradle"));
                language.setFrameworks(getJavaFramework(gradle.get(), true));
            } else if (maven.isPresent()) {
                language.setTools(Arrays.asList("Maven"));
                language.setFrameworks(getJavaFramework(maven.get(), false));
            } else if (ant.isPresent()) {
                language.setTools(Arrays.asList("Ant"));
            }
        } catch (ParserConfigurationException e) {
        } catch (SAXException e) {
        } catch (IOException e) {
        }

        return language;
    }

    private List<String> getJavaFramework(String file, boolean isGradle) throws ParserConfigurationException, SAXException, IOException {
        List<String> frameworks = new ArrayList<>();
        boolean isMaven = !isGradle;
        boolean hasQuarkus = hasDependency(file, "io.quarkus", isGradle, isMaven);
        if (hasQuarkus) {
            frameworks.add("Quarkus");
        }
        boolean hasSpring = hasDependency(file, "org.springframework", isGradle, isMaven);
        if (hasSpring) {
            frameworks.add("Spring");
        }
        boolean hasOpenLiberty = hasDependency(file, "io.openliberty", isGradle, isMaven);
        if (hasOpenLiberty) {
            frameworks.add("OpenLiberty");
        }
        boolean hasMicronaut = hasDependency(file, "io.micronaut", isGradle, isMaven);
        if (hasMicronaut) {
            frameworks.add("Micronaut");
        }
        boolean hasVertx = hasDependency(file, "io.vertx", isGradle, isMaven);
        if (hasVertx) {
            frameworks.add("Vertx");
        }
        return frameworks;
    }

    private boolean hasDependency(String file, String tag, boolean isGradle, boolean isMaven) throws IOException, ParserConfigurationException, SAXException {
        if (isGradle) {
            return IsTagInFile(file, tag);
        } else if (isMaven) {
            return hasGroupIdMaven(Paths.get(file), tag);
        }
        return false;
    }

    private boolean hasGroupIdMaven(Path file, String groupId) throws ParserConfigurationException, IOException, SAXException {
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

    public boolean IsTagInFile(String file, String tag) throws IOException {
        return Files.readAllLines(Paths.get(file)).stream().anyMatch(line -> line.contains(tag));
    }

}
