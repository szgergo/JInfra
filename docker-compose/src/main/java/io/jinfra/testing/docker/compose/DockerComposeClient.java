package io.jinfra.testing.docker.compose;

import io.jinfra.testing.CommandResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static io.jinfra.testing.ProcessRunner.runCommand;

public class DockerComposeClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerComposeClient.class);

    public static Integer runDockerCompose(String dockerComposeFilePath) {
        LOGGER.warn("************************************************");
        LOGGER.warn("Disclaimer: If the docker-compose file does not have health check only the start of the compose can be tested. ");
        LOGGER.warn("************************************************");
        try {
            if (!hasRuntimeDockerCompose()) {
                return Integer.MIN_VALUE;
            }
            final File dockerComposeFile = new File(dockerComposeFilePath);
            final File dockerComposeCanonicalFile;
            dockerComposeCanonicalFile = dockerComposeFile.getCanonicalFile();
            final String dockerComposeFileabsolutePath = dockerComposeCanonicalFile
                    .getAbsolutePath();
            final String command = "docker-compose -f "
                    + dockerComposeFileabsolutePath
                    + " up -d";
            final Optional<CommandResponse> commandResponse = runCommand(command);
            if (commandResponse.isPresent()) {
                Integer processResult = commandResponse.get().getProcessResultCode();
                final String stopCommand = "docker-compose -f " + dockerComposeFileabsolutePath + " down";
                runCommand(stopCommand);
                return processResult;
            }
            return null;
        } catch (Throwable e) {
            LOGGER.error("Error happened while running docker-compose file {}",
                    dockerComposeFilePath,
                    e);
            return null;
        }
    }

    public static String getDockerComposeClientVersion() {
        final Optional<CommandResponse> commandResponse = runCommand("docker-compose -v");

        if (commandResponse.isPresent()) {
            for (String stdOutLine : commandResponse.get().getStdOut()) {
                if (stdOutLine != null && stdOutLine.contains("docker-compose version")) {
                    return stdOutLine;
                }
            }
        }
        return null;
    }

    public static Integer checkDockerComposeFileValidity(File dockerComposeFile) {
        try {
            final String command = "docker-compose -f "
                    + dockerComposeFile.getCanonicalPath()
                    + " config";
            final Optional<CommandResponse> commandResponse = runCommand(command);
            if (commandResponse.isPresent()) {
                return commandResponse.get().getProcessResultCode();
            }
        } catch (IOException e) {
            LOGGER.error("Error while trying to validate docker-compose file: {}",
                    dockerComposeFile, e);
            return Integer.MIN_VALUE;
        }
        return Integer.MIN_VALUE;
    }

    private static boolean hasRuntimeDockerCompose() {
        final Optional<CommandResponse> commandResponse = runCommand("docker-compose -v");
        return commandResponse.isPresent()
                && Integer.valueOf(0).equals(commandResponse.get().getProcessResultCode());
    }
}
