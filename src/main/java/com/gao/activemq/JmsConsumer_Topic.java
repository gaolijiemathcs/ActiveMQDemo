package com.gao.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JmsConsumer_Topic {
    public static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    public static final String TOPIC_NAME = "topic-01";

    public static void main(String[] args) throws Exception {

//        System.out.println("*** 我是1号消费者");
        System.out.println("*** 我是3号消费者");
        // 1 创建连接工厂 按照给定的URL地址 采用默认用户名和密码
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        // 2 通过连接工厂，获得连接connection, 并且启动访问
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();

        // 3 创建会话session
        // 两个参数 第一个叫事物/第二个叫签收
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // 4 创建目的地（队列还是主题）
        Topic topic = session.createTopic(TOPIC_NAME);

        // 5 创建消费者
        MessageConsumer messageConsumer = session.createConsumer(topic);

        // 通过监听器来消费消息
        messageConsumer.setMessageListener((message) -> {
          if(message != null && message instanceof TextMessage) {
              TextMessage textMessage = (TextMessage) message;
              try {
                  System.out.println("***消费者接收到的消息：" + textMessage.getText());
              } catch (JMSException e) {
                  e.printStackTrace();
              }
          }
        });
        // 保持控制台可以持续使用
        // 一定要加这一句。要等连接上以后 才能监听到 要不然监听不上 就已经关闭了。
        System.in.read();
        messageConsumer.close();
        session.close();
        connection.close();
    }
}
