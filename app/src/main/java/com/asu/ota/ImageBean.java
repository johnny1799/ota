package com.asu.ota;

public class ImageBean
{
    private int id;

    private String version;

    private int dbid;

    private String desc;

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
}
