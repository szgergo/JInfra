package io.jinfra.testing.docker.compose.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DockerComposeUtils {

    private static final String SERVICES = "services";

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerComposeUtils.class);

    private static final ObjectReader YAML_OBJECT_READER;

    static {
        YAML_OBJECT_READER = new ObjectMapper(new YAMLFactory()).reader();
    }

    public static List<String> getServices(String dockerComposeFilePath) {
        try {
            return getServices(YAML_OBJECT_READER
                    .readTree(loadFile(dockerComposeFilePath)));
        } catch (Throwable e) {
            LOGGER.error("Error while loading docker-compose file {}", dockerComposeFilePath, e);
            return Collections.emptyList();
        }
    }

    public static List<String> getServices(JsonNode jsonNode) {
        JsonNode servicesNode = jsonNode.get(SERVICES);
        List<String> services = new ArrayList<>();
        if(servicesNode == null) {
            return Collections.emptyList();
        }

        Iterator<String> servicesIterator = servicesNode.fieldNames();

        while(servicesIterator.hasNext()) {
            String nextService = servicesIterator.next();
            services.add(nextService);
        }

        return services;
    }

    public static int getServiceCount(JsonNode jsonNode) {
        JsonNode servicesNode = jsonNode.get(SERVICES);

        if(servicesNode == null) {
            return 0;
        }

        return servicesNode.size();
    }

    public static String loadFile(String filePath) {
        try {
            return IOUtils.resourceToString(filePath, Charset.forName("UTF-8"));
        } catch (Throwable e) {
            LOGGER.error("Error while loading file: {}", filePath, e);
            return null;
        }
    }
}
