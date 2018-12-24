package com.chinanx.springboot.model;

import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProcModel {
    private long id;
    private String name;
    private int version;
    private long deploymentId;
    private long sourceId;
    private String resourceName;
    private String dgrmResourceName;
    private byte[] bytes;

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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(long deploymentId) {
        this.deploymentId = deploymentId;
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getDgrmResourceName() {
        return dgrmResourceName;
    }

    public void setDgrmResourceName(String dgrmResourceName) {
        this.dgrmResourceName = dgrmResourceName;
    }

    @JsonIgnore
    public byte[] getBytes() {
        return bytes;
    }

    public String getBytesString() throws UnsupportedEncodingException {
        return bytes == null ? null : new String((byte[]) bytes, "utf-8");
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public void setBytesString(String bytes) throws UnsupportedEncodingException {
        this.bytes = bytes.getBytes("utf-8");
    }

    @Override
    public String toString() {
        return "ProcModel [id=" + id + ", name=" + name + ", version=" + version + ", deploymentId=" + deploymentId
                + ", sourceId=" + sourceId + ", resourceName=" + resourceName + ", dgrmResourceName=" + dgrmResourceName
                + "]";
    }
}
