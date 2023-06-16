package com.liu.fanout;

import com.liu.utils.RabitMQUtils;
import com.liu.utils.SleepUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author liushuaibiao
 * @date 2023/6/15 15:32
 */
public class ReceiveLogs01 {
    //交换机的名称
    public static final String EXCHANGE_NAME="logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建一个队列
        Channel channel = RabitMQUtils.getChannel();
        //声明一个交换机 指定 fanout 模式
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        //声明一个队列 临时队列
        /**
         * 生成一个临时队列,队列的名称是随机的
         * 当消费者与队列断开链接时,队列就自动删除了
         */
        String queueName = channel.queueDeclare().getQueue();

        /**
         * 接收消息的回调
         */
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            System.out.println("接收到的消息:"+new String(message.getBody()));
        };
        /*
        把交换机和队列进行绑定
         */
        channel.queueBind(queueName,EXCHANGE_NAME,"qqq");
        System.out.println("等待接收消息,打印");
        channel.basicConsume(queueName,true,deliverCallback,cusumerTag->{});
    }
}
