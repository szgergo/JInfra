package io.jinfra.testing.docker.tests.instruction.api;

import io.jinfra.testing.docker.api.DockerInstruction;
import io.jinfra.testing.docker.api.instructions.FromInstruction;
import io.jinfra.testing.docker.api.FromPlatform;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FromInstructionTest {

    @Test
    public void testFromInstructionWithPlatform() {
        FromInstruction fromInstruction
                = new FromInstruction("--platform=linux/amd64 postgresql:latest AS myPostgres");
        assertEquals(DockerInstruction.FROM,fromInstruction.getDockerInstruction());
        assertEquals(FromPlatform.linux_amd64, fromInstruction.getPlatform());
    }

    @Test
    public void testFromInstructionWithoutPlatform() {
        FromInstruction fromInstruction
                = new FromInstruction("postgresql:latest AS myPostgres");
        assertEquals(DockerInstruction.FROM,fromInstruction.getDockerInstruction());
        assertNull(fromInstruction.getPlatform());
    }

    @Test
    public void testFromInstructionInvalidPlatform() {
        FromInstruction fromInstruction
                = new FromInstruction("--platform=something postgresql:latest AS myPostgres");
        assertEquals(DockerInstruction.FROM,fromInstruction.getDockerInstruction());
        assertNull(fromInstruction.getPlatform());
    }

    @Test
    public void testFromInstructionValidImageWithTag() {
        final String expectedImage = "postgresql";
        FromInstruction fromInstruction
                = new FromInstruction(expectedImage + ":latest AS myPostgres");
        assertEquals(DockerInstruction.FROM,fromInstruction.getDockerInstruction());
        assertEquals(expectedImage, fromInstruction.getImage());
    }

    @Test
    public void testValidImageAndPlatform() {
        final String expectedImage = "postgresql";
        FromInstruction fromInstruction
                = new FromInstruction("--platform=something " + expectedImage + ":latest AS myPostgres");
        assertEquals(DockerInstruction.FROM,fromInstruction.getDockerInstruction());
        assertEquals(expectedImage, fromInstruction.getImage());
    }

    @Test
    public void testValidImageOnlyImageAsParam() {
        final String expectedImage = "postgresql";
        FromInstruction fromInstruction
                = new FromInstruction(expectedImage);
        assertEquals(DockerInstruction.FROM,fromInstruction.getDockerInstruction());
        assertEquals(expectedImage, fromInstruction.getImage());
    }

    @Test
    public void testValidImageWithTag() {
        final String expectedImage = "postgresql";
        FromInstruction fromInstruction
                = new FromInstruction(expectedImage + ":latest");
        assertEquals(DockerInstruction.FROM,fromInstruction.getDockerInstruction());
        assertEquals(expectedImage, fromInstruction.getImage());
    }

    @Test
    public void testValidImageWithLatestTag() {
        final String expectedTag = "latest";
        FromInstruction fromInstruction
                = new FromInstruction("postgresql:" + expectedTag);
        assertEquals(DockerInstruction.FROM,fromInstruction.getDockerInstruction());
        assertEquals(expectedTag, fromInstruction.getTag());
    }

    @Test
    public void testValidImageWithDigest() {
        final String expectedDigest = "123245546";
        FromInstruction fromInstruction
                = new FromInstruction("postgresql@" + expectedDigest);
        assertEquals(DockerInstruction.FROM,fromInstruction.getDockerInstruction());
        assertEquals(expectedDigest, fromInstruction.getDigest());
        assertNull(fromInstruction.getTag());
    }

    @Test
    public void testValidName() {
        final String expectedName = "myPostgres";
        FromInstruction fromInstruction
                = new FromInstruction("postgresql@123245546 AS " + expectedName);
        assertEquals(DockerInstruction.FROM,fromInstruction.getDockerInstruction());
        assertEquals(expectedName, fromInstruction.getName());
        assertNull(fromInstruction.getTag());
    }

    @Test
    public void testValidFullFromInstruction() {
        final String expectedImage = "postgresql";
        final String expectedTag = "latest";
        final String expectedName = "myPostgres";
        FromInstruction fromInstruction
                = new FromInstruction("--platform=linux/amd64 " + expectedImage + ":" + expectedTag + " AS " + expectedName);
        assertEquals(DockerInstruction.FROM,fromInstruction.getDockerInstruction());
        assertEquals(FromPlatform.linux_amd64,fromInstruction.getPlatform());
        assertEquals(expectedImage,fromInstruction.getImage());
        assertEquals(expectedTag,fromInstruction.getTag());
        assertEquals(expectedName,fromInstruction.getName());
        assertNull(fromInstruction.getDigest());
    }

    @Test
    public void testNull() {
        FromInstruction fromInstruction = new FromInstruction(null);
        assertEquals(DockerInstruction.FROM,fromInstruction.getDockerInstruction());
        assertNull(fromInstruction.getPlatform());
        assertNull(fromInstruction.getImage());
        assertNull(fromInstruction.getTag());
        assertNull(fromInstruction.getName());
        assertNull(fromInstruction.getDigest());
    }

    @Test
    public void testEmptyString() {
        FromInstruction fromInstruction = new FromInstruction("");
        assertEquals(DockerInstruction.FROM,fromInstruction.getDockerInstruction());
        assertNull(fromInstruction.getPlatform());
        assertNull(fromInstruction.getImage());
        assertNull(fromInstruction.getTag());
        assertNull(fromInstruction.getName());
        assertNull(fromInstruction.getDigest());
    }

    @Test
    public void testGibberish() {
        FromInstruction fromInstruction = new FromInstruction("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed");
        assertEquals(DockerInstruction.FROM,fromInstruction.getDockerInstruction());
        assertNull(fromInstruction.getPlatform());
        assertNull(fromInstruction.getImage());
        assertNull(fromInstruction.getTag());
        assertNull(fromInstruction.getName());
        assertNull(fromInstruction.getDigest());
    }
}
