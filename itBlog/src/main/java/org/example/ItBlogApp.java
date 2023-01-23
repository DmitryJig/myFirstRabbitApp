package org.example;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Message;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * ИТ блог, публикует статьи по языкам программирования
 * (Producer/sender)
 */
@Slf4j
public class ItBlogApp {

//    private final static String QUEUE_NAME = "it_blog"; // todo delete?
    private final static String EXCHANGER_NAME = "it_blog_exchanger";

    public static void main(String[] args) throws IOException, TimeoutException {
        ItBlogApp itBlogApp = new ItBlogApp();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Message message = null;

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGER_NAME, BuiltinExchangeType.DIRECT);

            while (true){ // для выхода из цикла надо набрать "exit"
                message = itBlogApp.readMessage();
                if (message.getType().equalsIgnoreCase("exit")){
                    break;
                }
                System.out.println(message.toString());
                channel.basicPublish(EXCHANGER_NAME, message.getType(), null, message.getBodyMsg().getBytes("UTF-8"));
                System.out.println("[x] Sent '" + message.getBodyMsg() + "'");
            }
        }
    }

    // java hello subscriber from Java
    // php hello subscriber from Php
    public Message readMessage() {
        Scanner scanner = new Scanner(System.in);
        String scannedLine = scanner.nextLine();
        String[] splitString = scannedLine.trim().split(" ", 2);
        Message message = new Message();
        if (splitString.length == 2) {
            message.setType(splitString[0]);
            message.setBodyMsg(splitString[1]);
            return message;
        } else if (splitString[0].equalsIgnoreCase("exit")){
            message.setType("exit");
            return message;
        }
        throw new RuntimeException("Некорректный ввод данных");
    }
}