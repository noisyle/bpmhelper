package com.chinanx.springboot.model;

public class ProcVariable {
    private long id;
    private String name;
    private String value;
    private long modelId;
    private long resourceId;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
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
    public long getModelId() {
        return modelId;
    }
    public void setModelId(long modelId) {
        this.modelId = modelId;
    }
    public long getResourceId() {
        return resourceId;
    }
    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }
}
