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
package com.redhat.devtools.recognizer.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.devtools.recognizer.api.DevfileType;
import java.io.IOException;
import picocli.CommandLine;

public class MetadataConverter implements CommandLine.ITypeConverter<DevfileType> {

    public MetadataConverter() {}

    @Override
    public DevfileType convert(String s) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(s, DevfileType.class);
    }
}
