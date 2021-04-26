package com.kanhaichuan.transaction;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Send {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.79.129");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root");

        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();//获取连接
            channel = connection.createChannel();//获取通道

            channel.queueDeclare("transactionQueue",true,false,false,null);

            channel.exchangeDeclare("transactionExchange","direct",true);

            channel.queueBind("transactionQueue","transactionExchange","transactionRoutingKey");

            String message = "我的测试transaction消息";

            channel.txSelect();


            channel.basicPublish("transactionExchange","transactionRoutingKey",null,message.getBytes(StandardCharsets.UTF_8));
            channel.txCommit();
            System.out.println("transaction消息发送成功");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {
            if(channel != null){
                try {
                    channel.txRollback();
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }

            }
            if(connection != null){
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
