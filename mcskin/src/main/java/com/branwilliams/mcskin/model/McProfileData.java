package com.branwilliams.mcskin.model;

import java.util.List;

/**
 * @author Brandon
 * @since November 24, 2019
 */
public class McProfileData {

    private String id;

    private String name;

    private List<McProfileProperty> properties;

    public McProfileData(String id, String name, List<McProfileProperty> properties) {
        this.id = id;
        this.name = name;
        this.properties = properties;
    }

    public McProfileProperty getProperty(String name) {
        for (McProfileProperty property : properties) {
            if (property.getName().equalsIgnoreCase(name))
                return property;
        }
        return null;
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

    public List<McProfileProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<McProfileProperty> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "McProfileData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", properties=" + properties +
                '}';
    }
}
