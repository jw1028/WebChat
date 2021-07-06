package org.example.dao;

import org.example.exception.AppException;
import org.example.model.Channel;
import org.example.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChannelDAO {

    public static List<Channel> query() {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        //定义返回数据
        List<Channel> list = new ArrayList<>();
        try {
            //1 获取数据库连接Connection
            c = Util.getConnection();
            //2 通过Connection+sql创建操作命令对象Statement
            String sql = "select * from channel ";
            ps = c.prepareStatement(sql);
            //3 执行sql: 执行前替换占位符
            rs = ps.executeQuery();
            //4 如果是查询操作，处理结果集
            while (rs.next()){//移动到下一行，有数据返回true
                Channel channel = new Channel();
                //设置属性
                channel.setChannelId(rs.getInt("channelId"));
                channel.setChannelName(rs.getString("channelName"));
                list.add(channel);
            }
            return list;
        }catch (Exception e){
            throw new AppException("查询频道列表出错", e);
        }finally {
            //5 释放资源
            Util.close(c, ps, rs);
        }
    }

    public static Channel queryByName(String name) {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        //定义返回数据
        Channel channel = null;
        try {
            //1 获取数据库连接Connection
            c = Util.getConnection();
            //2 通过Connection+sql创建操作命令对象Statement
            String sql = "select * from channel where channelName=?";
            ps = c.prepareStatement(sql);
            //3 执行sql: 执行前替换占位符
            ps.setString(1, name);
            rs = ps.executeQuery();
            //4 如果是查询操作，处理结果集
            while (rs.next()){//移动到下一行，有数据返回true
                channel = new Channel();
                //设置结果集字段到用户对象的属性中
                channel.setChannelId(rs.getInt("channelId"));
                channel.setChannelName(rs.getString("channelName"));

            }
            return channel;
        }catch (Exception e){
            throw new AppException("查询频道出错", e);
        }finally {
            //5 释放资源
            Util.close(c, ps, rs);
        }
    }

    public static int addChannel(Channel channel) {
        Connection c = null;
        PreparedStatement ps = null;
        try{
            c = Util.getConnection();
            String sql = "insert into channel values(null,?)";
            ps = c.prepareStatement(sql);
            ps.setString(1, channel.getChannelName());
            return ps.executeUpdate();
        }catch (Exception e){
            throw new AppException("新增频道出错", e);
        }finally {
            Util.close(c, ps);
        }
    }

    public static int delChannel(Channel channel) {
        Connection c = null;
        PreparedStatement ps = null;
        try{
            c = Util.getConnection();
            String sql = "delete from channel where channelName =?";
            ps = c.prepareStatement(sql);
            ps.setString(1, channel.getChannelName());
            return ps.executeUpdate();
        }catch (Exception e){
            throw new AppException("删除频道出错", e);
        }finally {
            Util.close(c, ps);
        }
    }
}
