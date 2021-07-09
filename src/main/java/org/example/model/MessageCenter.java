package org.example.model;
package org.example.model;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.*;

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

    private static volatile MessageCenter messageCenter;

    //单例的Message对象
    private MessageCenter(){}
    //双重校验锁的单例模式
    public static MessageCenter getInstance(){
        if(messageCenter == null){
            synchronized(MessageCenter.class) {
                if(messageCenter == null) {
                    messageCenter = new MessageCenter();
                }
            }
        }
        return messageCenter;
    }
    /**
     * 不直接发消息，先把消息存放在队列中，由另一个线程去发
     */
    public  void addMessage(String message){
        messageQueue.add(message);
    }

    /**
     * websocket建立连接时，添加用户id和客户端session，保存起来
     */
    public  void addOnlineUser(Integer userId, Session session){
        clientsMap.put(userId, session);
    }
    /**
     * 关闭websocket连接，和出错时，删除客户端session
     */
    public  void delOnlineUser(Integer userId){
        clientsMap.remove(userId);
    }

    /**
     * 接收到某用户的消息时，转发到所有客户端:
     * 等待WebSocket中onMessage回调方法执行完，性能差
     */
    public  void sendMessage(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                10,100,60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadPoolExecutor.DiscardPolicy()
        );
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取数据，如果队列为空，阻塞等待
                    String message = messageQueue.take();
                    //遍历客户端中保存的session并一次发送(获取clientMap所有的value）
                    Enumeration<Session> enumeration = clientsMap.elements();
                    while(enumeration.hasMoreElements()){
                        Session session = enumeration.nextElement();
                        session.getBasicRemote().sendText(message);
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}

