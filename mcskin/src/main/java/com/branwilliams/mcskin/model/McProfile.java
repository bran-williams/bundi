package com.branwilliams.mcskin.model;

/**
 * @author Brandon
 * @since November 24, 2019
 */
public class McProfile {

    private String id;

    private String name;

    public McProfile(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "McProfile{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
