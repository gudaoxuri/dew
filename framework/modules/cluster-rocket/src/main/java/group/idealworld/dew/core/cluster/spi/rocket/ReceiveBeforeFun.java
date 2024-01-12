package group.idealworld.dew.core.cluster.spi.rocket;

import java.util.Map;

@FunctionalInterface
public interface ReceiveBeforeFun {

    Object invoke(String topic, Map<String, Object> properties);

}
