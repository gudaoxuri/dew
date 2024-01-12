package group.idealworld.dew.core.cluster.spi.rabbit;

import com.rabbitmq.client.AMQP;

/**
 * The interface Receive before fun.
 *
 * @author gudaoxuri
 */
@FunctionalInterface
public interface ReceiveBeforeFun {

    /**
     * Invoke object.
     *
     * @param exchange          the exchange
     * @param routingKey        the routing key
     * @param queueName         the queue name
     * @param messageProperties the message properties
     * @return the object
     */
    Object invoke(String exchange, String routingKey, String queueName, AMQP.BasicProperties messageProperties);

}
