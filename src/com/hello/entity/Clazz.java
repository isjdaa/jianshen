package com.hello.entity;

public class Clazz {
    //不在数据库里
    private int  stuCount;//班级人数
    private String clazzno;//班级编号
    private String name;//班级名

    public int getStuCount() {
        return stuCount;
    }

    public void setStuCount(int stuCount) {
        this.stuCount = stuCount;
    }

    public String getClazzno() {
        return clazzno;
    }

    public void setClazzno(String clazzno) {
        this.clazzno = clazzno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

