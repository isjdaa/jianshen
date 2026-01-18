package com.hello.entity;

import java.util.Date;

public class Member {
    private String id; // 对应旧的 sno
    private String password;
    private String name;
    private String tele;
    private Date joindate; // 入会时间
    private Integer age;
    private String gender;
    private String address;
    private String membershipType; // 会员类型：月卡/年卡/次卡等
    private Date expiryDate; // 到期时间
    private Double balance; // 预存余额
    private String trainerNo; // 教练编号/关联

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTele() { return tele; }
    public void setTele(String tele) { this.tele = tele; }

    public Date getJoindate() { return joindate; }
    public void setJoindate(Date joindate) { this.joindate = joindate; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String membershipType) { this.membershipType = membershipType; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }

    public String getTrainerNo() { return trainerNo; }
    public void setTrainerNo(String trainerNo) { this.trainerNo = trainerNo; }

    @Override
    public String toString() {
        return "Member{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", tele='" + tele + '\'' +
                ", joindate=" + joindate +
                ", membershipType='" + membershipType + '\'' +
                ", expiryDate=" + expiryDate +
                ", balance=" + balance +
                '}';
    }
}