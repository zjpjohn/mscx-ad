package com.sxzhongf.kafkademo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * KafkaDemoConsumer for kafka原生消费API使用
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/8/14
 */
public class KafkaDemoConsumer {

    //创建kafka consumer
    private static KafkaConsumer<String, String> consumer;
    private static Properties properties;

    static {
        properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");// kafka broker list addrs
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        //配置kafka消费者组 KafkaDemo
        properties.put("group.id", "KafkaDemo");
    }

    /**
     * 消费kafka队列消息，并自动提交位移量
     */
    private static void consumeMessageAutoCommit() {

        //设置 kafka 位移自动提交开启
        properties.put("enable.auto.commit", true);
        consumer = new KafkaConsumer<String, String>(properties);

        //订阅一个指定的topic
        consumer.subscribe(Collections.singleton("mscx-kafka-demo-partitioner"));

        try {
            //消费订阅到的消息
            while (true) {
                boolean tag = true;
                // 拉取消息并设置超时时间
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Topic : %s, Partition: %s, Offset : %s , key : %s, value : %s\n"
                            , record.topic(), record.partition(), record.offset(), record.key(), record.value());

                    if (record.value().equals("done")) {
                        tag = false;
                    }
                }

                //获取消息是否已经消费结束
                if (!tag) break;
            }
        } finally {
            consumer.close();
        }
    }

    public static void main(String[] args) {
        consumeMessageAutoCommit();
    }
}
