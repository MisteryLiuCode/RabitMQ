package com.liu.polling;

import com.liu.utils.RabitMQUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

/**
 * @author liushuaibiao
 * @date 2023/6/12 18:20
 * 第一个工作线程
 */
public class work01 {
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabitMQUtils.getChannel();
//        消息的接收
        /**
         * 接收消息的回调
         */
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            System.out.println("接收到的消息:"+new String(message.getBody()));
        };

        //取消消息的回调
        CancelCallback cancelCallback= consumerTag -> {
            System.out.println(consumerTag+"消息者取消消费接口逻辑");
        };
        //运行另一个线程的时候改为 C2
        System.out.println("C2等待接收消息......");
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}
