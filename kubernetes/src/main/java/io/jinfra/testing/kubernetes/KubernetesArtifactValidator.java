package io.jinfra.testing.kubernetes;

import io.jinfra.testing.docker.utils.DockerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class KubernetesArtifactValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesArtifactValidator.class);

    public static boolean isArtifactValid(String k8sArtifact) {
        if(k8sArtifact == null || k8sArtifact.isEmpty()) {
            return false;
        }
        try {
            final File k8sArtifactFile = new File(k8sArtifact);
            final File k8sArtifactAbsoluteFile = k8sArtifactFile.getCanonicalFile();
            final String k8sArtifactName = k8sArtifactFile.getName();
            final String osArch = System.getProperty("os.arch");
            boolean isArmV8 = "aarch64".equalsIgnoreCase(osArch);

            final String platform = isArmV8 ? " --platform linux/amd64" : "";
            //Experimental feature platform needed for apple silicon.
            final String additionalProps = platform
                    + " -v "
                    + k8sArtifactAbsoluteFile.getAbsolutePath()
                    + ":"
                    + "/"
                    + k8sArtifactName;

            return DockerClient
                    .runImage("garethr/kubeval:0.15.0",
                            additionalProps,
                            k8sArtifactName) != null;
        } catch (Throwable e) {
            LOGGER.error("Error happened during k8s artifact {} validation ",k8sArtifact,e);
            return false;
        }
    }
}
