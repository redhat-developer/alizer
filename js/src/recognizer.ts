/*-----------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat, Inc. All rights reserved.
 *  Licensed under the MIT License. See LICENSE file in the project root for license information.
 *-----------------------------------------------------------------------------------------------*/

import * as jsyaml from 'js-yaml';
import {promises as fs} from 'fs';
import * as path from 'path';
import * as glob from 'glob';
import { Language } from './types';
import { JAVA, PYTHON } from './constants';
import { getJava } from './javaRecognizer';
import { getPython } from './pythonRecognizer';

export async function detectLanguages(root: string): Promise<Language[]> {
    if (!root) {
        return Promise.reject(new Error('The project root is not valid'));
    }

    const fileWithLanguages = await fs.readFile(path.join(__dirname, '..', 'resources', 'languages.yaml'), 'utf-8');
    const allLanguages = jsyaml.safeLoad(fileWithLanguages);
    
    const files = await getFiles(root);

    const extensionOccurrences = files.reduce((acc, curr) => {
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
            let languageOccurrence = (attributes as any).extensions.
                                filter(element => Object.keys(extensionOccurrences).indexOf(element) !== -1).
                                map(item => extensionOccurrences[item])[0];
            if (languageOccurrence) {
                if (languagesFiltered.hasOwnProperty(language)) {
                    languageOccurrence += languagesFiltered[language];
                }
                languagesFiltered[language] = languageOccurrence;
            }            
        }
    }

    const languagesDetected: Language[] = [];
    for(const language of Object.entries(languagesFiltered).sort(([_k1, v1], [_k2, v2]) => (v2 as number)-(v1 as number))) {
        languagesDetected.push(await getDetailedLanguage(language[0], files));
    }
    console.log(languagesDetected)
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
    return new Promise((resolve, reject) => {
        glob(`${root}/**/*.*`, `!${root}/**`, (err: any, files: string[] | PromiseLike<string[]>) => {
            if (err) {
                reject(`Unable to retrieve files from current project ${root}. ${err}`);
            }
            return resolve(files);
        })
    });
}

detectLanguages('/home/luca/Public/github.com/redhat-developer/empty-dir');