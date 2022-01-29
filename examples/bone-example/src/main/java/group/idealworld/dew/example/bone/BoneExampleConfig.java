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

package group.idealworld.dew.example.bone;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 在根路径创建配置隐射类.
 *
 * @author gudaoxuri
 */
@Component
@ConfigurationProperties(prefix = "bone-example")
public class BoneExampleConfig {

    private String someProp;

    /**
     * Gets some prop.
     *
     * @return the some prop
     */
    public String getSomeProp() {
        return someProp;
    }

    /**
     * Sets some prop.
     *
     * @param someProp the some prop
     */
    public void setSomeProp(String someProp) {
        this.someProp = someProp;
    }

}
