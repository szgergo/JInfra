package io.jinfra.testing.docker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DockerFileValidator {

    private final List<String> dockerFile;

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerFileValidator.class);

    public DockerFileValidator(String dockerFilePath) throws IOException {
        dockerFile = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(dockerFilePath))) {
            String line;
            StringBuilder finickyLine = new StringBuilder();
            while ((line = br.readLine()) != null) {
                if(line.isEmpty()) {
                    continue;
                }
                if(line.length() > 1 && line.lastIndexOf("\\") == line.length() -1 ) {
                    finickyLine.append(line);
                } else {
                    if(finickyLine.length() != 0) {
                        dockerFile.add(finickyLine.toString());
                    }
                    finickyLine = new StringBuilder();
                    dockerFile.add(line);
                }
            }
        }
        for (String lineFromFile : dockerFile) {
            LOGGER.debug("Processed line: {} ", lineFromFile);
        }
    }
}
