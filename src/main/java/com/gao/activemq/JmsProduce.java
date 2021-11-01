package com.gao.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JmsProduce {
    public static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    public static final String QUEUE_NAME = "queue01";

    public static void main(String[] args) throws JMSException {
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

        // 5 创建消息的生产者
        MessageProducer messageProducer = session.createProducer(queue);
        // 6 通过使用messageProducer生产三条消息发送到MQ的队列厘米
        for(int i = 0; i < 3; i++) {
            // 7 创建消息
//            TextMessage textMessage = session.createTextMessage("msg---" + i);//理解为一个字符串
            TextMessage textMessage = session.createTextMessage("msgListener---" + i);//理解为一个字符串
            // 8 通过messageProducer 发送给mq
            messageProducer.send(textMessage);
        }
        // 9 关闭资源
        messageProducer.close();
        session.close();
        connection.close();

        System.out.println("*** 消息发送到MQ完成");
    }
}
