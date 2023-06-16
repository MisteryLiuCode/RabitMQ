package com.liu.topic;

import com.liu.utils.RabitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author liushuaibiao
 * @date 2023/6/16 15:15
 * 声明主题交换机及相关队列
 */
public class ReceiveLogsTopic02 {
    //交换机的名称
    public static final String EXCHANGE_NAME="topic_logs";
    //接收消息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabitMQUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME,"topic");
        //声明队列
        String queueName="Q2";
        channel.queueDeclare(queueName,false,false,false,null);
        //将交换机和队列绑定
        channel.queueBind(queueName,EXCHANGE_NAME,"*.*.rabit");
        channel.queueBind(queueName,EXCHANGE_NAME,"lazy.#");
        System.out.println("等待接收消息...");
        /**
         * 接收消息的回调
         */
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            System.out.println("接收到的消息:"+new String(message.getBody()));
            System.out.println("接收队列:"+queueName+"绑定键:"+message.getEnvelope().getRoutingKey());
        };
        //接收消息
        channel.basicConsume(queueName,true,deliverCallback,consumerTag -> {});
    }
}
