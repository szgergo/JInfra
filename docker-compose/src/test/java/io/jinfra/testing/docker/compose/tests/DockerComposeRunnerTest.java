package io.jinfra.testing.docker.compose.tests;

import io.jinfra.testing.docker.compose.DockerComposeRunner;
import org.junit.Test;

import static io.jinfra.testing.TestConstants.getTestResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class DockerComposeRunnerTest {

    @Test
    public void testRunningValidDockerComposeFile() {
        assumeDockerComposeVersion();
        assertTrue(DockerComposeRunner
                .canRunDockerCompose(getTestResource("runnable-docker-compose.yml")));
    }

    @Test
    public void testDockerComposeWithCorrectHealthCheck() {
        assumeDockerComposeVersion();
        assertTrue(DockerComposeRunner
                .canRunDockerCompose(getTestResource("healthcheck_docker-compose.yml")));
    }

    @Test
    public void testDockerComposeAlwaysFailingHealthCheck() {
        assumeDockerComposeVersion();
        assertFalse(DockerComposeRunner
                .canRunDockerCompose(getTestResource("incorrect-health-check-docker-compose.yml")));
    }

    @Test
    public void testDockerComposeClientVersion() {
        assumeDockerComposeVersion();
        assertTrue(DockerComposeRunner.hasRunTimeDockerComposeClientVersion("1.28.2"));
    }

    private void assumeDockerComposeVersion() {
        assumeTrue("docker-compose version is not 1.28.2",
                DockerComposeRunner.hasRunTimeDockerComposeClientVersion("1.28.2"));
    }
}
