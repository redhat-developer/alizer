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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NodejsServiceDetectorProviderImpl implements ServiceDetectorProvider {
    @Override
    public ServiceDetectorProvider create() {
        return new NodejsServiceDetectorProviderImpl();
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList("Javascript", "Typescript");
    }


    @Override
    public List<String> getServices(Path root, Language language) {
        try {
            JsonNode node = Utils.getResourceAsJsonNode("/services.yml");
            Map<String, ArrayNode> dependencies = Utils.getDependenciesByLanguage(node, "nodejs");
            return getDependenciesUsed(root.resolve("package.json"), dependencies);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private List<String> getDependenciesUsed(Path packageJsonFile, Map<String, ArrayNode> dependencies) throws IOException {
        List<String> dependenciesUsed = new ArrayList<>();
        Map content = Utils.getJsonFileAsMap(packageJsonFile.toFile());
        if (content.containsKey("dependencies")) {
            ((Map)content.get("dependencies")).keySet().forEach(k -> {
                Optional<String> service = getServiceByDependency(k.toString(), dependencies);
                service.ifPresent(dependenciesUsed::add);
            });
        }
        return dependenciesUsed;
    }

    private Optional<String> getServiceByDependency(String dependency, Map<String, ArrayNode> dependencies) {
        return dependencies.entrySet().stream().filter(entry -> {
            for (JsonNode node: entry.getValue()) {
                if (node.asText().equalsIgnoreCase(dependency)) {
                    return true;
                }
            }
            return false;
        }).map(Map.Entry::getKey).findFirst();
    }
}
