package io.jinfra.testing.docker.tests;

import io.jinfra.testing.docker.DockerFileValidator;
import org.junit.Test;

import java.io.IOException;

import static io.jinfra.testing.TestConstants.getTestResource;

public class DockerFileValidatorTest {

    @Test
    public void testLoadValidDockerFile() throws IOException {
        new DockerFileValidator(getTestResource("Dockerfile"));
    }
}
