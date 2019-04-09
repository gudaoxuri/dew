/*
 * Copyright 2019. the original author or authors.
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

package ms.dew.devops.it.todo.compute.service;

import com.ecfront.dew.common.$;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;

/**
 * Compute service.
 *
 * @author gudaoxuri
 */
@Service
public class ComputeService {

    private static final Logger logger = LoggerFactory.getLogger(ComputeService.class);

    /**
     * Compute string.
     *
     * @param jsCode the js code
     * @return result
     * @throws ScriptException the script exception
     */
    public String compute(String jsCode) throws ScriptException {
        logger.info("Compute : " + jsCode);
        return $.eval(String.valueOf(jsCode)).toString();
    }

}
