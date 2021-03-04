package io.jinfra.testing.docker.compose.tests;

import io.jinfra.testing.TestConstants;
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
        DockerComposeValidator dockerComposeValidator =
                new DockerComposeValidator(TestConstants.getTestResource("invalid-docker-compose.yml"));
        assertFalse(dockerComposeValidator.isValid());
    }

    @Test
    public void testInvalidWithWrongKeyYamlFile() {
        DockerComposeValidator dockerComposeValidator =
                new DockerComposeValidator(TestConstants.getTestResource("invalid2-docker-compose.yml"));
        assertFalse(dockerComposeValidator.isValid());
    }

    @Test
    public void testInvalidWithWrongKeyWithErrorMessagesYamlFile() {
        DockerComposeValidator dockerComposeValidator =
                new DockerComposeValidator(TestConstants.getTestResource("invalid2-docker-compose.yml"));
        final String expectedMessage = "$.versionaaaa: is not defined in the schema and the schema does not allow additional properties";
        final String expectedPath = "$";
        final String expectedType = "additionalProperties";
        DockerComposeValidatorErrorMessage expectedErrorMessage =
                new DockerComposeValidatorErrorMessage(expectedMessage, expectedPath, expectedType);
        Set<DockerComposeValidatorErrorMessage> actualErrorMessages =
                dockerComposeValidator.isValidWithErrorMessages();
        
        assertFalse(actualErrorMessages.isEmpty());
        assertEquals(1,actualErrorMessages.size());
        assertTrue(actualErrorMessages.contains(expectedErrorMessage));
    }

    @Test
    public void testValidYamlFile() {
        DockerComposeValidator dockerComposeValidator =
                new DockerComposeValidator(TestConstants.getTestResource("valid-docker-compose.yml"));
        assertTrue(dockerComposeValidator.isValid());
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
        assertTrue(dockerComposeValidator.hasServices("db","admin","roadmappio"));
    }

    @Test
    public void testServicesNotInComposeFile() {
        DockerComposeValidator dockerComposeValidator =
                new DockerComposeValidator(TestConstants.getTestResource("valid-docker-compose.yml"));
        assertFalse(dockerComposeValidator.hasServices("xxx"));
    }
}
