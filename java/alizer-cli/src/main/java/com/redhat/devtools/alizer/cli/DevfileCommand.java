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

import com.redhat.devtools.alizer.api.DevFileRecognizer;
import com.redhat.devtools.alizer.api.DevfileType;
import com.redhat.devtools.alizer.api.LanguageRecognizer;
import com.redhat.devtools.alizer.api.RecognizerFactory;
import com.redhat.devtools.alizer.registry.support.DevfileMetadata;
import com.redhat.devtools.alizer.registry.support.DevfileRegistryMetadataProviderBuilder;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.CheckedTemplate;
import picocli.CommandLine;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@CommandLine.Command(name = "devfile")
public class DevfileCommand extends BaseCommand implements Runnable{

    @CommandLine.Option(names = {"-r", "--registry"}, description = "Pass the registry")
    List<String> registries;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance result(DevfileType result);
    }


    @Override
    public void run() {
        DevFileRecognizer reco = new RecognizerFactory().createDevFileRecognizer();
        DevfileType type = null;
        try {
            if (registries != null && !registries.isEmpty()) {
                List<DevfileMetadata> devfiles = new DevfileRegistryMetadataProviderBuilder().withURLs(registries).build().getDevfileMetada();
                type = reco.selectDevFileFromTypes(name, devfiles.stream().map(DevfileTypeAdapter::new).collect(Collectors.toList()));
            }
            System.out.println(getTemplateForFormat(Templates.result(type)).render());
        } catch (IOException e) {}
    }
}
