package org.example.servlet;

import org.example.model.MessageCenter;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/message/{userId}")
public class MessageWebsocket {

    @OnOpen
    public void onOpen(@PathParam("userId") Integer userId,
                       Session session){
        //1.把每个客户端的session都保存起来，之后转发消息到所有客户端要用
        MessageCenter.addOnlineUser(userId, session);
        //2.查询本客户端（用户）上次登录前的消息（数据库查）
        System.out.println("建立连接："+userId);
    }

    @OnMessage
    public void onMessage(Session session,
                          String message){
        //1.遍历保存的所有session，每个都发送消息
        MessageCenter.sendMessage(message);
        //2.消息还要保存在数据库
        System.out.printf("接收到消息：%s", message);
    }

    @OnClose
    public void onClose(){
        //本客户端关闭连接，要在之前保存的session集合中，删除
        System.out.println("关闭连接");
    }

    @OnError
    public void onError(Throwable t){
        System.out.println("出错了");
        t.printStackTrace();
        //和关闭连接的操作一样
    }
}
