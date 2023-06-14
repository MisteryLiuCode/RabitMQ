package com.liu;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author liushuaibiao
 * @date 2023/6/12 17:49
 * 消费者
 */
public class Consumer {
//    队列名称
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("mq.misteryliu.com");
        factory.setUsername("admin");
        factory.setPassword("aaaaaa");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        /**
         * 接收消息的回调
         */
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            System.out.println(new String(message.getBody()));
        };

        //取消消息的回调
        CancelCallback cancelCallback=consumerTag -> {
            System.out.println("消息消费被中断");
        };

        /**
         * 消费者消费消息
         * 消费哪个队列
         * 消费成功之后是否要自动应答  true 代表自动应答,false 代表手动应答
         */
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}
