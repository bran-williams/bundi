package com.branwilliams.mcskin.model;

/**
 * @author Brandon
 * @since November 24, 2019
 */
public class McProfileProperty {

    private String name;

    private String value;

    private String signature;

    public McProfileProperty(String name, String value, String signature) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "McProfileProperty{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
