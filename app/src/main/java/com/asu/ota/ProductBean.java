package com.asu.ota;

public class ProductBean
{
    private int id;

    private String name;

    private int score;

    private String desc;

    public ProductBean()
    {

    }

    public ProductBean(String name, String desc)
    {
        this.name = name;
        this.desc = desc;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getScore()
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }
}
