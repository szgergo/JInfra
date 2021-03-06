package io.jinfra.testing.kubernetes.tests;

import io.jinfra.testing.kubernetes.KubernetesArtifactValidator;
import org.junit.Test;

import static io.jinfra.testing.TestConstants.getTestResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KubernetesArtifactValidatorTest {

    @Test
    public void testValidK8sArtifact() {
        assertTrue(KubernetesArtifactValidator
                .isArtifactValid(getTestResource("valid-k8s-artifact-namespace.json")));

    }

    @Test
    public void testInvalidK8sArtifact() {
        assertFalse(KubernetesArtifactValidator
                .isArtifactValid(getTestResource("invalid-k8s-artifact-namespace.json")));
    }
}
