package io.jinfra.testing;

import java.io.File;

public class TestConstants {

    private static final String TEST_RESOURCE_BASE_PATH = "src" + File.separator + "test" + File.separator + "resources";

    public static String getTestResource(String resource) {
        if(resource.contains("/") || resource.contains("\\")) {
            throw new IllegalArgumentException("Please use only single resource element. Use TestConstants#concatPaths for deeper paths");
         }
        return TEST_RESOURCE_BASE_PATH + File.separator + resource;
    }

    public static File getTestResourceAsFile(String resource) {
        return new File(getTestResource(resource));
    }

}
