package com.liu.messageIsNotLost;

import com.liu.utils.RabitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * @author liushuaibiao
 * @date 2023/6/13 15:44
 */
public class Task2 {
    //队列名称
    public static final String TASK_QUEUE_NAME="ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabitMQUtils.getChannel();
        //开启发布确认
        channel.confirmSelect();

        //队列持久化
        boolean durable = true;
        channel.queueDeclare(TASK_QUEUE_NAME,durable,false,false,null);
//        为了演示工作线程可以轮询接收消息,这里发送消息从控制台接收
        Scanner scanner=new Scanner(System.in);
        while (scanner.hasNext()){
            String message=scanner.next();
            //设置消息持久化  MessageProperties.PERSISTENT_TEXT_PLAIN
            channel.basicPublish("",TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            System.out.println("发送消息完成:"+message);
        }
    }
}
