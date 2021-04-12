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
package com.redhat.devtools.alizer.cli;

import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.Variant;
import picocli.CommandLine;

public abstract class BaseCommand {
    @CommandLine.Parameters(index = "0", description = "The project to analyze")
    String name;

    @CommandLine.Option(names = {"-o"}, description = "Format")
    String format;

    protected TemplateInstance getTemplateForFormat(TemplateInstance template) {
        if (format == null) {
            template.setAttribute(TemplateInstance.SELECTED_VARIANT, Variant.forContentType(Variant.TEXT_PLAIN));
        } else if ("json".equalsIgnoreCase(format)) {
            template.setAttribute(TemplateInstance.SELECTED_VARIANT, Variant.forContentType(Variant.APPLICATION_JSON));
        } else if ("yaml".equalsIgnoreCase(format)) {
            template.setAttribute(TemplateInstance.SELECTED_VARIANT, Variant.forContentType("application/yaml"));
        }
        return template;
    }
}
