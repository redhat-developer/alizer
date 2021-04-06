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

import com.redhat.devtools.recognizer.api.LanguageRecognizer;
import com.redhat.devtools.recognizer.api.LanguageRecognizerBuilder;
import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(name = "analyze")
public class AnalyzeCommand extends BaseCommand implements Runnable{


    @Override
    public void run() {
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
            } catch (IOException e) {}
        }
}
