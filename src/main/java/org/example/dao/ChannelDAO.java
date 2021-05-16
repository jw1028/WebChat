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
            String sql = "select * from channel";
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
}
