/*-----------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat, Inc. All rights reserved.
 *  Licensed under the MIT License. See LICENSE file in the project root for license information.
 *-----------------------------------------------------------------------------------------------*/

import * as jsyaml from 'js-yaml';
import {promises as fs} from 'fs';
import * as path from 'path';
import { glob } from 'glob-gitignore';
import { DevfileType, Language } from './types';
import { FRAMEWORK_WEIGHT, JAVA, PYTHON, TOOL_WEIGHT } from './constants';
import { getJava } from './javaRecognizer';
import { getPython } from './pythonRecognizer';


export async function selectDevFileFromTypes(root: string, devfileTypes: DevfileType[]) {
    const languages = await detectLanguages(root);
    for (const language of languages) {
        const devfileTypesPickedUsingLanguage = devfileTypes
                        .map(devfileType => { 
                            return {
                                score: computeLanguageScore(language, devfileType),
                                devfileType
                            };
                        })
                        .filter(devfileType => devfileType.score > 0)
                        .sort((a, b) => b.score - a.score);
        if (devfileTypesPickedUsingLanguage.length > 0) {
            return devfileTypesPickedUsingLanguage[0].devfileType;
        }
    }
    return;
}

function computeLanguageScore(language: Language, devfileType: DevfileType) {
    let score = 0;
    if (devfileType.getLanguage() === language.name || matches(devfileType.getLanguage(), language.aliases)) {
        score++;
        if (devfileType.getProjectType() && matches(devfileType.getProjectType(), language.frameworks)) {
            score += FRAMEWORK_WEIGHT;
        }
        const tags = devfileType.getTags();
        if (tags && tags.length > 0) {
            tags.forEach(tag => {
                if (matches(tag, language.frameworks)) {
                    score += FRAMEWORK_WEIGHT;
                }
                if (language.builder === tag) {
                    score += TOOL_WEIGHT;
                }
            });
        }
    }
    return score;
}

function matches(value: string, list?: string[]) {
    return list && list.filter(s => s.toLowerCase() === value.toLowerCase()).length > 0;
}

export async function detectLanguages(root: string): Promise<Language[]> {
    if (!root) {
        throw new Error('The project root is not valid');
    }
    
    const YAMLFileWithAllLanguages = await fs.readFile(path.join(__dirname, '..', 'resources', 'languages.yaml'), 'utf-8');
    const allLanguages = jsyaml.safeLoad(YAMLFileWithAllLanguages);
    
    const allFilesFromRoot = await getFiles(root);

    const extensionOccurrences = allFilesFromRoot.reduce((acc, curr) => {
        curr = `.${curr.split('.').pop()}`;
        if (!acc[curr]) {
            acc[curr] = 1;
        } else {
            acc[curr] += 1;
        }
        return acc;
    }, {});

    const languagesFiltered = {};

    for (const [language, attributes] of Object.entries(allLanguages)) {
        if (attributes.hasOwnProperty('type') && 
            attributes.hasOwnProperty('extensions') &&
            (attributes as any).type === 'programming') {
            let languageOccurrence = (attributes as any).extensions
                                .filter(element => Object.keys(extensionOccurrences).indexOf(element) !== -1)
                                .reduce((acc, curr) => acc + extensionOccurrences[curr], 0);
            if (languageOccurrence !== 0) {
                languagesFiltered[language] = {
                    aliases: (attributes as any).aliases,
                    occurrence: languageOccurrence
                }
            }            
        }
    }

    const languagesDetected: Language[] = [];
    for(const language of Object.entries(languagesFiltered).sort(([_k1, v1], [_k2, v2]) => (v2 as number)-(v1 as number))) {
        languagesDetected.push({
            aliases: (language[1] as any).aliases,
            ...await getDetailedLanguage(language[0], allFilesFromRoot)
        });
    }
    return languagesDetected;
    
}

async function getDetailedLanguage(language: string, files: string[]): Promise<Language> {
    language = language.toLowerCase();
    switch (language) {
        case JAVA: {
            return await getJava(files);
        }
        case PYTHON: {
            return await getPython(files);
        }
        default: {
            return {
                name: language
            };
        }
    }
}

async function getFiles(root: string): Promise<string[]> {
    const ignores = await getIgnores(root);
    const searchResult = await glob(`**`, {cwd: root, ignore: ignores});
    return searchResult.map(it => path.join(root, it));
}

async function getIgnores(root: string): Promise<string[]> {
    const ignorePath = path.join(root, '.gitignore');
    let ignores = [];
    try{
        const stat = await fs.stat(ignorePath);
        if(stat){
            const ignoreContent = await fs.readFile(ignorePath, {encoding: 'utf-8'});
            if(ignoreContent) {
                ignores =  ignoreContent.split('\n').filter(it => it && !it.trim().startsWith('#'));
            }
        }
        
    } catch (err) {
        //file doesn't exist
    }
    return ignores;
}
