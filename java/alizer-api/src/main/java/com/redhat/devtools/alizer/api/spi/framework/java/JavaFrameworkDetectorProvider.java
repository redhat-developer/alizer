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
package com.redhat.devtools.alizer.api.spi.framework.java;

import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorWithConfigFileProvider;
import com.redhat.devtools.alizer.api.utils.DocumentParser;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import static com.redhat.devtools.alizer.api.Constants.JAVA;

public abstract class JavaFrameworkDetectorProvider implements FrameworkDetectorWithConfigFileProvider {

    protected abstract String getFrameworkTag();

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(JAVA);
    }

    @Override
    public boolean hasFramework(File file) throws IOException {
        if (file.getName().equalsIgnoreCase("build.gradle")) {
            return DocumentParser.isTagInFile(file, getFrameworkTag());
        } else {
            try {
                return hasGroupIdMaven(file, getFrameworkTag());
            } catch (ParserConfigurationException | SAXException e) {
                throw new IOException(e.getMessage());
            }
        }
    }

    protected boolean hasGroupIdMaven(File file, String groupId) throws ParserConfigurationException, IOException, SAXException {
        NodeList nodeList = DocumentParser.getElementsByTag(file, "groupId");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getTextContent().startsWith(groupId)) {
                return true;
            }
        }
        return false;
    }

}
