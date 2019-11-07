package com.asu.ota.model;

public class ImageBean
{
    private int id;

    private String version;

    private String preVersion;

    private int dbid;

    private String desc;

    private String fileName;

    private String location;

    public ImageBean(String version)
    {
        this.version = version;
    }

    public ImageBean()
    {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getDbid() {
        return dbid;
    }

    public void setDbid(int dbid) {
        this.dbid = dbid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPreVersion() {
        return preVersion;
    }

    public void setPreVersion(String preVersion) {
        this.preVersion = preVersion;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
