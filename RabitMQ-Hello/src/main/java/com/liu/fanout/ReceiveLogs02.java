package com.liu.fanout;

import com.liu.utils.RabitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author liushuaibiao
 * @date 2023/6/15 15:32
 */
public class ReceiveLogs02 {
    //交换机的名称
    public static final String EXCHANGE_NAME="logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建一个队列
        Channel channel = RabitMQUtils.getChannel();
        //声明一个交换机
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

            String msg = new String(message.getBody(), "UTF-8");
                File file = new File("/Users/liushuaibiao/Downloads/同步空间/学习课程/尚硅谷 RabitMQ/logs/rabbitmq_info.txt");
                FileUtils.writeStringToFile(file,msg,"UTF-8");
                System.out.println("数据写入文件成功");
        };
        /*
        把交换机和队列进行绑定
         */
        channel.queueBind(queueName,EXCHANGE_NAME,"ddd");
        System.out.println("等待接收消息,打印");
        channel.basicConsume(queueName,true,deliverCallback,cusumerTag->{});
    }
}
