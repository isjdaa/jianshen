package com.hello.service;

import com.hello.dao.StudentDAO;
import com.hello.entity.Student;
import com.hello.utils.vo.PagerVO;

public class StudentService {

    StudentDAO dao = new StudentDAO();



    public String insert(Student student) {
        if(student.getSno() == null || student.getSno().equals("")){
            return  "学生学号不可为空";
        }if(student.getPassword()==null|| student.getPassword().equals("")){
            return "密码不可为空";
        } if(student.getName()==null|| student.getName().equals("")){
            return "学生姓名不可为空";
        }
        if(student.getClazzno()==null|| student.getClazzno().equals("")) {
            return "学生班级不可为空";
        }
        Student ex=dao.getBySno(student.getSno());
        if(ex!=null){
            return "学号已存在！";
        }
        dao.insert(student);
        return null;
    }
    public String update(Student student){
        if(student.getSno()==null || student.getSno().equals("")){
            return "被修改的学生学号不可为空";
        }
        dao.update(student);
        return null;//成功
    }


    public Student getBySno(String sno) {

        return dao.getBySno(sno);
    }
    public  int count(){
        return dao.count();
    }



    public PagerVO<Student> page(int current,int size,String sno,String name,String gender,String clazzno){
        String whereSql=" where 1=1 ";
        //拼接
        if(sno!=null  && !"".equals(sno)){
            whereSql +=" and sno like '%"+sno+"%'";
        }
        if(name!=null  && !"".equals(name)){
            whereSql +=" and name like '%"+name+"%'";
        }
        if(gender!=null  && !"".equals(gender)){
            whereSql +=" and gender = '"+gender+"'";
        }
        if(clazzno!=null  && !"".equals(clazzno)){
            whereSql +=" and clazzno  ='"+clazzno+"'";
        }
        return  dao.page(current,size,whereSql);
    }

    public  int delete(String sno){
        return dao.delete(sno);
    }
}