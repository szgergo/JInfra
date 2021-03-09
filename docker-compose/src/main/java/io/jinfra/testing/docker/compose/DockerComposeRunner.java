package io.jinfra.testing.docker.compose;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DockerComposeRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerComposeRunner.class);

    public static boolean canRunDockerCompose(String dockerComposeFilePath) {
        return Integer.valueOf(0)
                .equals(DockerComposeClient.runDockerCompose(dockerComposeFilePath));
    }

    public static boolean hasRunTimeDockerComposeClientVersion(String version) {
        final String  dockerComposeClientVersion = DockerComposeClient
                .getDockerComposeClientVersion();

        return dockerComposeClientVersion != null
                && dockerComposeClientVersion.contains(version);
    }

    public static boolean isValid(File dockerComposeFile) {
        try {
            return Integer
                    .valueOf(0)
                    .equals(DockerComposeClient
                            .checkDockerComposeFileValidity(dockerComposeFile));
        } catch (Throwable exception) {
            LOGGER.error("Error while trying to validate docker-compose file: ", exception);
            return false;
        }
    }
}
