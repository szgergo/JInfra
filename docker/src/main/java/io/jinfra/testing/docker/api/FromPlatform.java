package io.jinfra.testing.docker.api;

public enum FromPlatform {

    linux_amd64("linux/amd64"),
    linux_arm64("linux/arm64"),
    windows_amd64("windows/amd64"),
    $BUILDPLATFORM("$BUILDPLATFORM");

    private final String platform;

    FromPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }

    public static FromPlatform calculatePlatform(String rawPlatform) {

        for(FromPlatform fromPlatform : values()) {
            if(fromPlatform.getPlatform().equals(rawPlatform)) {
                return fromPlatform;
            }
        }
        return null;
    }
}
