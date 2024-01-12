package group.idealworld.dew.core.cluster.dto;

import java.util.Map;

/**
 * 消息Meta.
 *
 * @author gudaoxuri
 */
public class MessageHeader {

    /**
     * The message name/topic.
     */
    public String name;
    /**
     * The message header.
     */
    public Map<String, Object> header;

    /**
     * Instantiates a new Message header.
     *
     * @param name   the message name/topic
     * @param header the message header
     */
    public MessageHeader(String name, Map<String, Object> header) {
        this.name = name;
        this.header = header;
    }
}
