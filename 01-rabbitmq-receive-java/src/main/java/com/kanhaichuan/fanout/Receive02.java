package com.kanhaichuan.fanout;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Receive02 {
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


            channel.queueDeclare("fanoutQueue",true,false,false,null);

            channel.exchangeDeclare("fanoutExchange","fanout",true);
            channel.queueBind("fanoutQueue","fanoutExchange","");

            /**
             * 参数一:消费者监听的队列名称,队列名要和发送时的队列名完全一致,否则接收不到消息
             * 参数二:消息自动确认机制,true表示自动确认,接受完消息自动删除队列的消息
             * 参数三:消息接收者的标签,当多个消费者同时监听同一个队列时用于确认不同消费者,通常为空字符串即可
             * 参数四:消息接收的回调方法,对消息的处理
             *
             * 注意:使用了basicConsume以后,会启动一个线程,会持续的监听消息,因此不能关闭连接和通道对象
             */
            channel.basicConsume("fanoutQueue",true,"",new DefaultConsumer(channel){
                //消息的具体接收和处理方法
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body,"utf-8");
                    System.out.println("receive02:"+message);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
