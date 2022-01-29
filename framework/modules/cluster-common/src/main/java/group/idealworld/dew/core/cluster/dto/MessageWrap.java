/*
 * Copyright 2022. the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.idealworld.dew.core.cluster.dto;

import java.util.Map;
import java.util.Optional;

/**
 * 消息体.
 *
 * @author gudaoxuri
 */
public class MessageWrap {

    /**
     * The message name/topic.
     */
    private String name;
    /**
     * The message header.
     */
    private Optional<Map<String, Object>> header;
    /**
     * The message body.
     */
    private String body;

    /**
     * Instantiates a new Message wrap.
     */
    public MessageWrap() {
    }

    /**
     * Instantiates a new Message wrap.
     *
     * @param name the message name/topic
     * @param body the message body
     */
    public MessageWrap(String name, String body) {
        this.name = name;
        this.body = body;
    }

    /**
     * Instantiates a new Message wrap.
     *
     * @param name   the message name/topic
     * @param header the message header
     * @param body   the message body
     */
    public MessageWrap(String name, Optional<Map<String, Object>> header, String body) {
        this.name = name;
        this.header = header;
        this.body = body;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets header.
     *
     * @return the header
     */
    public Optional<Map<String, Object>> getHeader() {
        return header;
    }

    /**
     * Sets header.
     *
     * @param header the header
     */
    public void setHeader(Optional<Map<String, Object>> header) {
        this.header = header;
    }

    /**
     * Gets body.
     *
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets body.
     *
     * @param body the body
     */
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "MessageWrap{"
                + "name='" + name + '\''
                + ", header=" + header
                + '}';
    }

}
