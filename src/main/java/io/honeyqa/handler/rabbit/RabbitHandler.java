package io.honeyqa.handler.rabbit;

import java.io.IOException;
import io.honeyqa.connector.ConnectionManager;
import io.honeyqa.*;
import com.rabbitmq.client.*;

public class RabbitHandler {

        private static final String TASK_QUEUE_NAME = "honey_queue";

        public static void main(String[] argv) throws Exception {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            final Connection connection = factory.newConnection();
            final Channel channel = connection.createChannel();

            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            System.out.println(" Waiting for messages");

            channel.basicQos(1);

            final Consumer consumer = new DefaultConsumer(channel) {

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");

                    System.out.println(" [x] Received '" + message + "'");

                    try {

                        doWork(message);

                    } finally {

                       // channel.basicConsume(TASK_QUEUE_NAME,false,consumer);

                        System.out.println(" [x] Done");

                        channel.basicAck(envelope.getDeliveryTag(), false);

                    }
                }
            };

            channel.basicConsume(TASK_QUEUE_NAME, false, consumer);

        }

        private static void doWork(String task) {
            for (char ch : task.toCharArray()) {
                if (ch == '.') {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException _ignored) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }


