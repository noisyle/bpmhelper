package com.chinanx.springboot.model;

public class BizModel {
    private long id;
    private String name;
    private String json;

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

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return "BizModel [id=" + id + ", name=" + name + "]";
    }

}
