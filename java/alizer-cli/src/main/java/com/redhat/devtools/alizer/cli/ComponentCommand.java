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
import com.redhat.devtools.alizer.registry.support.DevfileMetadata;
import com.redhat.devtools.alizer.registry.support.DevfileRegistryMetadataProviderBuilder;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.CheckedTemplate;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import picocli.CommandLine;

@CommandLine.Command(name = "component")
public class ComponentCommand extends BaseCommand implements Runnable{

    @CommandLine.Option(names = {"-r", "--registry"}, description = "Pass the registry")
    List<String> registries;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance result(List<Component> result);
    }

    @Override
    public void run() {
        ComponentRecognizerImpl reco = new RecognizerBuilder().componentRecognizer();
        List<Component> components = null;

        try {
            if (registries != null && !registries.isEmpty()) {
                List<DevfileMetadata> devfiles = new DevfileRegistryMetadataProviderBuilder().withURLs(registries).build().getDevfileMetada();
                components = reco.analyze(name, devfiles.stream().map(DevfileTypeAdapter::new).collect(Collectors.toList()));
            }
            System.out.println(getTemplateForFormat(Templates.result(components)).render());
        } catch (IOException e) {}
    }
}
