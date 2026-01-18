package com.hello.entity;

import java.util.Date;
public class Student {
    private Clazz clazz;//班级信息



    private String sno;//学号
    private String password;//密码
    private String name;//姓名
    private String tele;//电话
    private Date enterdate;//入学时间
    private Integer age;//年龄
    private String gender;//性别
    private String address;//详细地址
    private String clazzno;//班级


    public Clazz getClazz() {
        return clazz;
    }

    public void setClazz(Clazz clazz) {
        this.clazz = clazz;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTele() {
        return tele;
    }

    public void setTele(String tele) {
        this.tele = tele;
    }

    public Date getEnterdate() {
        return enterdate;
    }

    public void setEnterdate(Date enterdate) {
        this.enterdate = enterdate;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getClazzno() {
        return clazzno;
    }

    public void setClazzno(String clazzno) {
        this.clazzno = clazzno;
    }

    @Override
    public String toString() {
        return "Student{" +
                "sno='" + sno + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", tele='" + tele + '\'' +
                ", enterdate=" + enterdate +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", clazzno='" + clazzno + '\'' +
                '}';
    }
}
