/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.redhat.devtools.alizer.api;

import java.util.List;

public class LanguageScore implements Comparable<LanguageScore> {
    private static final int FRAMEWORK_WEIGHT = 10;
    private static final int TOOL_WEIGHT = 5;

    private int score;

    private DevfileType devfileType;

    public LanguageScore(Language language, DevfileType devfileType) {
        this.devfileType = devfileType;
        computeScore(language);
    }

    private static boolean matches(List<String> list, String val) {
        return list.stream().filter(s -> s.equalsIgnoreCase(val)).count() > 0;
    }

    private void computeScore(Language language) {
        if (devfileType.getLanguage().equalsIgnoreCase(language.getName()) ||
        matches(language.getAliases(), devfileType.getLanguage())) {
            score++;
            if (devfileType.getProjectType() != null && matches(language.getFrameworks(), devfileType.getProjectType())) {
                score += FRAMEWORK_WEIGHT;
            }
            devfileType.getTags().forEach(tag -> {
                if (matches(language.getFrameworks(), tag)) {
                    score += FRAMEWORK_WEIGHT;
                }
                if (matches(language.getTools(), tag)) {
                    score += TOOL_WEIGHT;
                }
            });
        }
    }

    public DevfileType getDevfileType() {
        return devfileType;
    }

    public int getScore() {
        return score;
    }

    @Override
    public int compareTo(LanguageScore other) {
        return other.getScore() - getScore();
    }
}
