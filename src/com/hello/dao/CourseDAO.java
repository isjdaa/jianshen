package com.hello.dao;

import com.hello.entity.Course;
import com.hello.utils.JdbcHelper;
import com.hello.utils.vo.PagerVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    // 添加课程
    public int insert(Course course) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.executeUpdate(
                "insert into tb_course values(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                course.getId(), course.getCourseName(), course.getCoachId(),
                course.getCourseTime(), course.getDuration(),
                course.getMaxStudents(), course.getCurrentStudents(),
                course.getStatus(), course.getDescription()
        );
        helper.closeDB();
        return res;
    }

    // 更新课程
    public int update(Course course) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.executeUpdate(
                "update tb_course set course_name = ?, coach_id = ?, course_time = ?, duration = ?, max_students = ?, current_students = ?, status = ?, description = ? where id = ?",
                course.getCourseName(), course.getCoachId(), course.getCourseTime(),
                course.getDuration(), course.getMaxStudents(),
                course.getCurrentStudents(), course.getStatus(),
                course.getDescription(), course.getId()
        );
        helper.closeDB();
        return res;
    }

    // 更新课程当前人数（报名/取消）
    public int updateCurrentStudents(String courseId, int change) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.executeUpdate(
                "update tb_course set current_students = current_students + ? where id = ?",
                change, courseId
        );
        helper.closeDB();
        return res;
    }

    // 根据ID查询课程
    public Course getById(String id) {
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select * from tb_course where id = ?", id);
        try {
            if (resultSet.next()) {
                return toEntity(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return null;
    }

    // 查询所有课程（分页）
    public PagerVO<Course> findAll(int current, int size) {
        PagerVO<Course> pagerVO = new PagerVO<>();
        pagerVO.setCurrent(current);
        pagerVO.setSize(size);
        JdbcHelper helper = new JdbcHelper();
        
        // 查询总数
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_course");
        try {
            if (resultSet.next()) {
                pagerVO.setTotal(resultSet.getInt(1));
            }
            
            // 查询数据
            resultSet = helper.executeQuery(
                    "select * from tb_course order by course_time desc limit ?, ?",
                    (current - 1) * size, size
            );
            List<Course> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(toEntity(resultSet));
            }
            pagerVO.setList(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return pagerVO;
    }

    // 查询教练的课程
    public PagerVO<Course> findByCoachId(int current, int size, String coachId) {
        PagerVO<Course> pagerVO = new PagerVO<>();
        pagerVO.setCurrent(current);
        pagerVO.setSize(size);
        JdbcHelper helper = new JdbcHelper();
        
        // 查询总数
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_course where coach_id = ?", coachId);
        try {
            if (resultSet.next()) {
                pagerVO.setTotal(resultSet.getInt(1));
            }
            
            // 查询数据
            resultSet = helper.executeQuery(
                    "select * from tb_course where coach_id = ? order by course_time desc limit ?, ?",
                    coachId, (current - 1) * size, size
            );
            List<Course> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(toEntity(resultSet));
            }
            pagerVO.setList(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return pagerVO;
    }

    // 转换为实体对象
    private Course toEntity(ResultSet resultSet) throws SQLException {
        Course course = new Course();
        course.setId(resultSet.getString("id"));
        course.setCourseName(resultSet.getString("course_name"));
        course.setCoachId(resultSet.getString("coach_id"));
        course.setCourseTime(resultSet.getTimestamp("course_time"));
        course.setDuration(resultSet.getInt("duration"));
        course.setMaxStudents(resultSet.getInt("max_students"));
        course.setCurrentStudents(resultSet.getInt("current_students"));
        course.setStatus(resultSet.getString("status"));
        course.setDescription(resultSet.getString("description"));
        return course;
    }

    // 删除课程
    public int delete(String id) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.executeUpdate(
                "delete from tb_course where id = ?",
                id
        );
        helper.closeDB();
        return res;
    }
}