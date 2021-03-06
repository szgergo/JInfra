package io.jinfra.testing.docker.compose.tests;

import io.jinfra.testing.TestConstants;
import io.jinfra.testing.docker.compose.DockerComposeRunner;
import io.jinfra.testing.docker.compose.DockerComposeValidator;
import io.jinfra.testing.docker.compose.api.DockerComposeValidatorErrorMessage;
import org.junit.Test;

import java.io.File;
import java.util.Set;

import static org.junit.Assert.*;

public class DockerComposeValidatorTest {

    @Test
    public void testOpenExistingFile() {
        new DockerComposeValidator(TestConstants.getTestResource("test-docker-compose.yml"));
    }

    @Test
    public void testOpenNonExistingFile() {
        assertThrows(IllegalArgumentException.class, () -> new DockerComposeValidator(TestConstants.getTestResource("non-existing-file")));
    }

    @Test
    public void testOpenDirectory() {
        assertThrows(IllegalArgumentException.class,
                () -> new DockerComposeValidator(TestConstants.getTestResource("test-dir")));
    }

    @Test
    public void testPassNullAsArgument() {
        assertThrows(IllegalArgumentException.class,() -> new DockerComposeValidator((File)null));
    }

    @Test
    public void testInvalidYamlFile() {
        assertFalse(DockerComposeRunner
                .isValid(TestConstants.getTestResourceAsFile("invalid-docker-compose.yml")));
    }

    @Test
    public void testInvalidWithWrongKeyYamlFile() {
        assertFalse(DockerComposeRunner
                .isValid(TestConstants.getTestResourceAsFile("invalid2-docker-compose.yml")));
    }

    @Test
    public void testValidYamlFile() {
        assertTrue(DockerComposeRunner
                .isValid(TestConstants.getTestResourceAsFile("valid-docker-compose.yml")));

    }

    @Test
    public void testCheckVersionOfDockerCompose() {
        DockerComposeValidator dockerComposeValidator =
                new DockerComposeValidator(TestConstants.getTestResource("valid-docker-compose.yml"));
        assertTrue(dockerComposeValidator.hasVersion("3.1"));
    }

    @Test
    public void testCheckNumberOfServices() {
        DockerComposeValidator dockerComposeValidator =
                new DockerComposeValidator(TestConstants.getTestResource("valid-docker-compose.yml"));
        assertTrue(dockerComposeValidator.hasNumberOfServices(3));
    }

    @Test
    public void testServicesList() {
        DockerComposeValidator dockerComposeValidator =
                new DockerComposeValidator(TestConstants.getTestResource("valid-docker-compose.yml"));
        assertTrue(dockerComposeValidator.hasServices("db_valid","admin_valid","roadmappio_valid"));
    }

    @Test
    public void testServicesNotInComposeFile() {
        DockerComposeValidator dockerComposeValidator =
                new DockerComposeValidator(TestConstants.getTestResource("valid-docker-compose.yml"));
        assertFalse(dockerComposeValidator.hasServices("xxx"));
    }
}
