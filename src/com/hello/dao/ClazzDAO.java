package com.hello.dao;

import com.hello.entity.Clazz;
import com.hello.utils.JdbcHelper;
import com.hello.utils.vo.PagerVO;
import com.sun.jmx.snmp.SnmpUnknownAccContrModelException;
import javafx.beans.binding.When;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClazzDAO {

    public PagerVO<Clazz> page(int current, int size, String whereSql){
        PagerVO<Clazz> pagerVO = new PagerVO<>();
        pagerVO.setCurrent(current);
        pagerVO.setSize(size);
        JdbcHelper helper = new JdbcHelper();
        if(whereSql == null){
            whereSql = " ";
        }
        ResultSet resultSet;
        try {
            resultSet = helper.executeQuery("select count(1) from tb_clazz "+whereSql);
            resultSet.next();
            int total = resultSet.getInt(1);
            pagerVO.setTotal(total);

            resultSet = helper.executeQuery("select * from tb_clazz " + whereSql + " limit " + ((current-1)*size) + "," + size);
            List<Clazz> list = new ArrayList<>();
            while (resultSet.next()){
                Clazz e = new Clazz();
                e.setClazzno(resultSet.getString("clazzno"));
                e.setName(resultSet.getString("name"));
                list.add(e);
            }
            pagerVO.setList(list);
            return pagerVO;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            helper.closeDB();
        }
        return null;
    }

    public int insert(Clazz clazz){
        JdbcHelper helper = new JdbcHelper();
        int res = helper.executeUpdate("insert into tb_clazz values(?,?)",clazz.getClazzno(),clazz.getName());
        helper.closeDB();
        return res;
    }

    public int update(Clazz clazz){
        JdbcHelper helper = new JdbcHelper();
        int res = 0;
        //为null的属性不做更新
        String sql = "update tb_clazz set name = ? where clazzno = ? ";
        res = helper.executeUpdate(sql,clazz.getName(),clazz.getClazzno());
        helper.closeDB();
        return res;
    }

    public Clazz getByClazzno(String clazzno){
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select * from tb_clazz where clazzno = ?",clazzno);
        try {
            if(resultSet.next()){
                Clazz clazz = new Clazz();
                clazz.setName(resultSet.getString("name"));
                clazz.setClazzno(resultSet.getString("clazzno"));
                return clazz;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            helper.closeDB();
        }
        return null;
    }




    public  int delete(String clazzno){
        JdbcHelper helper=new JdbcHelper();
        int res=helper.executeUpdate("delete from tb_clazz where clazzno= ?",clazzno);
        helper.closeDB();
        return res;
    }


    public int count() {
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_clazz");
        try {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            helper.closeDB();
        }
        return 0;
    }
    //班级学生数量统计
    public List<Clazz> statistics(){
String sql="SELECT c.clazzno,c.name,COUNT(1) as stuCount\n" +
        "  from tb_clazz c,tb_student s\n" +
        "WHERE s.clazzno=c.clazzno\n" +
        "GROUP BY c.clazzno,c.`name`";
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery(sql);
     try{
         List<Clazz> list=new ArrayList<>();
         while (resultSet.next()){
        Clazz clazz=new Clazz();
        clazz.setClazzno(resultSet.getString("clazzno"));
        clazz.setName(resultSet.getString("name"));
        clazz.setStuCount(Integer.parseInt(resultSet.getString("stuCount")));
        list.add(clazz);
         }
         return list;
     }catch(Exception e){
         e.printStackTrace();
     }finally {
         helper.closeDB();
     }
     return null;
    }


    public List<Clazz> listAll() {

        JdbcHelper helper = new JdbcHelper();

        ResultSet resultSet;
        try {


            resultSet = helper.executeQuery("select * from tb_clazz ");
            List<Clazz> list = new ArrayList<>();
            while (resultSet.next()){
                Clazz e = new Clazz();
                e.setClazzno(resultSet.getString("clazzno"));
                e.setName(resultSet.getString("name"));
                list.add(e);
            }
            return list;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            helper.closeDB();
        }
        return null;
    }
    }
