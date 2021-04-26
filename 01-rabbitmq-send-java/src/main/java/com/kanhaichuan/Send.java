package com.kanhaichuan;

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
            /**
             * 声明一个队列
             * 参数一:队列-->任意
             * 参数二:是否持久化
             * 参数三:是否排外,如果排外则只允许一个消费者监听
             * 参数四:是否自动删除队列,true-->队列中没有消息,没有消费者连接就会自动将队列删除
             * 参数五:队列属性设置通常为null即可
             */
            channel.queueDeclare("myQueue",true,false,false,null);
            String message = "我的测试rabbitmq消息";

            /**
             * 参数一:交换机名称
             * 参数二:队列名或routingkey,当指定了交换机之后,这个值就是routingkey
             * 参数三:消息的属性信息,通常为null
             * 参数四:消息字节数组
             */
            channel.basicPublish("","myQueue",null,message.getBytes(StandardCharsets.UTF_8));
            System.out.println("消息发送成功");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {
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
