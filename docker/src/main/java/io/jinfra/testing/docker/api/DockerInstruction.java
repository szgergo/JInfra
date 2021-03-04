package io.jinfra.testing.docker.api;

//Based on https://docs.docker.com/engine/reference
public enum DockerInstruction {
    FROM,
    RUN,
    CMD,
    LABEL,
    MAINTAINER,
    EXPOSE,
    ENV,
    ADD,
    COPY,
    ENTRYPOINT,
    VOLUME,
    USER,
    WORKDIR,
    ARG,
    ONBUILD,
    STOPSIGNAL,
    HEALTHCHECK,
    SHELL
}
