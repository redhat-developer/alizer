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

import com.redhat.devtools.recognizer.api.DevfileType;
import com.redhat.devtools.recognizer.api.LanguageRecognizerBuilder;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import picocli.CommandLine;

@TopCommand
@CommandLine.Command
public class AlizerCli implements Runnable{

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Help for alizer")
    boolean usageHelpRequested;

    @CommandLine.Option(names = {"-m", "--meta"}, description = "Pass the devfile type in json format {\"name\":\"value\", \"language\":\"java\", \"tags\":[\"tag1\",\"tag2\"]}", converter = MetadataConverter.class)
    List<DevfileType> devfileTypes;

    @CommandLine.Option(names = {"-d", "--devfile"}, description = "Pass the meta devfile path")
    List<File> devfiles;

    @CommandLine.Parameters(index = "0", description = "The project to analyze")
    String name;

    @Override
    public void run() {
        if (name == null) return;

        if (devfileTypes != null || devfiles != null) {
            devfileTypes = devfileTypes == null ? Collections.emptyList() : devfileTypes;
            devfiles = devfiles == null ? Collections.emptyList() : devfiles;
            try {
                String devfile = new LanguageRecognizerBuilder().build().selectDevFile(name, devfileTypes, devfiles);
                if (devfile != null) {
                    System.out.println(devfile);
                }
            } catch (IOException e) {}
        } else {
            try {
                new LanguageRecognizerBuilder().build().analyze(name).forEach(
                        lang -> {
                            System.out.printf("%-10s %-10s %-10s %-10s\n",
                                    lang.getName(),
                                    String.join(", ", lang.getFrameworks()),
                                    String.join(", ", lang.getTools()),
                                    String.format(" % .2f", lang.getUsageInPercentage()) + "%");
                        }
                );
            } catch (IOException e) {}
        }
    }
}
