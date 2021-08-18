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

import com.redhat.devtools.alizer.api.RecognizerFactory;
import com.redhat.devtools.alizer.api.Service;
import com.redhat.devtools.alizer.api.ServiceRecognizerImpl;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.CheckedTemplate;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import picocli.CommandLine;

@CommandLine.Command(name = "service")
public class ServiceCommand extends BaseCommand implements Runnable {

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance result(Map<Path, List<Service>> result);
    }

    @Override
    public void run() {
        ServiceRecognizerImpl reco = new RecognizerFactory().createServiceRecognizer();
        try {
            Map<Path, List<Service>> services = reco.analyze(name);
            System.out.println(getTemplateForFormat(Templates.result(services)).render());
        } catch (IOException e) {}
    }
}
