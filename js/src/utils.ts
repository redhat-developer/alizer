/*-----------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat, Inc. All rights reserved.
 *  Licensed under the MIT License. See LICENSE file in the project root for license information.
 *-----------------------------------------------------------------------------------------------*/
import {promises as fs} from 'fs';

export async function isTagInFile(file: string, tag: string): Promise<boolean> {
    const content = await fs.readFile(file)
    return content.includes(tag);
}