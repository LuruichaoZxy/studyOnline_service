package rabbit;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class producer02_publish {

    //队列名称
     private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
     private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
     private static final String EXCHANGE_FANOUT_INFORM = "exchange_fanout_inform";
    public static void main(String[] args) {
        //通过连接工厂创建新的连接个mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //创建虚拟机
        connectionFactory.setVirtualHost("/");

        Connection connection = null;

        try{
            //建立新连接
            connection = connectionFactory.newConnection();
            //建立会话通道
            Channel channel = connection.createChannel();
            //声明队列
            /*** 声明队列，如果Rabbit中没有此队列将自动创建
             * * param1:队列名称
             * * param2:是否持久化
             * * param3:队列是否独占此连接
             * * param4:队列不再使用时是否自动删除此队列
             * * param5:队列参数
             * */
            channel.queueDeclare(QUEUE_INFORM_EMAIL,true,false,false,null);
            channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);
            //声明交换机
            /**
             * 交换机名称
             * 交换机类型
             * fanout:对应的工作模式是public/subscribe 发布订阅模式
             * direct:对应routing 路由工作模式
             * topic:对应topic 通配符工作模式
             * headers: 对应headers工作模式
             */
            channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);
            //交换机和队列绑定
            /**
             * String queue, String exchange, String routingKey
             * queue:队列名称
             * exchange:交换机名称
             * routerKey:在发布订阅模式中设为空字符串  作用是交换机根据路由key值把消息转发到指定的队列中
             */
            channel.queueBind(QUEUE_INFORM_EMAIL,EXCHANGE_FANOUT_INFORM,"");
            channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_FANOUT_INFORM,"");

            /*** 消息发布方法
             * * param1：Exchange的名称，如果没有指定，则使用Default Exchange
             * * param2:routingKey,消息的路由Key，是用于Exchange（交换机）将消息转发到指定的消息队列
             * * param3:消息包含的属性
             * * param4：消息体
             * */
             /*** 这里没有指定交换机，消息将发送给默认交换机，每个队列也会绑定那个默认的交换机，但是不能显 示绑定或解除绑定* 默认的交换机，
              * routingKey等于队列名称 */
            for (int i = 0; i < 5; i++) {
                String message = "send message to user";
                channel.basicPublish(EXCHANGE_FANOUT_INFORM,"",null,message.getBytes());
                System.out.println("mq:public"+message);
            }


        }catch (Exception e){

        }

    }
}
