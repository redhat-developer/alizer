/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.redhat.devtools.alizer.registry.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represent an entry in the registry index.
 * @see <a href="https://github.com/devfile/registry-support/blob/master/index/generator/schema/schema.go">https://github.com/devfile/registry-support/blob/master/index/generator/schema/schema.go</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DevfileMetadata {
    @JsonProperty("name")
    private String name;

    @JsonProperty("version")
    private String version;

    @JsonProperty("attributes")
    private Map<String, String> attributes = Collections.emptyMap();

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("tags")
    private List<String> tags = Collections.emptyList();

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("globalMemoryLimit")
    private String globalMemoryLimit;

    @JsonProperty("projectType")
    private String projectType;

    @JsonProperty("language")
    private String language;

    @JsonProperty("links")
    private Map<String, String> links = Collections.emptyMap();

    @JsonProperty("resources")
    private List<String> resources = Collections.emptyList();

    @JsonProperty("starterProjects")
    private List<String> starterProjects = Collections.emptyList();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGlobalMemoryLimit() {
        return globalMemoryLimit;
    }

    public void setGlobalMemoryLimit(String globalMemoryLimit) {
        this.globalMemoryLimit = globalMemoryLimit;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public List<String> getStarterProjects() {
        return starterProjects;
    }

    public void setStarterProjects(List<String> starterProjects) {
        this.starterProjects = starterProjects;
    }
}
