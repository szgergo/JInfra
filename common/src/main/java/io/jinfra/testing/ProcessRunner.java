package io.jinfra.testing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ProcessRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessRunner.class);

    public static Optional<CommandResponse> runCommand(String command) {
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        Process process;
        try {
            if (isWindows) {
                //TODO: Check if runs on windows
                process = new ProcessBuilder()
                        //.inheritIO()
                        .command(command)
                        .start();
            } else {
                process = new ProcessBuilder()
                        //.inheritIO()
                        .command(Arrays.asList(command.split(" ")))
                        .start();
            }

            List<String> stdOut = new ArrayList<>();
            List<String> stdErr = new ArrayList<>();
            CompletableFuture<String> soutFut = readOutStream(process.getInputStream());
            CompletableFuture<String> serrFut = readOutStream(process.getErrorStream());
            CompletableFuture<String> resultFut = soutFut.thenCombine(serrFut, (stdout, stderr) -> {
                if (stderr != null && !stderr.isEmpty()) {
                    LOGGER.error(stderr);
                    stdErr.add(stderr);
                }
                if (stdout != null && !stdout.isEmpty()) {
                    LOGGER.info(stdout);
                    stdOut.add(stdout);
                }
                return stdout;
            });

            resultFut.get(10, TimeUnit.MINUTES);

            if (process.waitFor(10, TimeUnit.MINUTES)) {
                Integer exitValue = process.exitValue();
                final CommandResponse commandResponse
                        = new CommandResponse(stdOut, stdErr, exitValue);
                LOGGER.info("CommandResponse: {}", commandResponse);
                return Optional.of(commandResponse);
            } else {
                LOGGER.error("Command execution timed out.");
                return Optional.empty();
            }

        } catch (Throwable exception) {
            LOGGER.error("Error happened executing the command: ", exception);
            return Optional.empty();
        }
    }

    static CompletableFuture<String> readOutStream(InputStream is) {
        return CompletableFuture.supplyAsync(() -> {
            try (
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr)
            ) {
                StringBuilder res = new StringBuilder();
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    res.append(inputLine).append(System.lineSeparator());
                }
                return res.toString();
            } catch (Throwable e) {
                throw new RuntimeException("problem with executing program", e);
            }
        });
    }
}
