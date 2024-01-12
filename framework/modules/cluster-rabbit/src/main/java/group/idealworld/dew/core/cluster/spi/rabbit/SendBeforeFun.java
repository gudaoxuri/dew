package group.idealworld.dew.core.cluster.spi.rabbit;

import com.rabbitmq.client.AMQP;

/**
 * The interface Send before fun.
 *
 * @author gudaoxuri
 */
@FunctionalInterface
public interface SendBeforeFun {

    /**
     * Invoke object.
     *
     * @param exchange          the exchange
     * @param routingKey        the routing key
     * @param messageProperties the message properties
     * @return the object
     */
    Object invoke(String exchange, String routingKey, AMQP.BasicProperties messageProperties);

}
