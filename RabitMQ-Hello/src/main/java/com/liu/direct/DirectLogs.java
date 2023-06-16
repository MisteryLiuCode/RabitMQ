package com.liu.direct;

import com.liu.utils.RabitMQUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * @author liushuaibiao
 * @date 2023/6/15 17:16
 */
public class DirectLogs {
    public static final String EXCHANGE_NAME="direct_logs";
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabitMQUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME,"direct");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message = scanner.next();
            //指定不同的 routeKey 可以路由到不同的队列
            channel.basicPublish(EXCHANGE_NAME,"warning",null,message.getBytes());
            System.out.println("生产者发出消息:"+message);
        }
    }
}
