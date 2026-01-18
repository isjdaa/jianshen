package com.hello.dao;
import com.hello.entity.Student;
import com.hello.utils.JdbcHelper;
import com.hello.utils.vo.PagerVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public PagerVO<Student> page(int current,int size,String whereSql){
        PagerVO<Student> pagerVO=new PagerVO<>();
        pagerVO.setCurrent(current);
        pagerVO.setSize(size);
        JdbcHelper helper=new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select  count(1) from tb_student"+whereSql);
        try{
            resultSet.next();
            int total =resultSet.getInt(1);
            pagerVO.setTotal(total);
            // selet  * from tb_student where .. limit 10,18
            resultSet=helper.executeQuery("select * from tb_student "+ whereSql +"limit "+((current-1)*size)+","+size);
            List<Student> list=new ArrayList<>();
            while(resultSet.next()){
                Student student=toEntity(resultSet);
                list.add(student);
            }
            pagerVO.setList(list);
            return  pagerVO;
        }catch (Exception e){
           e.printStackTrace();;
        }finally {
            helper.closeDB();;
        }
        return  pagerVO;
    }
    //封装入库sql
public int insert(Student student){
        JdbcHelper helper=new JdbcHelper();
        int res=helper.executeUpdate("insert into tb_student values(?,?,?,?,?,?,?,?,?)",
                student.getSno(),student.getPassword(),student.getName(),student.getTele(),
                student.getEnterdate(),student.getAge(),student.getGender(),student.getAddress(),student.getClazzno());
                helper.closeDB();
    return res;
}
/*student 里面有null属性的话就忽视，不是就加入更新的sql语句更新*/
public int update(Student student){
        JdbcHelper helper=new JdbcHelper();
        int res=0;
        String sql="update tb_student set";
        List<Object> params = new ArrayList<>();
        if(student.getPassword()!=null) {
            sql += "password=?,";
            params.add(student.getPassword());
        }
            if(student.getName()!=null) {
                sql += "name=?,";
                params.add(student.getName());
            }
            if(student.getTele()!=null) {
                sql += "tele=?,";
                params.add(student.getTele());
            }
            if(student.getEnterdate()!=null) {
                sql += "enterdate=?,";
                params.add(student.getEnterdate());
            }
            if(student.getAge( )!=null) {
                sql += "age=?,";
                params.add(student.getAge());
            }
            if(student.getGender()!=null) {
                sql += "gender=?,";
                params.add(student.getGender());
            }
            if(student.getAddress()!=null) {
                sql += "address=?,";
                params.add(student.getAddress());
            }
            if(student.getClazzno()!=null) {
                sql += "clazzno=?,";
                params.add(student.getClazzno());
            }
sql=sql.substring(0,sql.length()-1);
sql+="where sno ='"+student.getSno()+"'";
res=helper.executeUpdate(sql,params.toArray());
helper.closeDB();
return res;

}

public  int delete(String sno){
        JdbcHelper helper=new JdbcHelper();
        int res=helper.executeUpdate("delete from tb_student where sno= ?",sno);
        helper.closeDB();
        return res;
}
    public int count(String wheresql){
        if(wheresql == null){
            wheresql="";
        }
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_student"+wheresql );
        try {
            resultSet.next();
            return  resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            helper.closeDB();
        }
        return 0;}
        //重载
    public int count() {
        return  count("");
    }
        public Student getBySno(String sno){

        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select * from tb_student where sno = ?",sno);
        try {
           if(resultSet.next()){
               return toEntity(resultSet);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            helper.closeDB();
        }
        return null;
    }
public Student toEntity(ResultSet resultSet) throws SQLException {
    Student student = new Student();
    student.setSno(resultSet.getString("sno"));
    student.setPassword(resultSet.getString("password"));
    student.setName(resultSet.getString("name"));
    student.setTele(resultSet.getString("tele"));
    student.setEnterdate(resultSet.getDate("enterdate"));
    student.setAge(resultSet.getInt("age"));
    student.setGender(resultSet.getString("gender"));
    student.setAddress(resultSet.getString("address"));
    student.setClazzno(resultSet.getString("clazzno"));
    return student;
}



}

