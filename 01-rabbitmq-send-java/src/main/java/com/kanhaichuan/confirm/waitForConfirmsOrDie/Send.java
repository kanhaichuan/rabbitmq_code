package com.kanhaichuan.confirm.waitForConfirmsOrDie;

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
            /**
             * 批量消息确认,当前通道中的消息是否全部成功写入
             * 这个方法没有返回值,如果有一条消息没有成功或向服务器发送确认时,服务器不可访问都会被认为消息确认失败
             * 可能有消息没有发送成功,我们需要进行消息的补发策略
             * 如果无法向服务器获取确认信息,当前方法就会抛出InterruptedException异常,这时就需要补发到队列
             * waitForConfirmsOrDie可以指定一个参数timeout用于等待服务器的确认时间,超时也会抛出异常
             *
             * 注意:批量消息确认的速度比普通的确认要快,但是一旦出现消息需要补发的情况,我们不能确定具体是哪条消息没有完成发送
             * 需要将本次发送的所有消息全部进行补发
             */
            channel.waitForConfirmsOrDie();
            System.out.println("confirm消息发送成功" );
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
