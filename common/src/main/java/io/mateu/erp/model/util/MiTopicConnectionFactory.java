package io.mateu.erp.model.util;

import com.rabbitmq.jms.admin.RMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;

public class MiTopicConnectionFactory implements TopicConnectionFactory {

    private static RMQConnectionFactory f = jmsConnectionFactory();

    private static RMQConnectionFactory jmsConnectionFactory() {
        RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
        connectionFactory.setUsername("tester");
        connectionFactory.setPassword("tester8912");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setHost("quon.mateu.io");
        return connectionFactory;
    }

    @Override
    public TopicConnection createTopicConnection() throws JMSException {
        return f.createTopicConnection();
    }

    @Override
    public TopicConnection createTopicConnection(String s, String s1) throws JMSException {
        return f.createTopicConnection(s, s1);
    }

    @Override
    public Connection createConnection() throws JMSException {
        return f.createConnection();
    }

    @Override
    public Connection createConnection(String s, String s1) throws JMSException {
        return f.createConnection(s, s1);
    }
}
