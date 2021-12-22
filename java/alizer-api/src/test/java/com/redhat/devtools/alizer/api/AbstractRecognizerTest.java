/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.redhat.devtools.alizer.api;

import com.redhat.devtools.alizer.api.DevfileType;
import org.junit.BeforeClass;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractRecognizerTest {
    protected static List<DevfileType> devfileTypes;
    protected static DevfileType JAVA_MAVEN;
    protected static DevfileType JAVA;
    protected static DevfileType JAVA_QUARKUS;
    protected static DevfileType JAVA_SPRING;
    protected static DevfileType JAVA_VERTX;
    protected static DevfileType JAVA_WILDFLY_BOOTABLE;
    protected static DevfileType JAVA_WILDFLY;
    protected static DevfileType NODEJS;
    protected static DevfileType PYTHON_DJANGO;
    protected static DevfileType PYTHON;
    protected static DevfileType CSHARP;
    protected static DevfileType VB_NET4_5;
    protected static DevfileType VB_NET4_6;

    @BeforeClass
    public static void init() {
        devfileTypes = Arrays.asList(
                JAVA_MAVEN = createDevfileType("java-maven", "java", "java", "Java", "Maven"),
                JAVA = createDevfileType("java", "java", "java"),
                JAVA_QUARKUS = createDevfileType("java-quarkus", "java", "quarkus", "Java", "Quarkus"),
                JAVA_SPRING = createDevfileType("java-spring", "java", "spring", "Java", "Spring"),
                JAVA_VERTX = createDevfileType("java-vertx", "java", "vertx","Java", "Vert.x"),
                JAVA_WILDFLY_BOOTABLE = createDevfileType("java-wildfly-bootable", "java", "wildfly", "RHEL8", "Java", "OpenJDK", "Maven", "WildFly", "Microprofile", "WildFly Bootable"),
                JAVA_WILDFLY = createDevfileType("java-wildfly", "java", "wildfly", "Java", "WildFly"),
                NODEJS = createDevfileType("nodejs", "nodejs", "nodejs", "NodeJS", "Express", "ubi8"),
                PYTHON_DJANGO = createDevfileType("python-django", "python", "django", "Python", "pip", "Django"),
                PYTHON = createDevfileType("python", "python", "python", "Python", "pip"),
                CSHARP = createDevfileType("csharp", "c#", "csharp", "csharp", ".net"),
                VB_NET4_5 = createDevfileType("net4.5", "Visual Basic .NET", "vb", "4.5", ".net"),
                VB_NET4_6 = createDevfileType("net4.6", "Visual Basic .NET", "vb", "4.6", ".net")
        );
    }

    static DevfileType createDevfileType(String name, String language, String projectType, String ...tags) {
        return new DevfileType() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getLanguage() {
                return language;
            }

            @Override
            public String getProjectType() {
                return projectType;
            }

            @Override
            public List<String> getTags() {
                return Arrays.asList(tags);
            }
        };
    }
}
