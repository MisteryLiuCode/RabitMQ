package com.liu.deadLetter;

import com.liu.utils.RabitMQUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author liushuaibiao
 * @date 2023/6/16 17:39
 */
public class Consumer01 {

    //普通交换机的名称
    public static final String NORMAL_EXCHANGE="normal_exchange";
    //死信交换机名称
    public static final String DEAD_EXCHANGE="dead_exchange";
    //普通队列名称
    public static final  String NORMAL_QUEUE="normal_queue";
    //死信队列
    public static final  String DEAD_QUEUE="dead_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabitMQUtils.getChannel();
        //声明一个交换机 指定 direct 模式
        channel.exchangeDeclare(NORMAL_EXCHANGE,"direct");
        channel.exchangeDeclare(DEAD_EXCHANGE,"direct");
        //声明死信队列和普通队列
        Map<String, Object> arguments=new HashMap<>();
        //死信交换机
        arguments.put("x-dead-letter-exchange",DEAD_EXCHANGE);
        //死信路由 key
        arguments.put("x-dead-letter-routing-key","lisi");
        //可以在生产端设置消息过期时间
//        arguments.put("x-message-ttl",100000);
        channel.queueDeclare(NORMAL_QUEUE,false,false,false,arguments);
        channel.queueDeclare(DEAD_QUEUE,false,false,false,null);
        //将交换机和队列绑定起来,并在交换机和队列之间设置 routeKey
        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"zhangsan");
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"lisi");
    }
}
