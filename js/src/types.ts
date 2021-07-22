/*-----------------------------------------------------------------------------------------------
 *  Copyright (c) Red Hat, Inc. All rights reserved.
 *  Licensed under the MIT License. See LICENSE file in the project root for license information.
 *-----------------------------------------------------------------------------------------------*/
export interface Language {
    name: string;
    aliases?: string[];
    builder?: string;
    frameworks?: string[];
}

export interface DevfileType {
    getName(): string;
    getLanguage(): string;
    getProjectType(): string;
    getTags(): string[];
}
