package io.jinfra.testing;

import org.slf4j .Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class TestConstants {

    private static final String TEST_RESOURCE_BASE_PATH = "src" + File.separator + "test" + File.separator + "resources";
    private static final Logger LOGGER = LoggerFactory.getLogger(TestConstants.class);

    public static String getTestResource(String resource) {
        if(resource.contains("/") || resource.contains("\\")) {
            throw new IllegalArgumentException("Please use only single resource element. Use TestConstants#concatPaths for deeper paths");
         }
        return TEST_RESOURCE_BASE_PATH + File.separator + resource;
    }

    public static File getTestResourceAsFile(String resource) {
        return new File(getTestResource(resource));
    }

    public static String concatPaths(String... pathElements) {
        LOGGER.debug("Path elements to concat: {}", pathElements);
        if (pathElements == null || pathElements.length == 0) {
            return null;
        }

        String concatenatedPath = "";
        for(String pathElement : pathElements) {
            if(pathElement.contains("/") || pathElement.contains("\\")) {
                throw new IllegalArgumentException("Use only single path elements in list.");
            }
            concatenatedPath = concatenatedPath + File.separator + pathElement;
        }
        LOGGER.debug("Concatenated path: {}", concatenatedPath);
        return concatenatedPath;
    }
}
