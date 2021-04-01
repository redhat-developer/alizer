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

    @CommandLine.Parameters(index = "0", description = "The project to analyze")
    String name;

    @Override
    public void run() {
        if (name == null) return;

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
