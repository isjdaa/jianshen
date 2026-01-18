package com.hello.dao;

import com.hello.entity.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    private Connection connection;

    public MemberDAO(Connection connection) {
        this.connection = connection;
    }

    public Member getById(String id) throws SQLException {
        String sql = "SELECT * FROM tb_member WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapToMember(rs);
            } 
        }
        return null;
    }

    public void insert(Member member) throws SQLException {
        String sql = "INSERT INTO tb_member (name, ...) VALUES (?, ...)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            // set other parameters
            ps.executeUpdate();
        }
    }

    public void update(Member member) throws SQLException {
        String sql = "UPDATE tb_member SET name = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getId());
            ps.executeUpdate();
        }
    }

    public List<Member> page(int pageIndex, int pageSize) throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM tb_member LIMIT ?, ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, pageIndex * pageSize);
            ps.setInt(2, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                members.add(mapToMember(rs));
            }
        }
        return members;
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM tb_member WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    private Member mapToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getString("id"));
        member.setName(rs.getString("name"));
        // map other fields
        return member;
    }
}
