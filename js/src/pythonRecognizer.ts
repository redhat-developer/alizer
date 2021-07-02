/*-----------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat, Inc. All rights reserved.
 *  Licensed under the MIT License. See LICENSE file in the project root for license information.
 *-----------------------------------------------------------------------------------------------*/
import path = require("path");
import { DJANGO, PYTHON } from "./constants";
import { Language } from "./types";

export async function getPython(files: string[]): Promise<Language> {
    return {
        name: PYTHON,
        frameworks: await getPythonFrameworks(files)
    };
}

async function getPythonFrameworks(files: string[]): Promise<string[]> {
    const frameworks: string[] = [];
    
    const manage = files.filter(file => path.basename(file).toLowerCase() === 'manage.py')[0];
    const urls = files.filter(file => path.basename(file).toLowerCase() === 'urls.py')[0];
    const wsgi = files.filter(file => path.basename(file).toLowerCase() === 'wsgi.py')[0];
    const asgi = files.filter(file => path.basename(file).toLowerCase() === 'asgi.py')[0];

    if (manage || urls || wsgi || asgi) {
        frameworks.push(DJANGO);
    }

    return frameworks;
}