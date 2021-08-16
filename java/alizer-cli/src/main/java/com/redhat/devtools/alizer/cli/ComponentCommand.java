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

import com.redhat.devtools.alizer.api.Component;
import com.redhat.devtools.alizer.api.ComponentRecognizerImpl;
import com.redhat.devtools.alizer.api.RecognizerBuilder;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.CheckedTemplate;
import java.io.IOException;
import java.util.List;
import picocli.CommandLine;

@CommandLine.Command(name = "component")
public class ComponentCommand extends BaseCommand implements Runnable{

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance result(List<Component> result);
    }

    @Override
    public void run() {
        ComponentRecognizerImpl reco = new RecognizerBuilder().componentRecognizer();
        try {
            List<Component> components = reco.analyze(name);
            System.out.println(getTemplateForFormat(Templates.result(components)).render());
        } catch (IOException e) {}
    }
}
