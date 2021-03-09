package io.jinfra.testing.kubernetes;

import io.jinfra.testing.CommandResponse;
import io.jinfra.testing.docker.utils.DockerClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class KubernetesArtifactValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesArtifactValidator.class);
    private static final String PASS_KUBERNETES_VALIDATOR = "PASS";

    public static boolean isArtifactValid(String k8sArtifact) {
        if(k8sArtifact == null || k8sArtifact.isEmpty()) {
            return false;
        }
        try {
            final File k8sArtifactFile = new File(k8sArtifact);
            final File k8sArtifactAbsoluteFile = k8sArtifactFile.getCanonicalFile();
            final String osArch = System.getProperty("os.arch");

            final String kubevalDockerFile
                    = getAbsolutePathForClassPathResource("kubeval_0_15_Dockerfile").trim();
            final String kubevalDockerContainerName = UUID.randomUUID().toString();

            //Experimental feature platform for apple silicon.
            boolean isArmV8 = "aarch64".equalsIgnoreCase(osArch);
            final List<String> platform = isArmV8 ? Arrays.asList("--platform","linux/amd64") : null;

            final String builtContainerName = DockerClient
                    .buildImageWithDockerFile(kubevalDockerFile,
                            kubevalDockerContainerName,
                            Arrays.asList("--build-arg",
                                    "file_to_validate="
                                            + readFileIntoString(k8sArtifactAbsoluteFile)));

            if(StringUtils.isNotBlank(builtContainerName)) {
                return DockerClient
                        .runImage(builtContainerName,
                                platform,
                                        (CommandResponse commandResponse)
                                                -> commandResponse
                                                .getStdOut()
                                                .stream()
                                                .anyMatch((String line) -> line.contains(PASS_KUBERNETES_VALIDATOR))) != null;
            }
        } catch (Throwable e) {
            LOGGER.error("Error happened during k8s artifact {} validation ",k8sArtifact,e);
            return false;
        }
        return false;
    }

    private static String getAbsolutePathForClassPathResource(String classPathResource) {
        try {
            return new File(Thread
                    .currentThread()
                    .getContextClassLoader()
                    .getResource(classPathResource).toURI())
                    .getAbsolutePath();
        } catch (Throwable e) {
            LOGGER.error("Error while trying to load classpath resource: {} ",classPathResource,e);
            return null;
        }
    }

    private static String readFileIntoString(File k8sArtifactAbsoluteFile) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(k8sArtifactAbsoluteFile
                .getAbsolutePath()), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append(System.lineSeparator()));
        }
        catch (Throwable e) {
            LOGGER.error("Error while loading k8s artifact: {} ",k8sArtifactAbsoluteFile,e);
        }

        return contentBuilder.toString();
    }
}
