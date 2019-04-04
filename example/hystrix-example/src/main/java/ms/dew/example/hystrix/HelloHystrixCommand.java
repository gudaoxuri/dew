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

package ms.dew.example.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HelloHystrixCommand extends HystrixCommand<HelloHystrixCommand.Model> {

    public static final Logger logger = LoggerFactory.getLogger(HelloHystrixCommand.class);

    private Model model;

    protected HelloHystrixCommand(HystrixCommandGroupKey group) {
        super(group);
    }

    protected HelloHystrixCommand(HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool) {
        super(group, threadPool);
    }

    protected HelloHystrixCommand(HystrixCommandGroupKey group, int executionIsolationThreadTimeoutInMilliseconds) {
        super(group, executionIsolationThreadTimeoutInMilliseconds);
    }

    protected HelloHystrixCommand(HystrixCommandGroupKey group, HystrixThreadPoolKey threadPool, int executionIsolationThreadTimeoutInMilliseconds) {
        super(group, threadPool, executionIsolationThreadTimeoutInMilliseconds);
    }

    protected HelloHystrixCommand(Setter setter) {
        super(setter);
    }

    public static HelloHystrixCommand getInstance(String key) {
        return new HelloHystrixCommand(HystrixCommandGroupKey.Factory.asKey(key));
    }

    public static void main(String[] args) throws Exception {
        HelloHystrixCommand helloHystrixCommand = HelloHystrixCommand.getInstance("dew");
        helloHystrixCommand.model = helloHystrixCommand.new Model("run");
        logger.info("main:      " + helloHystrixCommand.model + "thread id: " + Thread.currentThread().getId());
        System.out.println(helloHystrixCommand.execute());

    }

    @Override
    protected Model run() throws Exception {
        int i = 1 / 0;
        logger.info("run:   thread id:  " + Thread.currentThread().getId());
        return model;
    }

    @Override
    protected Model getFallback() {
        return new Model("fallback");
    }

    class Model {

        private String name;

        public Model(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Model{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
