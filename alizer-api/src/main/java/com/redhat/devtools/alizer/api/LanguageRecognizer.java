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
package com.redhat.devtools.alizer.api;

import com.redhat.devtools.alizer.api.DevfileType;

import java.io.IOException;
import java.util.List;

public interface LanguageRecognizer {
    DevfileType selectDevFileFromTypes(String srcPath, List<DevfileType> devfileTypes) throws IOException;
    List<Language> analyze(String path) throws IOException;
}
