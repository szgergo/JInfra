package io.jinfra.testing.docker.compose;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.jinfra.testing.docker.compose.utils.DockerComposeUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DockerComposeValidator {

    public static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerComposeValidator.class);
    private Optional<JsonNode> dockerComposeJsonNode;

    public DockerComposeValidator(File dockerComposeFile) {
        if (dockerComposeFile == null) {
            throw new IllegalArgumentException("Argument can't be null");
        }
        Path dockerComposeFilePath = dockerComposeFile.toPath();
        if (!Files.exists(dockerComposeFilePath) || !Files.isRegularFile(dockerComposeFilePath)) {
            throw new IllegalArgumentException("File can't be found or not a file: " + dockerComposeFile);
        }
        this.dockerComposeJsonNode = loadDockerComposeFile(dockerComposeFile);
    }

    public DockerComposeValidator(String filePath) {
        this(new File(filePath));
    }

    public boolean hasVersion(String version) {
        if(dockerComposeJsonNode.isPresent() && version != null) {
            final String versionFromComposeFile = dockerComposeJsonNode.get().get("version").asText();
            return version.equals(versionFromComposeFile);
        }
        return false;
    }

    private Optional<JsonNode> loadDockerComposeFile(File dockerComposeFile) {
        try {
            return Optional
                    .of(new ObjectMapper(new YAMLFactory())
                            .reader()
                            .readTree(FileUtils
                                    .readFileToString(dockerComposeFile, UTF_8)));
        } catch (Throwable e) {
            LOGGER.error("Unable to load docker-compose file: {}", dockerComposeFile.getAbsoluteFile().getName());
        }
        return Optional.empty();
    }

    public boolean hasNumberOfServices(int numberOfServices) {
        if(dockerComposeJsonNode.isPresent() && numberOfServices > -1) {
            final int numberOfServicesFromComposeFile = DockerComposeUtils
                    .getServiceCount(dockerComposeJsonNode.get());
            return numberOfServicesFromComposeFile == numberOfServices;
        }
        return false;
    }

    public boolean hasServices(String... services) {
        if(services != null && services.length != 0) {
            List<String> servicesList = Arrays.asList(services);
            List<String> servicesInDockerCompose = DockerComposeUtils
                    .getServices(dockerComposeJsonNode.get());
            return servicesInDockerCompose.containsAll(servicesList);
        }
        return false;
    }
}
