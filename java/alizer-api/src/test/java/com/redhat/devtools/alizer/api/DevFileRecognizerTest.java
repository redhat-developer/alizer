package com.redhat.devtools.alizer.api;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

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
    public void testVBNetDevFile() throws IOException {
        DevfileType devFile = recognizer.selectDevFileFromTypes(new File("../../resources/projects/VB.NET-ECommerce").getCanonicalPath(), devfileTypes);
        assertTrue(devFile.getName().equalsIgnoreCase("net4.5"));
    }
}
