package com.kanhaichuan.confirm.addConfirmListener;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
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
            channel.addConfirmListener(new ConfirmListener() {
                //消息确认的回调方法
                //参数一:被确认的消息编号,从1开始
                //参数二:当前消息是否同时确认了多个
                @Override
                public void handleAck(long l, boolean b) throws IOException {
                    System.out.println("消息编号:"+l+"-----是否确认了多条:"+b);
                }

                //消息没有确认的回调方法
                //需要进行补发
                @Override
                public void handleNack(long l, boolean b) throws IOException {
                    System.out.println("消息发送失败");
                }
            });
            for(int i = 0;i<10000;i++){
                channel.basicPublish("confirmExchange","confirmRoutingKey",null,message.getBytes(StandardCharsets.UTF_8));
            }


            System.out.println("confirm消息发送成功" );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } /*finally {
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
        }*/


    }
}
