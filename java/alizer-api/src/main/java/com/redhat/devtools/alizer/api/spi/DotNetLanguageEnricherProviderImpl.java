package com.redhat.devtools.alizer.api.spi;

import com.redhat.devtools.alizer.api.Language;
import com.redhat.devtools.alizer.api.spi.framework.FrameworkDetectorProvider;
import com.redhat.devtools.alizer.api.spi.framework.nodejs.NodeJsFrameworkDetectorProvider;
import com.redhat.devtools.alizer.api.utils.DocumentParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.redhat.devtools.alizer.api.Constants.CSHARP;
import static com.redhat.devtools.alizer.api.Constants.FSHARP;
import static com.redhat.devtools.alizer.api.Constants.VBNET;

public class DotNetLanguageEnricherProviderImpl extends LanguageEnricherProvider {

    private static final Pattern PROJ_PATTERN = Pattern.compile(".*\\.\\w+proj");

    @Override
    public LanguageEnricherProvider create() {
        return new DotNetLanguageEnricherProviderImpl();
    }

    @Override
    public List<String> getSupportedLanguages() {
        return Arrays.asList(CSHARP, FSHARP, VBNET);
    }

    @Override
    public Language getEnrichedLanguage(Language language, List<File> files) throws IOException {
        List<File> configFiles = files.stream()
                .filter(file -> PROJ_PATTERN.matcher(file.getName()).matches())
                .collect(Collectors.toList());

        if (!configFiles.isEmpty()) {
            language.setFrameworks(getFrameworks(configFiles));
        }

        return language;
    }

    private List<String> getFrameworks(List<File> files) throws IOException {
        List<String> frameworks = new ArrayList<>();
        try {
            for (File file: files) {
                String framework = getFramework(file);
                if (!framework.isEmpty()) {
                    frameworks.add(framework);
                }
            }
        } catch (ParserConfigurationException | SAXException e) {

        }
        return frameworks;
    }

    private String getFramework(File file) throws ParserConfigurationException, IOException, SAXException {
        Node httpRuntimeNode = DocumentParser.getElementsByTag(file, "httpRuntime").item(0);
        if (httpRuntimeNode != null) {
            String framework = getFrameworkAttribute(httpRuntimeNode);
            if (!framework.isEmpty()) {
                return framework;
            }
        }
        Node compilationNode = DocumentParser.getElementsByTag(file, "compilation").item(0);
        if (compilationNode != null) {
            String framework = getFrameworkAttribute(compilationNode);
            if (!framework.isEmpty()) {
                return framework;
            }
        }
        NodeList targetFrameworkVersionNodeList = DocumentParser.getElementsByTag(file, "TargetFrameworkVersion");
        if (targetFrameworkVersionNodeList.getLength() > 0) {
            return targetFrameworkVersionNodeList.item(0).getTextContent();
        }
        NodeList targetFrameworkNodeList = DocumentParser.getElementsByTag(file, "TargetFramework");
        if (targetFrameworkNodeList.getLength() > 0) {
            return targetFrameworkNodeList.item(0).getTextContent();
        }
        return "";
    }

    private String getFrameworkAttribute(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) {
            return "";
        }

        Node targetFrameworkNode = attributes.getNamedItem("targetFramework");
        if (targetFrameworkNode != null) {
            return targetFrameworkNode.getTextContent();
        }
        return "";
    }

}
