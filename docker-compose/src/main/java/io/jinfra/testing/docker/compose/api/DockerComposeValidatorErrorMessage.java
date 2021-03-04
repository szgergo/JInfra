package io.jinfra.testing.docker.compose.api;

import java.util.Objects;

public class DockerComposeValidatorErrorMessage {

    private final String message;
    private final String path;
    private final String type;

    public DockerComposeValidatorErrorMessage(String message, String path, String type) {
        this.message = message;
        this.path = path;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DockerComposeValidatorErrorMessage that = (DockerComposeValidatorErrorMessage) o;
        return Objects.equals(getMessage(), that.getMessage()) && Objects.equals(getPath(), that.getPath()) && Objects.equals(getType(), that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessage(), getPath(), getType());
    }

    @Override
    public String toString() {
        return "DockerComposeValidatorErrorMessage{" +
                "message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
