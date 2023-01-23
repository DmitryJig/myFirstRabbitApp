package org.example;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 *  Подписчик, при запуске пишет "set_topic php"
 *  после чего начинает получать из очереди сообщения с темой "php"
 *  (Consumer/receiver)
 */
public class SubscriberApp {
//    private final static String QUEUE_NAME = "it_blog"; // todo delete
    private final static String EXCHANGER_NAME = "it_blog_exchanger";


    public static void main(String[] args) throws IOException, TimeoutException {
        SubscriberApp subscriberApp = new SubscriberApp();
        String topic = subscriberApp.readTopic();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGER_NAME, BuiltinExchangeType.DIRECT); // проверяем что эксченжер у нас есть

        String queueName = channel.queueDeclare().getQueue(); // просим канал создать очередь и дать ей имя
        System.out.println("My queue name: " + queueName);
        System.out.println("Topic: " + topic);
        channel.queueBind(queueName, EXCHANGER_NAME, topic); // делаем бинд на временную очередь

        System.out.println(" [*] Waiting for messages");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> { // работает в отдельном потоке
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });

    }

    // set_topic java
    // set_topic php
    public String readTopic(){
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] split = line.trim().split(" ");
        if (split.length == 2 && "set_topic".equals(split[0])){
            return split[1];
        }
        throw new RuntimeException("Error message");
    }
}