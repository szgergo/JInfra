package io.jinfra.testing.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class DockerRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerRunner.class);
    private static final DockerClient dockerClient;

    static {
        final DefaultDockerClientConfig dockerConfig = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .build();
        final DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerConfig.getDockerHost())
                .sslConfig(dockerConfig.getSSLConfig())
                .build();
        dockerClient = DockerClientImpl.getInstance(dockerConfig, httpClient);
    }

    public static boolean canDockerFileBeStarted(String dockerFile) throws InterruptedException {
        BuildImageCmd buildImageCmd = dockerClient.buildImageCmd(new File(dockerFile));
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> buildImageCmd.exec(new ResultCallback<BuildResponseItem>() {
            @Override
            public void onStart(Closeable closeable) {
                LOGGER.info("Started building image...");
            }

            @Override
            public void onNext(BuildResponseItem object) {
                if(object.isBuildSuccessIndicated()) {
                    LOGGER.info("Build completed.");
                    LOGGER.info("Start running image {}", object.getImageId());
                    CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(object.getImageId());
                    CreateContainerResponse createContainerResponse = createContainerCmd.exec();
                    StartContainerCmd startContainerCmd = dockerClient.startContainerCmd(createContainerResponse.getId());
                    startContainerCmd.exec();
                    StopContainerCmd stopContainerCmd = dockerClient.stopContainerCmd(createContainerResponse.getId());
                    stopContainerCmd.exec();
                    LOGGER.info("Finished running image {}", object.getImageId());
                } else if(object.isErrorIndicated()) {
                    LOGGER.error("{}", object);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.error("", throwable);

            }

            @Override
            public void onComplete() {
                LOGGER.info("Finished building image.");
                executorService.shutdown();

            }

            @Override
            public void close() throws IOException {
                executorService.shutdown();

            }
        }));

        executorService.awaitTermination(600, TimeUnit.SECONDS);

        return true;
    }
}
