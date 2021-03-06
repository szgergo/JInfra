package io.jinfra.testing.docker.tests;

import io.jinfra.testing.docker.DockerValidator;
import org.junit.Test;

import static io.jinfra.testing.TestConstants.getTestResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DockerValidatorTest {

    @Test
    public void testWithValidDockerFile() {
        assertTrue(DockerValidator
                .canImageBeBuiltWithDockerFile(getTestResource("Dockerfile")));
    }

    @Test
    public void testWithInvalidDockerFile() {
        assertFalse(DockerValidator
                .canImageBeBuiltWithDockerFile(getTestResource("invalid-Dockerfile")));
    }

    @Test
    public void testWitNullArgument() {
        assertFalse(DockerValidator
                .canImageBeBuiltWithDockerFile(null));
    }

    @Test
    public void testWitEmptyArgument() {
        assertFalse(DockerValidator
                .canImageBeBuiltWithDockerFile(""));
    }

    @Test
    public void testWitNonExistingDockerFile() {
        assertFalse(DockerValidator
                .canImageBeBuiltWithDockerFile("NON-EXISTING-FILE"));
    }
}
