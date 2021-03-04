package io.jinfra.testing.docker.api.instructions;

import io.jinfra.testing.docker.api.DockerFileInstruction;
import io.jinfra.testing.docker.api.DockerInstruction;
import io.jinfra.testing.docker.api.FromPlatform;

import java.util.Objects;

public class FromInstruction extends DockerFileInstruction {

    private static final String PLATFORM_KEY = "--platform";
    private static final String PLATFORM_SPLIT_CHAR = "=";
    private static final String IMAGE_TAG_SPLIT_REGEX = "[:@]";

    private final FromPlatform platform;

    private final String image;

    private final String tag;

    private final String name;

    private final String digest;

    public FromInstruction(String rawParameter) {
        super(DockerInstruction.FROM);
        this.platform = gatherPlatform(rawParameter);
        this.image = gatherImage(rawParameter);
        this.tag = gatherTag(rawParameter);
        this.name = gatherName(rawParameter);
        this.digest = gatherDigest(rawParameter);
    }

    public FromPlatform getPlatform() {
        return platform;
    }

    public String getImage() {
        return image;
    }

    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    public String getDigest() {
        return digest;
    }

    private String gatherDigest(String rawParameter) {
        return gatherImageAttachedModifier(rawParameter,"@");
    }

    // And image attached modifier is a tag or digest, for example: postgresql:latest, here "latest" is an image attached modifier
    private String gatherImageAttachedModifier(String rawParameter,String modifierSeparator) {
        if(rawParameter == null || rawParameter.length() == 0) {
            return null;
        }
        final String imageAndTag = getImageAndTag(rawParameter);
        if(imageAndTag == null || imageAndTag.length() == 0) {
            return null;
        }
        final String[] splitImage = imageAndTag.split(modifierSeparator);
        if (splitImage.length == 1) {
            return null;
        }
        return splitImage[1];
    }

    private String gatherName(String rawParameter) {
        if (rawParameter == null || rawParameter.length() == 0) {
            return null;
        }
        String[] splitRawParameter = rawParameter.split(" AS ");
        if (splitRawParameter.length != 2) {
            return null;
        }

        return splitRawParameter[1].trim();
    }

    private String gatherTag(String rawParameter) {
        return gatherImageAttachedModifier(rawParameter,":");
    }

    private String[] splitImageAndTag(String imageAndTag) {
        return imageAndTag.split(IMAGE_TAG_SPLIT_REGEX);
    }

    private String gatherImage(String rawParameter) {
        String rawImage = getImageAndTag(rawParameter);
        if (rawImage == null) return null;
        final String[] splitImage = splitImageAndTag(rawImage);
        return splitImage[0];
    }

    private String getImageAndTag(String rawParameter) {
        if (rawParameter == null || rawParameter.length() == 0) {
            return null;
        }
        String[] split = rawParameter.split(" ");

        if (split.length < 1 || split.length > 5) {
            return null;
        }
        String rawImage;
        if (rawParameter.contains(PLATFORM_KEY)) {
            rawImage = split[1];
        } else {
            rawImage = split[0];
        }
        return rawImage;
    }

    private FromPlatform gatherPlatform(String rawParameter) {
        if(rawParameter == null || rawParameter.length() == 0) {
            return null;
        }
        if(rawParameter.contains(PLATFORM_KEY)) {
            String[] splitRawParameter = rawParameter.split(PLATFORM_SPLIT_CHAR);
            if(splitRawParameter.length != 2) {
                return null;
            }
            String platform;
            try {
                platform = splitRawParameter[1].substring(0, splitRawParameter[1].indexOf(" "));
            } catch (Throwable exception) {
                return null;
            }
            return FromPlatform.calculatePlatform(platform);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FromInstruction that = (FromInstruction) o;
        return Objects.equals(getPlatform(), that.getPlatform()) && Objects.equals(getImage(), that.getImage()) && Objects.equals(getTag(), that.getTag()) && Objects.equals(getName(), that.getName()) && Objects.equals(getDigest(), that.getDigest());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlatform(), getImage(), getTag(), getName(), getDigest());
    }

    @Override
    public String toString() {
        return "FromInstruction{" +
                "platform='" + platform + '\'' +
                ", image='" + image + '\'' +
                ", tag='" + tag + '\'' +
                ", name='" + name + '\'' +
                ", digest='" + digest + '\'' +
                '}';
    }
}
