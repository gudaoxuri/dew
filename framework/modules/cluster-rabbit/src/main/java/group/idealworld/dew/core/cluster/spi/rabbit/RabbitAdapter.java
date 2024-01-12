package group.idealworld.dew.core.cluster.spi.rabbit;

import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Rabbit adapter.
 *
 * @author gudaoxuri
 */
public class RabbitAdapter {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Instantiates a new Rabbit adapter.
     *
     * @param rabbitTemplate the rabbit template
     */
    public RabbitAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Gets connection.
     *
     * @return the connection
     */
    Connection getConnection() {
        return rabbitTemplate.getConnectionFactory().createConnection();
    }

}
