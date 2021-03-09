package io.jinfra.testing.docker.utils;

import io.jinfra.testing.CommandResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static io.jinfra.testing.ProcessRunner.createCommandList;
import static io.jinfra.testing.ProcessRunner.runCommand;

public class DockerClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerClient.class);
    private static final String IMAGE_LIFECYCLE_MESSAGE = " image {} with name {}";
    public static final Predicate<CommandResponse> NO_OP_ADDITIONAL_CHECK = (CommandResponse commandResponse) -> false;


    public static String buildImageWithDockerFile(String dockerFilePath) {
        return buildImageWithDockerFile(dockerFilePath,UUID.randomUUID().toString());
    }

    public static String buildImageWithDockerFile(String dockerFilePath,
                                                  String name) {
        return buildImageWithDockerFile(dockerFilePath,name,null);
    }

    public static String buildImageWithDockerFile(String dockerFilePath,
                                                  String name,
                                                  List<String> additionalArguments) {
        try {
            if(dockerFilePath == null || dockerFilePath.length() == 0
            || name == null || name.length() == 0) {
                return null;
            }

            final File dockerFile = new File(dockerFilePath);
            if(!dockerFile.exists()) {
                LOGGER.error("Non-existing dockerfile: {}", dockerFilePath);
                return null;
            }
            final File fullFilePath = dockerFile.getCanonicalFile();
            final List<String> command = createCommandList("docker",
                    "build",
                    additionalArguments,
                    "-t",
                    name.toLowerCase(),
                    "-f",
                    fullFilePath.getPath(),
                    fullFilePath.getParent());

            LOGGER.info("Sending command to runtime: {}", command);

            final Optional<CommandResponse> commandResponse = runCommand(command);
            if(commandResponse.isPresent()) {
                return Integer.valueOf(0)
                        .equals(commandResponse.get().getProcessResultCode()) ? name : null;
            }
        } catch (IOException e) {
            LOGGER.error("Error happened: ", e);
            return null;
        }
        return null;
    }

    public static String runImage(String imageName) {
        return runImage(imageName,
                Arrays.asList("-it","-d"),
                null,NO_OP_ADDITIONAL_CHECK);
    }

    public static String runImage(String imageName,
                                  List<String> additionalProps,
                                  Predicate<CommandResponse> passPredicate) {
        return runImage(imageName,additionalProps,
                null,
                passPredicate);
    }

    public static String runImage(String imageName, List<String> additionalProps) {
        return runImage(imageName,additionalProps,null,
                NO_OP_ADDITIONAL_CHECK);
    }

    public static String runImage(String imageName,
                                  List<String> additionalProps,
                                  List<String> additionalCommand,
                                  Predicate<CommandResponse> additionalChecks) {
        if (imageName == null || imageName.length() == 0) {
            return null;
        }

        final String containerName = UUID.randomUUID().toString();
        final List<String> command = createCommandList("docker",
                "run",
                additionalProps,
                "--rm",
                "--name",
                containerName,
                imageName,
                additionalCommand);

        final List<String> stopCommand = Arrays.asList("docker","container","stop", containerName);

        LOGGER.info("Sending command to docker runTime: {}", command);

        LOGGER.info("Staring" + IMAGE_LIFECYCLE_MESSAGE, imageName, containerName);
        final Optional<CommandResponse> commandResponse = runCommand(command);
        if(commandResponse.isPresent()) {
            final CommandResponse actualCommandResponse = commandResponse.get();
            Integer processCode = actualCommandResponse.getProcessResultCode();
            LOGGER.info("Stopping" + IMAGE_LIFECYCLE_MESSAGE, imageName, containerName);
            runCommand(stopCommand);

            return Integer.valueOf(0)
                    .equals(processCode)
                    || additionalChecks.test(actualCommandResponse)
                    ? containerName
                    : null;
        }
      return null;
    }


}
