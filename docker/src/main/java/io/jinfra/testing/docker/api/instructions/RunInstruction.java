package io.jinfra.testing.docker.api.instructions;

import io.jinfra.testing.docker.api.RunCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RunInstruction {


    private final String command;
    private final RunCommandType runCommandType;

    private static final Logger LOGGER = LoggerFactory.getLogger(RunInstruction.class);

    public RunInstruction(String rawParameter) {
        this.command = rawParameter == null || rawParameter.length() == 0 ? null : rawParameter;
        this.runCommandType = gatherCommandType(rawParameter);
    }

    public String getCommand() {
        return command;
    }

    public RunCommandType getRunCommandType() {
        return runCommandType;
    }

    public boolean hasExecCommand() {
        return RunCommandType.EXEC.equals(runCommandType);
    }

    public boolean hasShellCommand() {
        return RunCommandType.SHELL.equals(runCommandType);
    }

    public static RunCommandType gatherCommandType(String rawParameter) {
        if(rawParameter == null || rawParameter.length() == 0) {
            return null;
        }
        return rawParameter.startsWith("[") && rawParameter.endsWith("]")
                ? RunCommandType.EXEC
                : RunCommandType.SHELL;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunInstruction that = (RunInstruction) o;
        return Objects.equals(getCommand(), that.getCommand());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommand());
    }

    @Override
    public String toString() {
        return "RunInstruction{" +
                "command='" + command + '\'' +
                '}';
    }
}
