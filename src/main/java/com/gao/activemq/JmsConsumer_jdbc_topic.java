package com.gao.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class JmsConsumer_jdbc_topic {
    public static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    public static final String TOPIC_NAME = "topic-jdbc-PERSISTENT";

    public static void main(String[] args) throws Exception {
        // 创建连接工厂 使用默认的用户名密码 编码不体现
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        // 2 获得连接并且启动
        Connection connection = activeMQConnectionFactory.createConnection();
        // 2.1 需要再连接上设置消费者id 用于识别消费者
        connection.setClientID("gao01");
        // 3 创建绘画，此步骤有两个参数 一个是否以事务的方式提交 第二个默认的签收方式
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // 4 创建主题
        Topic topic = session.createTopic(TOPIC_NAME);
        // 4.1 创建topicSubscriber订阅
        TopicSubscriber topicSubscriber = session.createDurableSubscriber(topic, "mq-jdbc");
        // 4.1 一定先运行依次 等于向消息服务中间件注册这个消费者 然后再运行客户端发送信息 这个时候
        // 无论消费者是否在线 都会接收到 不在线的化 下次连接的时候 会把没有收过的消息都接收写来
        connection.start();
        // 接收消息
        Message message = topicSubscriber.receive();
        while(true) {
            TextMessage textMessage = (TextMessage) topicSubscriber.receive(4000L);
            if(null != textMessage) {
                System.out.println("********消费者接受到消息:" + textMessage.getText());
            } else {
                break;
            }
        }
        topicSubscriber.close();
        session.close();
        connection.close();
    }
}
