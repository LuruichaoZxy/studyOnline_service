import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class consumer03_routing_sms {
    //队列名称

    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_ROUTING_INFORM="exchange_routing_inform";
    private static final String ROUTING_KEY_SMS="routing_key_sms";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //设置MabbitMQ所在服务器的ip和端口
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        //声明队列
        channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM,BuiltinExchangeType.DIRECT);
        //队列和交换机进行绑定
        channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_ROUTING_INFORM,ROUTING_KEY_SMS);
        //定义消费方法
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            /*** 消费者接收消息调用此方法
             * * @param consumerTag 消费者的标签，在channel.basicConsume()去指定
             * * @param envelope 消息包的内容，可从中获取消息id，消息routingkey，交换机，消息和重传标志 (收到消息失败后是否需要重新发送)
             * * @param properties
             * * @param body
             * * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //交换机
                String exchange = envelope.getExchange();
                //路由key
                String routingKey = envelope.getRoutingKey();
                //消息id
                long deliveryTag = envelope.getDeliveryTag();
                //消息内容
                String msg = new String(body);
                System.out.println("路由key:"+routingKey);
                System.out.println("receive message.." + msg);
            }
        };
        /*** 监听队列String queue, boolean autoAck,Consumer callback
         * * 参数明细
         * * 1、队列名称
         * * 2、是否自动回复，设置为true为表示消息接收到自动向mq回复接收到了，mq接收到回复会删除消息，设置 为false则需要手动回复
         * * 3、消费消息的方法，消费者接收到消息后调用此方法
         * */
        channel.basicConsume(QUEUE_INFORM_SMS, true, consumer);

    }
}
