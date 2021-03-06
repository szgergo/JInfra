package io.jinfra.testing.docker.tests;

import io.jinfra.testing.docker.utils.DockerClient;
import org.junit.Test;

import static io.jinfra.testing.TestConstants.getTestResource;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DockerClientTest {

    @Test
    public void testBuildingValidImage() {
         boolean isBuildSuccessful = DockerClient
                .buildImageWithDockerFile(getTestResource("Dockerfile"),
                        "myLittleImage") != null;
        assertTrue(isBuildSuccessful);
    }

    @Test
    public void testBuildingAndRunningAnImage() {
        String imageId = DockerClient.buildImageWithDockerFile(getTestResource("Dockerfile"));
        assertNotNull(imageId);
        String containerId = DockerClient.runImage(imageId);
        assertNotNull(containerId);
    }
}
