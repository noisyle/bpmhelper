package com.chinanx.springboot.model;

public class ProcVariable {
    private long id;
    private String name;
    private String value;
    private long modelId;
    private String resourceId;
    
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
    public String getResourceId() {
        return resourceId;
    }
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
    
    @Override
    public String toString() {
        return "ProcVariable [id=" + id + ", name=" + name + ", value=" + value + ", modelId=" + modelId
                + ", resourceId=" + resourceId + "]";
    }
    
}
