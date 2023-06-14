package com.liu.messageIsNotLost;

import com.liu.utils.RabitMQUtils;
import com.liu.utils.SleepUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author liushuaibiao
 * @date 2023/6/13 15:59
 */
public class work03 {

    //队列名称
    public static final String TASK_QUEUE_NAME="ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabitMQUtils.getChannel();
        System.out.println("C1 等待1秒,模拟处理消息较快");
//        消息的接收
        /**
         * 接收消息的回调
         */
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            //沉睡 1 秒
            SleepUtils.sleep(1);
            System.out.println("接收到的消息:"+new String(message.getBody()));
            /**
             * 手动应答
             * 参数:1.消息标签,2.是否批量应答
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
        };

        //取消消息的回调
        CancelCallback cancelCallback= consumerTag -> {
            System.out.println(consumerTag+"消息者取消消费接口逻辑");
        };
        //运行另一个线程的时候改为 C2
        System.out.println("C1等待接收消息......");
        //设置不公平分发
        int prefetchCount=1;
        channel.basicQos(prefetchCount);

        channel.basicConsume(TASK_QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}
