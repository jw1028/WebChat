package org.example.dao;

import org.example.exception.AppException;
import org.example.model.Message;
import org.example.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    public static int insert(Message msg) {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = Util.getConnection();
            String sql = "insert into message values(null,?,?,?,?)";
            ps = c.prepareStatement(sql);
            ps.setInt(1, msg.getUserId());
            ps.setInt(2, msg.getChannelId());
            ps.setString(3, msg.getContent());
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

            return ps.executeUpdate();
        }catch (Exception e){
            throw new AppException("保存消息出错", e);
        }finally {
            //5 释放资源
            Util.close(c, ps, null);
        }
    }

    public static List<Message> queryByLastLogout(Integer userId) {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        //定义返回数据
        List<Message> list = new ArrayList<>();
        try {
            //1 获取数据库连接Connection
            c = Util.getConnection();
            //2 通过Connection+sql创建操作命令对象Statement
            String sql = "select m.*,u.nickName from message m join user u " +
                    "on u.userId=m.userId where m.sendTime>(select lastLogout from user where userId =?)";
            ps = c.prepareStatement(sql);
            //3 执行sql: 执行前替换占位符
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            //4 如果是查询操作，处理结果集
            while (rs.next()){//移动到下一行，有数据返回true
                Message m = new Message();
                m.setUserId(userId);
                m.setNickName(rs.getString("nickName"));
                m.setChannelId(rs.getInt("channelID"));
                list.add(m);
            }
            return list;
        }catch (Exception e){
            throw new AppException("查询用户"+ userId + "出错",  e);
        }finally {
            //5 释放资源
            Util.close(c, ps, rs);
        }
    }
}
