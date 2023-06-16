package com.liu.direct;

import com.liu.utils.RabitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author liushuaibiao
 * @date 2023/6/15 15:32
 */
public class ReceiveDirect02 {
    //交换机的名称
    //交换机的名称
    public static final String EXCHANGE_NAME="direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建一个队列
        Channel channel = RabitMQUtils.getChannel();
        //声明一个交换机 指定 direct 模式
        channel.exchangeDeclare(EXCHANGE_NAME,"direct");
        //声明一个队列
        channel.queueDeclare("disk",false,false,false,null);
        //将交换机和队列绑定起来,并在交换机和队列之间设置 routeKey
        channel.queueBind("disk",EXCHANGE_NAME,"error");

        /**
         * 接收消息的回调
         */
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            System.out.println("接收到的消息:"+new String(message.getBody()));
        };
        channel.basicConsume("disk",deliverCallback,consumerTag -> {});
    }
}
