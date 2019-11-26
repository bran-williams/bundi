package com.branwilliams.mcskin.model;

import java.util.Map;

/**
 * @author Brandon
 * @since November 24, 2019
 */
public class McProfileTextures {

    private long timestamp;

    private String profileId;

    private String profileName;

    private boolean signatureRequired;

    private Map<String, Map<String, Object>> textures;

    public McProfileTextures(long timestamp, String profileId, String profileName, boolean signatureRequired, Map<String, Map<String, Object>> textures) {
        this.timestamp = timestamp;
        this.profileId = profileId;
        this.profileName = profileName;
        this.signatureRequired = signatureRequired;
        this.textures = textures;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public boolean isSignatureRequired() {
        return signatureRequired;
    }

    public void setSignatureRequired(boolean signatureRequired) {
        this.signatureRequired = signatureRequired;
    }

    public Map<String, Map<String, Object>> getTextures() {
        return textures;
    }

    public void setTextures(Map<String, Map<String, Object>> textures) {
        this.textures = textures;
    }

    @Override
    public String toString() {
        return "McProfileTextures{" +
                "timestamp=" + timestamp +
                ", profileId='" + profileId + '\'' +
                ", profileName='" + profileName + '\'' +
                ", signatureRequired=" + signatureRequired +
                ", textures=" + textures +
                '}';
    }
}
