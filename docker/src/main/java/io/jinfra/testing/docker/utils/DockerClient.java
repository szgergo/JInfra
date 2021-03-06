package io.jinfra.testing.docker.utils;

import io.jinfra.testing.CommandResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static io.jinfra.testing.ProcessRunner.runCommand;

public class DockerClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerClient.class);
    private static final String IMAGE_LIFECYCLE_MESSAGE = " image {} with name {}";

    public static String buildImageWithDockerFile(String dockerFilePath) {
        return buildImageWithDockerFile(dockerFilePath,UUID.randomUUID().toString());
    }

    public static String buildImageWithDockerFile(String dockerFilePath, String name) {
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
            final String command = "docker build" +
                    " -t " +
                    name.toLowerCase()
                    +
                    " -f " +
                    fullFilePath.getPath() +
                    " " +
                    fullFilePath.getParent();

            LOGGER.info("Sending command to runtime: {}", command);

            final Optional<CommandResponse> commandResponse = runCommand(command);
            if(commandResponse.isPresent()) {
                return new Integer(0)
                        .equals(commandResponse.get().getProcessResultCode()) ? name : null;
            }
        } catch (IOException e) {
            LOGGER.error("Error happened: ", e);
            return null;
        }
        return null;
    }

    public static String runImage(String imageName) {
        return runImage(imageName," -it -d", null);
    }

    public static String runImage(String imageName, String additionalProps) {
        return  runImage(imageName,additionalProps,null);
    }

    public static String runImage(String imageName,
                                  String additionalProps,
                                  String additionalCommand) {
        if (imageName == null || imageName.length() == 0) {
            return null;
        }

        final String containerName = UUID.randomUUID().toString();
        //docker run -it -v `pwd`/pv.yaml:/pv.yaml garethr/kubeval pv.yaml
        final String command = "docker run"
                + Optional.ofNullable(additionalProps).orElse("")
                + " --rm --name "
                + containerName + " "
                + imageName
                + " "
                + Optional.ofNullable(additionalCommand).orElse("");
        final String stopCommand = "docker container stop " + containerName;

        LOGGER.info("Sending command to docker runTime: {}", command);

        LOGGER.debug("Staring" + IMAGE_LIFECYCLE_MESSAGE, imageName, containerName);
        final Optional<CommandResponse> commandResponse = runCommand(command);
        if(commandResponse.isPresent()) {
            Integer processCode = commandResponse.get().getProcessResultCode();
            LOGGER.debug("Stopping" + IMAGE_LIFECYCLE_MESSAGE, imageName, containerName);
            runCommand(stopCommand);

            return new Integer(0)
                    .equals(processCode)
                    ? containerName
                    : null;
        }
      return null;
    }
}
