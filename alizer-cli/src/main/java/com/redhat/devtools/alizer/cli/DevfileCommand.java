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

import com.redhat.devtools.alizer.api.DevfileType;
import com.redhat.devtools.alizer.api.LanguageRecognizer;
import com.redhat.devtools.alizer.api.LanguageRecognizerBuilder;
import com.redhat.devtools.alizer.registry.support.DevfileMetadata;
import com.redhat.devtools.alizer.registry.support.DevfileRegistryMetadataProviderBuilder;
import picocli.CommandLine;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@CommandLine.Command(name = "devfile")
public class DevfileCommand extends BaseCommand implements Runnable{

    @CommandLine.Option(names = {"-r", "--registry"}, description = "Pass the registry")
    List<String> registries;

    @Override
    public void run() {
        LanguageRecognizer reco = new LanguageRecognizerBuilder().build();

            try {
                if (registries != null && !registries.isEmpty()) {
                    List<DevfileMetadata> devfiles = new DevfileRegistryMetadataProviderBuilder().withURLs(registries).build().getDevfileMetada();
                    DevfileType type = reco.selectDevFileFromTypes(name,devfiles.stream().map(it -> new DevfileTypeAdapter(it)).collect(Collectors.toList()));
                    if (type != null) {
                        System.out.println("Devfile " + type.getName() + " matched");
                    } else {
                        System.out.println("No devfile found to match");
                    }
                }
            } catch (IOException e) {}
        }
}
