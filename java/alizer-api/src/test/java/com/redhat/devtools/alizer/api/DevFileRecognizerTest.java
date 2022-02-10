package com.redhat.devtools.alizer.api;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class DevFileRecognizerTest extends AbstractRecognizerTest {
    private DevFileRecognizer recognizer;

    @Before
    public void setup() {
        recognizer = new RecognizerFactory().createDevFileRecognizer();
    }

    @Test
    public void testJAVAMetaDevFile() throws IOException {
        DevfileType devFile = recognizer.selectDevFileFromTypes(new File("../../resources/projects/micronaut").getCanonicalPath(), devfileTypes);
        assertTrue(devFile.getName().equalsIgnoreCase("java-maven"));
    }

    @Test
    public void testQuarkusDevFileType() throws IOException {
        DevfileType devFile = recognizer.selectDevFileFromTypes(new File("../../resources/projects/quarkus").getCanonicalPath(), devfileTypes);
        assertTrue(devFile.getName().equalsIgnoreCase("java-quarkus"));
    }

    @Test
    public void testMicronautDevFile() throws IOException {
        DevfileType devFile = recognizer.selectDevFileFromTypes(new File("../../resources/projects/micronaut").getCanonicalPath(), devfileTypes);
        assertTrue(devFile.getName().equalsIgnoreCase("java-maven"));
    }

    @Test
    public void testNodeDevFile() throws IOException {
        DevfileType devFile = recognizer.selectDevFileFromTypes(new File("../../resources/projects/nodejs-ex").getCanonicalPath(), devfileTypes);
        assertTrue(devFile.getName().equalsIgnoreCase("nodejs"));
    }

    @Test
    public void testDjangoDevFile() throws IOException {
        DevfileType devFile = recognizer.selectDevFileFromTypes(new File("../../resources/projects/django").getCanonicalPath(), devfileTypes);
        assertTrue(devFile.getName().equalsIgnoreCase("python-django"));
    }

    @Test
    public void testCSharpDevFile() throws IOException {
        DevfileType devFile = recognizer.selectDevFileFromTypes(new File("../../resources/projects/s2i-dotnetcore-ex").getCanonicalPath(), devfileTypes);
        assertTrue(devFile.getName().equalsIgnoreCase("csharp"));
    }

    @Test
    public void testFSharpDevFile() throws IOException {
        DevfileType devFile = recognizer.selectDevFileFromTypes(new File("../../resources/projects/net-fsharp").getCanonicalPath(), devfileTypes);
        assertTrue(devFile.getName().equalsIgnoreCase("fsharp"));
    }

    @Test
    public void testVBDevFile() throws IOException {
        DevfileType devFile = recognizer.selectDevFileFromTypes(new File("../../resources/projects/net-vb").getCanonicalPath(), devfileTypes);
        assertTrue(devFile.getName().equalsIgnoreCase("netcore3.1"));
    }

    @Test
    public void testGoDevFile() throws IOException {
        DevfileType devFile = recognizer.selectDevFileFromTypes(new File("../../resources/projects/golang-gin-app").getCanonicalPath(), devfileTypes);
        assertTrue(devFile.getName().equalsIgnoreCase("go"));
    }
}
