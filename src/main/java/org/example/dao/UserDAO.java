package org.example.dao;

import org.example.exception.AppException;
import org.example.model.User;
import org.example.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class UserDAO {

    public static User queryByName(String name) {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        //定义返回数据
        User u = null;
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
                u = new User();
                //设置结果集字段到用户对象的属性中
                u.setUserId(rs.getInt("userId"));
                u.setName(name);
                u.setPassword(rs.getString("password"));
                u.setNickName(rs.getString("nickName"));
                u.setIconPath(rs.getString("iconPath"));
                u.setSignature(rs.getString("signature"));
                java.sql.Timestamp lastLogout = rs.getTimestamp("lastLogout");
                u.setLastLogout(new Date(lastLogout.getTime()));
            }
            return u;
        }catch (Exception e){
            throw new AppException("查询用户账号出错", e);
        }finally {
            //5 释放资源
            Util.close(c, ps, rs);
        }
    }
}
