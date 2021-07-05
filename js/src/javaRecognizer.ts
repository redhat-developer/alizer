/*-----------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat, Inc. All rights reserved.
 *  Licensed under the MIT License. See LICENSE file in the project root for license information.
 *-----------------------------------------------------------------------------------------------*/
import path = require("path");
import { ANT, GRADLE, JAVA, MAVEN, MICRONAUT, OPENLIBERTY, QUARKUS, SPRINGBOOT } from "./constants";
import { Language } from "./types";
import { isTagInFile } from "./utils";

export async function getJava(files: string[]): Promise<Language> {
    const language: Language = {
        name: JAVA
    };
    // check builder
    const gradle = files.filter(file => path.basename(file).toLowerCase() === 'build.gradle')[0];
    const maven = files.filter(file => path.basename(file).toLowerCase() === 'pom.xml')[0];
    const ant = files.filter(file => path.basename(file).toLowerCase() === 'build.xml')[0];

    if (gradle) {
        language.builder = GRADLE;
        language.frameworks = await getJavaFrameworks(gradle, GRADLE);
    } else if (maven) {
        language.builder = MAVEN;
        language.frameworks = await getJavaFrameworks(maven, MAVEN);
    } else if (ant) {
        language.builder = ANT;
    }

    return language;
}

async function getJavaFrameworks(configFile: string, builder: string): Promise<string[]> {
    const frameworks: string[] = [];
    const hasQuarkus = await hasDependency(configFile, 'io.quarkus', builder);
    if (hasQuarkus) {
        frameworks.push(QUARKUS);
    }
    const hasSpring = await hasDependency(configFile, 'org.springframework', builder);
    if (hasSpring) {
        frameworks.push(SPRINGBOOT);
    }
    const hasOpenLiberty = await hasDependency(configFile, 'io.openliberty', builder);
    if (hasOpenLiberty) {
        frameworks.push(OPENLIBERTY);
    }
    const hasMicronaut = await hasDependency(configFile, 'io.micronaut', builder);
    if (hasMicronaut) {
        frameworks.push(MICRONAUT);
    }
    return frameworks;
}

async function hasDependency(configFile: string, tag: string, builder: string): Promise<boolean> {
    if (builder === GRADLE) {
        return await isTagInFile(configFile, tag);
    } else if (builder === MAVEN) {
        return await isTagInFile(configFile, tag);
    }

    return false;
}
