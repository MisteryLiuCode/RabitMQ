package com.liu.polling;

import com.liu.utils.RabitMQUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * @author liushuaibiao
 * @date 2023/6/13 14:22
 */
public class Task01 {
    //队列名称
    public static final String QUEUE_NAME="hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabitMQUtils.getChannel();
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
//        为了演示工作线程可以轮询接收消息,这里发送消息从控制台接收
        Scanner scanner=new Scanner(System.in);
        while (scanner.hasNext()){
            String message=scanner.next();
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            System.out.println("发送消息完成"+message);
        }
    }
}
