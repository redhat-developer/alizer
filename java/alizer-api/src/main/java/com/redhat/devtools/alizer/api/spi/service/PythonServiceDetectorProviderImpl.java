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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PythonServiceDetectorProviderImpl implements ServiceDetectorProvider {
    @Override
    public ServiceDetectorProvider create() {
        return new PythonServiceDetectorProviderImpl();
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList("PYTHON");
    }

    @Override
    public List<String> getServices(Path root, Language language) {
        Set<String> services = new HashSet<>();
        try {
            JsonNode node = Utils.getResourceAsJsonNode("/services.yml");
            Map<String, ArrayNode> dependencies = Utils.getDependenciesByLanguage(node, "python");
            List<Path> allPythonFiles = Files.walk(root, Integer.MAX_VALUE)
                    .filter(path -> Files.isRegularFile(path) && path.toString().toLowerCase().endsWith(".py"))
                    .collect(Collectors.toList());
            for (Path path: allPythonFiles) {
                services.addAll(getTagsInFile(path, dependencies));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(services);
    }

    private List<String> getTagsInFile(Path file, Map<String, ArrayNode> tags) throws IOException {
        if (!file.toFile().exists()) {
            return Collections.emptyList();
        }
        List<String> tagsFound = new ArrayList<>();
        List<String> allLines = Files.readAllLines(file);
        for(String line: allLines) {
            Optional<String> service = getServiceByDependency(line, tags);
            service.ifPresent(tagsFound::add);
        }
        return tagsFound;
    }

    private Optional<String> getServiceByDependency(String line, Map<String, ArrayNode> dependencies) {
        return dependencies.entrySet().stream().filter(entry -> {
            for (JsonNode node: entry.getValue()) {
                if (Pattern.matches("(import|from)\\s*" + node.asText(), line)) {
                    return true;
                }
            }
            return false;
        }).map(Map.Entry::getKey).findFirst();
    }
}
