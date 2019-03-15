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

package com.trc.test.cluster;

import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.cluster.test.*;
import org.springframework.stereotype.Component;

@Component
public class ClusterTest {

    public void testAll() throws InterruptedException {
        new ClusterMQTest().test(Dew.cluster.mq);
        new ClusterCacheTest().test(Dew.cluster.cache);
        new ClusterLockTest().test(Dew.cluster.lock.instance("test"));
        new ClusterMapTest().test(Dew.cluster.map.instance("test", ClusterMapTest.TestMapObj.class));
        new ClusterElectionTest().test(Dew.cluster.election.instance("test"));
    }

}
