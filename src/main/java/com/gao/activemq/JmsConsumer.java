package com.gao.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JmsConsumer {
    public static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    public static final String QUEUE_NAME = "queue01";

    public static void main(String[] args) throws Exception {

//        System.out.println("*** 我是1号消费者");
        System.out.println("*** 我是2号消费者");
        // 1 创建连接工厂 按照给定的URL地址 采用默认用户名和密码
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        // 2 通过连接工厂，获得连接connection, 并且启动访问
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();

        // 3 创建会话session
        // 两个参数 第一个叫事物/第二个叫签收
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // 4 创建目的地（队列还是主题）
        Queue queue = session.createQueue(QUEUE_NAME);

        // 5 创建消费者
        MessageConsumer messageConsumer = session.createConsumer(queue);

        /*
        // 第一种访问方式 同步阻塞方式（receive()）
        // 订阅者或者接受者 调用MessageConsumer的receive()方法来接收消息,receive方法能够在接收到消息之前（或者超时之前）将一直阻塞
        while (true) {
            TextMessage textMessage = (TextMessage) messageConsumer.receive(4000L);
            if(textMessage != null) {
                System.out.println("***消费者接收到消息：" + textMessage.getText());
            } else {
                break;
            }
        }
        // 顺着开 倒着关闭
        messageConsumer.close();
        session.close();
        connection.close();

         */

        // 通过监听的方式来监听消息 MessageConsumer messageConsumer = session.createConsumer(queue);
        // 异步非阻塞的方式监听(onMessage())
        // 订阅者或者接收者通过MessageConsumer的setMessageListener(MessageListener listener)注册一个消息监听器
        // 当消息到达之后 系统自动调用监听器 MessageListener的onMessage(Message message)的方法
        messageConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if(message != null && message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        System.out.println("***消费者接收到的消息：" + textMessage.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // 保持控制台可以持续使用
        // 一定要加这一句。要等连接上以后 才能监听到 要不然监听不上 就已经关闭了。
        System.in.read();
        messageConsumer.close();
        session.close();
        connection.close();

        /**
         * 1 先生产 启动1号消费者 1号消费者还能消费消息？ Y
         * 2 先生产 启动1号消费者 再启动2号消费者 2号消费者还能消费消息？ N 没有消息
         * 3 先启动2个消费者 生产6个消息 消费情况 一人一半 一人一个
         */
    }
}
