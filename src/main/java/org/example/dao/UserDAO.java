package org.example.dao;

import org.example.exception.AppException;
import org.example.model.User;
import org.example.util.Util;

import java.sql.*;
import java.util.Date;

public class UserDAO {

    /**
     *添加用户
     */
    public void addUser(User u) {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = Util.getConnection();
            String sql = "insert into user values(null, ?, ?, ?, '', ?, now())";
            ps = c.prepareStatement(sql);
            ps.setString(1, u.getName());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNickName());
            ps.setString(4, u.getSignature());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            Util.close(c, ps);
        }
    }

    /**
     *根据账号查询用户
     */
    public static User queryByName(String name) {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        //定义返回数据
        User user = null;
        try {
            //1 获取数据库连接Connection
            c = Util.getConnection();
            //2 通过Connection+sql创建操作命令对象Statement
            String sql = "select * from user where name=?";
            ps = c.prepareStatement(sql);
            //3 执行sql: 执行前替换占位符
            ps.setString(1, name);
            rs = ps.executeQuery();
            //4 如果是查询操作，处理结果集
            while (rs.next()){//移动到下一行，有数据返回true
                user = new User();
                //设置结果集字段到用户对象的属性中
                user.setUserId(rs.getInt("userId"));
                user.setName(name);
                user.setPassword(rs.getString("password"));
                user.setNickName(rs.getString("nickName"));
                user.setIconPath(rs.getString("iconPath"));
                user.setSignature(rs.getString("signature"));
                Timestamp lastLogout = rs.getTimestamp("lastLogout");
                user.setLastLogout(new Date(lastLogout.getTime()));
            }
            return user;
        }catch (Exception e){
            throw new AppException("查询用户账号出错", e);
        }finally {
            //5 释放资源
            Util.close(c, ps, rs);
        }
    }

    public static int updateLastLogout(Integer userId) {
        Connection c = null;
        PreparedStatement ps = null;
        try{
            c = Util.getConnection();
            String sql = "update user set lastLogout=? where userId=?";
            ps = c.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, userId);
            return ps.executeUpdate();
        }catch (Exception e){
            throw new AppException("修改用户上次登录时间出错", e);
        }finally {
            Util.close(c, ps);
        }
    }
}
