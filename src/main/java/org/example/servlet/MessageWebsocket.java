package org.example.servlet;

import org.example.dao.MessageDAO;
import org.example.dao.UserDAO;
import org.example.model.Message;
import org.example.model.MessageCenter;
import org.example.util.Util;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@ServerEndpoint("/message/{userId}")
public class MessageWebsocket {

    @OnOpen
    public void onOpen(@PathParam("userId") Integer userId,
                       Session session) throws IOException {
        //1.把每个客户端的session都保存起来，之后转发消息到所有客户端要用
        MessageCenter.addOnlineUser(userId, session);
        //2.查询本客户端（用户）上次登录前的消息（数据库查）
         List<Message> list = MessageDAO.queryByLastLogout(userId);
        //3.发送当前用户在上次登录后的消息
        for(Message m : list) {
            session.getBasicRemote().sendText(Util.serialize(m));
        }
        System.out.println("建立连接："+userId);
    }

    @OnMessage
    public void onMessage(Session session,
                          String message){
        //1.遍历保存的所有session，每个都发送消息
        MessageCenter.sendMessage(message);
        //2.消息还要保存在数据库
        // 反序列化json字符串为message字符串
        Message msg = Util.deserialize(message, Message.class);
        //message设置接收消息的时间
        msg.setSendTime(new Date());
        //插入数据库
        int n = MessageDAO.insert(msg);
        System.out.printf("接收到消息：%s\n", message);
    }

    @OnClose
    public void onClose(@PathParam("userId") Integer userId){
        //1.本客户端关闭连接，要在之前保存的session集合中，删除
        MessageCenter.delOnlineUser(userId);
        //2.建立连接
        int n = UserDAO.updateLastLogout(userId);
        System.out.println("关闭连接");
    }

    @OnError
    //出了异常才会执行
    public void onError(Throwable t){
        System.out.println("出错了");
        t.printStackTrace();
        //和关闭连接的操作一样
    }
}
