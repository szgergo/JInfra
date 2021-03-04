package io.jinfra.testing.docker.api;

public abstract class DockerFileInstruction {

    private final DockerInstruction dockerInstruction;

    public DockerFileInstruction(DockerInstruction dockerInstruction) {
        this.dockerInstruction = dockerInstruction;
    }

    public DockerInstruction getDockerInstruction() {
        return dockerInstruction;
    }
}
