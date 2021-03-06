package io.jinfra.testing.docker;

import io.jinfra.testing.docker.utils.DockerClient;

public class DockerValidator {

    public static boolean canImageBeBuiltWithDockerFile(String dockerFile) {
        return DockerClient.buildImageWithDockerFile(dockerFile) != null;
    }
}
