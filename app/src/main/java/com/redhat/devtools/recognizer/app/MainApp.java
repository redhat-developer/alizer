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
package com.redhat.devtools.recognizer.app;

import com.redhat.devtools.recognizer.api.LanguageRecognizer;

public class MainApp {

    public static void main(String[] args) {
        //if (args.length == 0) return;


        System.out.printf("%-10s %-10s %-10s %-10s\n", "LANGUAGE", "FRAMEWORK", "TOOL", "USAGE");

        LanguageRecognizer.analyze("/home/luca/Public/github.com/other/django-ex").forEach(
                lang -> {
                    System.out.printf("%-10s %-10s %-10s %-10s\n",
                            lang.getName(),
                            (!lang.getFrameworks().isEmpty() ? lang.getFrameworks().get(0) : ""),
                            (!lang.getTools().isEmpty() ? lang.getTools().get(0) : ""),
                            String.format(" % .2f", lang.getUsageInPercentage()) + "%");
                }
        );
    }
}
