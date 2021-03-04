package io.jinfra.testing.docker.tests.instruction.api;

import io.jinfra.testing.docker.DockerRunner;
import io.jinfra.testing.docker.api.instructions.RunInstruction;
import org.junit.Test;

import java.io.IOException;

import static io.jinfra.testing.TestConstants.getTestResource;
import static org.junit.Assert.*;

public class RunInstructionTest {

    private static final String DUMMY_SHELL_COMMAND = "mkdir -p";
    private static final String DUMMY_EXEC_TYPE_COMMAND = "[\"/bin/bash\", \"-c\", \"echo hello\"]";

    @Test
    public void testBashRunCommand() {
        RunInstruction runInstruction = new RunInstruction(DUMMY_SHELL_COMMAND);
        assertEquals(DUMMY_SHELL_COMMAND, runInstruction.getCommand());
    }

    @Test
    public void testExecBasedRunCommand() {
        RunInstruction runInstruction = new RunInstruction(DUMMY_EXEC_TYPE_COMMAND);
        assertEquals(DUMMY_EXEC_TYPE_COMMAND, runInstruction.getCommand());
    }

    @Test
    public void testBashCommandRunTypeRecognition() {
        RunInstruction runInstruction = new RunInstruction(DUMMY_SHELL_COMMAND);
        assertEquals(DUMMY_SHELL_COMMAND, runInstruction.getCommand());
        assertTrue(runInstruction.hasShellCommand());
        assertFalse(runInstruction.hasExecCommand());
    }

    @Test
    public void testExecFormCommandRecognition() {
        RunInstruction runInstruction = new RunInstruction(DUMMY_EXEC_TYPE_COMMAND);
        assertEquals(DUMMY_EXEC_TYPE_COMMAND, runInstruction.getCommand());
        assertTrue(runInstruction.hasExecCommand());
        assertFalse(runInstruction.hasShellCommand());
    }

    @Test
    public void testNullCommand() {
        RunInstruction runInstruction = new RunInstruction(null);
        assertFalse(runInstruction.hasShellCommand());
        assertFalse(runInstruction.hasExecCommand());
        assertNull(runInstruction.getCommand());
    }

    @Test
    public void testEmptyCommand() {
        RunInstruction runInstruction = new RunInstruction("");
        assertFalse(runInstruction.hasShellCommand());
        assertFalse(runInstruction.hasExecCommand());
        assertNull(runInstruction.getCommand());
    }

    @Test
    public void testExecCommandValidation() throws InterruptedException {
        assertTrue(DockerRunner.canDockerFileBeStarted(getTestResource("Dockerfile")));
    }
}
