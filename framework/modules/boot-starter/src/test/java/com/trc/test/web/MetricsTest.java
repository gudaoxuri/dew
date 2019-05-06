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

package com.trc.test.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Metrics test.
 *
 * @author gudaoxuri
 */
@Component
public class MetricsTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    /**
     * Test metric.
     *
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public void testMetric() throws IOException, InterruptedException {
        for (int i = 0; i < 100; i++) {
            testRestTemplate.getForObject("/test/valid-method-spring/2", String.class);
            Thread.sleep(5);
        }
    }
}
