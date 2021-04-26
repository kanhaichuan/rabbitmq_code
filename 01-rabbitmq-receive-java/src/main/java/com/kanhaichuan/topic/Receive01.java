package com.kanhaichuan.topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Receive01 {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.79.129");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root");

        Connection connection =null;
        Channel channel =null;

        try {
            connection=connectionFactory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare("myTopicQueue02",true,false,false,null);
            channel.exchangeDeclare("topicExchange","topic",true);
            channel.queueBind("myTopicQueue02","topicExchange","aa.*");

            channel.basicConsume("myTopicQueue02",true,"",new DefaultConsumer(channel){
                //消息的具体接收和处理方法
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body,"utf-8");
                    System.out.println("消费者aa.*:"+message);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
