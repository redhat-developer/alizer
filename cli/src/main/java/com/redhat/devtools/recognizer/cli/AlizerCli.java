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

import com.redhat.devtools.alizer.registry.support.DevfileMetadata;
import com.redhat.devtools.alizer.registry.support.DevfileRegistryMetadataProviderBuilder;
import com.redhat.devtools.recognizer.api.DevfileType;
import com.redhat.devtools.recognizer.api.Language;
import com.redhat.devtools.recognizer.api.LanguageRecognizer;
import com.redhat.devtools.recognizer.api.LanguageRecognizerBuilder;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import picocli.CommandLine;

@TopCommand
@CommandLine.Command
public class AlizerCli implements Runnable{

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Help for alizer")
    boolean usageHelpRequested;

    @CommandLine.Parameters(index = "0", description = "The project to analyze")
    String name;

    @CommandLine.Option(names = {"-r", "--registry"}, description = "Pass the registry")
    List<String> registries;

    @Override
    public void run() {
        if (name == null) return;
        LanguageRecognizer reco = new LanguageRecognizerBuilder().build();

            try {
                reco.analyze(name).forEach(
                        lang -> {
                            System.out.printf("%-10s %-10s %-10s %-10s\n",
                                    lang.getName(),
                                    String.join(", ", lang.getFrameworks()),
                                    String.join(", ", lang.getTools()),
                                    String.format(" % .2f", lang.getUsageInPercentage()) + "%");
                        }
                );
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
