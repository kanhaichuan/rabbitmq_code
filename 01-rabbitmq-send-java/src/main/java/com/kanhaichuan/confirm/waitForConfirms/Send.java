package com.kanhaichuan.confirm.waitForConfirms;

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

            channel.queueDeclare("confirmQueue",true,false,false,null);
            channel.exchangeDeclare("confirmExchange","direct",true);
            channel.queueBind("confirmQueue","confirmExchange","confirmRoutingKey");

            String message = "我的测试confirm消息";
            channel.confirmSelect();
            channel.basicPublish("confirmExchange","confirmRoutingKey",null,message.getBytes(StandardCharsets.UTF_8));
            boolean flag = channel.waitForConfirms();
            System.out.println("confirm消息发送成功" + flag);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(channel != null){
                try {
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
