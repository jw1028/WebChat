package org.example.model;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 保存websocket需要的信息：
 * 所有客户端session
 * 多个客户端要保证线程安全
 */
public class MessageCenter {

    /**
     * HashMap不安全
     * 支持线程安全的map结构，并且满足高并发（读写，读读并发，写写互斥的，加锁粒度）
     * <userID, session> userId唯一对应一个session
     */
    private static final ConcurrentHashMap<Integer, Session> clientsMap = new ConcurrentHashMap<>();

    /**
     * 阻塞队列：用来存放消息，接收的客户端消息就放在里边（放进去是很快）
     * 再启动一个线程，不停的拉取队列中的消息，发送（发送和接收并发并行执行，分离）
     */
    private static BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    private static MessageCenter center;

    //单例的Message对象
    private MessageCenter(){}
    public static MessageCenter getInstance(){
        if(center == null){//单例模式，以后可以改造为双重校验锁的单例模式
            center = new MessageCenter();
            new Thread(()->{//启动一个线程，不停的从阻塞队列拿数据
                while(true) {
                    try {
                        String message = messageQueue.take();//获取数据，如果队列为空，阻塞等待
                        sendMessage(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        return center;
    }
    /**
     * 不直接发消息，先把消息存放在队列中，由另一个线程去发
     */
    public static void addMessage(String message){
        messageQueue.add(message);
    }

    /**
     * websocket建立连接时，添加用户id和客户端session，保存起来
     */
    public static void addOnlineUser(Integer userId, Session session){

        clientsMap.put(userId, session);
    }
    /**
     * 关闭websocket连接，和出错时，删除客户端session
     */
    public static void delOnlineUser(Integer userId){

        clientsMap.remove(userId);
    }

    /**
     * 接收到某用户的消息时，转发到所有客户端:
     * 等待WebSocket中onMessage回调方法执行完，性能差
     * TODO: 优化（使用阻塞队列的方式解决：并行并发的执行任务提交和任务执行）
     */
    public static void sendMessage(String message){
        try {
            //遍历客户端中保存的session并一次发送
            Enumeration<Session> e = clientsMap.elements();
            while(e.hasMoreElements()){
                Session session = e.nextElement();
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
