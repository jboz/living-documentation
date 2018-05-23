package ch.ifocusit.livingdoc.plugin;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

import java.io.File;

public class DiagramMojoITest extends AbstractMojoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testSomething() throws Exception {
        File pom = getTestFile("src/test/resources/microservice-pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

        DiagramMojo myMojo = (DiagramMojo) lookupMojo("diagram", pom);
        assertNotNull(myMojo);
        myMojo.execute();
    }
}