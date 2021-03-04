package io.jinfra.testing.docker.compose;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import io.jinfra.testing.docker.compose.api.DockerComposeValidatorErrorMessage;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class DockerComposeValidator {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final String SERVICES = "services";
    private final File dockerComposeFile;
    private final ObjectMapper objectMapper;
    private final JsonSchemaFactory factory;
    private final String dockerComposeSchemaFile;
    private static final String DOCKER_COMPOSE_SCHEMA_FILE_NAME = "schema/docker-compose-schema.json";
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerComposeValidator.class);
    private static Optional<JsonNode> jsonNode;

    public DockerComposeValidator(File dockerComposeFile) {
        jsonNode = Optional.empty();
        if (dockerComposeFile == null) {
            throw new IllegalArgumentException("Argument can't be null");
        }
        Path dockerComposeFilePath = dockerComposeFile.toPath();
        if (!Files.exists(dockerComposeFilePath) || !Files.isRegularFile(dockerComposeFilePath)) {
            throw new IllegalArgumentException("File can't be found or not a file: " + dockerComposeFile);
        }
        this.dockerComposeFile = dockerComposeFile;
        this.objectMapper = new ObjectMapper(new YAMLFactory());
        this.objectMapper.registerModules();
        this.dockerComposeSchemaFile = readSchemaFile();
        factory = JsonSchemaFactory
                .builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909))
                .objectMapper(objectMapper)
                .build();
        if(isValid()) {
            loadDockerComposeFile();
        }
    }

    public DockerComposeValidator(String filePath) {
        this(new File(filePath));
    }

    public boolean isValid() {
        try {
            return factory
                    .getSchema(dockerComposeSchemaFile)
                    .validate(jsonNode.isPresent() ? jsonNode.get() : objectMapper.readTree(dockerComposeFile))
                    .isEmpty();
        } catch (Throwable exception) {
            LOGGER.error("Error while trying to validate docker-compose file: ", exception);
            return false;
        }
    }

    public Set<DockerComposeValidatorErrorMessage> isValidWithErrorMessages() {
        try {
            return Optional.ofNullable(factory
                    .getSchema(dockerComposeSchemaFile)
                    .validate(objectMapper.readTree(dockerComposeFile)))
                    .orElse(Collections.emptySet())
                    .stream()
                    .map(validationMessage ->
                            new DockerComposeValidatorErrorMessage(validationMessage.getMessage(), validationMessage.getPath(), validationMessage.getType()))
                    .collect(Collectors.toSet());
        } catch (Throwable exception) {
            exception.printStackTrace();
            return Collections.emptySet();
        }

    }

    public boolean hasVersion(String version) {
        if(jsonNode.isPresent() && version != null) {
            final String versionFromComposeFile = jsonNode.get().get("version").asText();
            return version.equals(versionFromComposeFile);
        }
        return false;
    }

    private void loadDockerComposeFile() {
        try {
            jsonNode = Optional.of(objectMapper.readTree(dockerComposeFile));
        } catch (IOException e) {
            LOGGER.error("Unable to load docker-compose file: {}", dockerComposeFile);
        }
    }

    private String readSchemaFile() {
        try {
            return IOUtils
                    .toString(ClassLoader.getSystemResourceAsStream(DOCKER_COMPOSE_SCHEMA_FILE_NAME),
                            UTF_8);
        } catch (IOException e) {
            LOGGER.error("Exception while loading schema file: ", e);
            return null;
        }
    }

    public boolean hasNumberOfServices(int numberOfServices) {
        if(jsonNode.isPresent() && numberOfServices > -1) {
            final int numberOfServicesFromComposeFile = jsonNode.get().get(SERVICES).size();
            return numberOfServicesFromComposeFile == numberOfServices;
        }
        return false;
    }

    public boolean hasServices(String... services) {
        if(services != null && services.length != 0) {
            List<String> servicesList = Arrays.asList(services);
            for (Iterator<String> it = jsonNode.get().get(SERVICES).fieldNames(); it.hasNext(); ) {
                String service = it.next();
                if (!servicesList.contains(service)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
