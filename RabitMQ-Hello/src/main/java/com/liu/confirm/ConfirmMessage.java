package com.liu.confirm;

import com.liu.utils.RabitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

/**
 * @author liushuaibiao
 * @date 2023/6/14 14:28
 * 发布确认模式
 * 比较使用的时间
 * 单个确认
 * 批量确认
 * 异步确认
 */
public class ConfirmMessage {
    //三个方法,单个确认,批量确认,异步批量确认

    //发送消息的个数
    public static final int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception {
        //单个确认发布消息
//        ConfirmMessage.publishMessageSingleOne();
        //批量确认发布消息
//        ConfirmMessage.publishMessageBatch();
        //异步确认消息
        publishMessageAsync();
    }

    //    单个确认
    public static void publishMessageSingleOne() throws IOException, TimeoutException, InterruptedException {
        Channel channel = RabitMQUtils.getChannel();
        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        //记录开始的时间
        long start = System.currentTimeMillis();

        //批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            //单个消息就马上发布确认
            boolean flag = channel.waitForConfirms();
            if (flag) {
                System.out.println("消息发送成功");
            }
        }
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个单独确认消息,耗时" + (end - start) + "ms");
    }

    //    批量发布确认
    public static void publishMessageBatch() throws IOException, TimeoutException, InterruptedException {
        Channel channel = RabitMQUtils.getChannel();
        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        //记录开始的时间
        long start = System.currentTimeMillis();

        //批量确认消息大小
        int batch = 100;
        //批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            //批量确认
            if (i % 100 == 0) {
                //单个消息就马上发布确认
                boolean flag = channel.waitForConfirms();
                if (flag) {
                    System.out.println("批量确认消息成功");
                }
            }
        }
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个批量确认消息,耗时" + (end - start) + "ms");
    }

    //异步发布确认
    public static void publishMessageAsync() throws Exception {

        Channel channel = RabitMQUtils.getChannel();
        //开启发布确认模式
        channel.confirmSelect();

        /**
         * 准备线程安全有序的一个哈希表Map，适用于高并发情况下;
         * 1、轻松的将序号与消息进行关联;
         * 2、轻松的批量删除消息条目（只要给出序号）；
         * 3、支持高并发（多线程）；
         */
        ConcurrentSkipListMap<Long, String> outstandingConfirms =
                new ConcurrentSkipListMap<>();

        //队列的声明
        String queueName = UUID.randomUUID().toString();

        channel.queueDeclare(queueName, true, false, false, null);

        //开始时间
        long beginTime = System.currentTimeMillis();


        //消息确认成功，回调函数
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            if (multiple) {
                //2.删除掉已经确认的消息，剩下的就是未确认的消息
                ConcurrentNavigableMap<Long, String> confirmed
                        = outstandingConfirms.headMap(deliveryTag);
                confirmed.clear();
            } else {
                outstandingConfirms.remove(deliveryTag);
            }
            System.out.println("确认的消息：" + deliveryTag);
        };

        //消息确认失败，回调函数
        /**
         * deliveryTag: 消息的标记；
         * multiple： 是否为批量确认
         */
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {

            String message = outstandingConfirms.get(deliveryTag);
            System.out.println("未确认的消息是：" + message + "，未确认的消息标识是：" + deliveryTag);
        };

        //准备消息的监听器：监听消息哪些消息发送成功，哪些消息发送失败？
        /**
         * 1、监听那些消息发送成功
         * 2、监听哪些消息发送事变
         */
        channel.addConfirmListener(ackCallback, nackCallback); //异步通知

        //批量发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = "消息" + i;
            channel.basicPublish("", queueName, null, message.getBytes());

            //1、在此处记录下所有要发送的消息,key为发送消息的序号
            outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
        }

        //结束时间
        long endTime = System.currentTimeMillis();
        System.out.println("发布：" + MESSAGE_COUNT + "个异步发布确认消息，耗时：" + (endTime - beginTime) + "ms");
    }

}
