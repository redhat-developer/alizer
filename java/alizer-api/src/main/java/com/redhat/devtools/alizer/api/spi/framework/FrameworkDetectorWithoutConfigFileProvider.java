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
package com.redhat.devtools.alizer.api.spi.framework;

import com.redhat.devtools.alizer.api.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public abstract class FrameworkDetectorWithoutConfigFileProvider extends FrameworkDetectorProvider {

    public abstract boolean hasFramework(List<Path> files) throws IOException;

    public abstract List<Service> getServices(Path root) throws IOException;
}
