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
