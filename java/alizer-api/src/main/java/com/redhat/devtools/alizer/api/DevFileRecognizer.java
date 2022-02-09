package com.redhat.devtools.alizer.api;

import java.io.IOException;
import java.util.List;

public interface DevFileRecognizer {
    <T extends DevfileType> T selectDevFileFromTypes(String srcPath, List<T> devfileTypes) throws IOException;
    <T extends DevfileType> T selectDevFileFromTypes(List<Language> languages, List<T> devfileTypes) throws IOException;
}
