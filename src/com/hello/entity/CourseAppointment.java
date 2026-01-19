package com.hello.entity;

import java.util.Date;

public class CourseAppointment {
    private String id;
    private String customerId;
    private String courseId;
    private Date appointmentTime;
    private String status;
    private Date createTime;
    
    // 关联对象
    private Customer customer;
    private Course course;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public Date getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(Date appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return "CourseAppointment{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", appointmentTime=" + appointmentTime +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", customer=" + customer +
                ", course=" + course +
                '}';
    }
}