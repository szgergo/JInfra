package io.jinfra.testing.docker.tests;

import io.jinfra.testing.docker.DockerValidator;
import org.junit.Test;

import java.io.IOException;

import static io.jinfra.testing.TestConstants.getTestResource;

public class DockerValidatorTest {

    @Test
    public void testLoadValidDockerFile() throws IOException {
        DockerValidator dockerValidator = new DockerValidator(getTestResource("Dockerfile"));
    }
}
