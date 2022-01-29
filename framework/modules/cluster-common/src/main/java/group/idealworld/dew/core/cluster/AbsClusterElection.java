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

package group.idealworld.dew.core.cluster;

import group.idealworld.dew.core.cluster.exception.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;


/**
 * 领导者选举服务.
 *
 * @author gudaoxuri
 */
public abstract class AbsClusterElection implements ClusterElection {

    protected static final Logger logger = LoggerFactory.getLogger(AbsClusterElection.class);

    /**
     * 未完成初始化标识.
     */
    protected static final long FLAG_UNINITIALIZED = 0;
    /**
     * 领导者标识.
     */
    protected static final long FLAG_LEADER = 1;
    /**
     * 非领导者标识.
     */
    protected static final long FLAG_FOLLOWER = -1;

    /**
     * The Leader.
     * <p>
     * 0 未初始化， 1 是领导者 -1 不是领导者
     */
    protected AtomicLong leader = new AtomicLong(FLAG_UNINITIALIZED);

    /**
     * 执行（重新）选举.
     * <p>
     * 需调用方定时调用此接口
     */
    protected abstract void election();

    /**
     * 退出选举，暂未实现.
     */
    protected void quit() {
        throw new NotImplementedException();
    }

    /**
     * 当前工程是否是领导者.
     *
     * @return 是否是领导者
     */
    @Override
    public boolean isLeader() {
        while (this.leader.get() == FLAG_UNINITIALIZED) {
            try {
                Thread.sleep(100);
                logger.trace("Waiting leader election...");
            } catch (InterruptedException ex) {
                logger.error("Leader election error", ex);
            }
        }
        return this.leader.get() == FLAG_LEADER;
    }

}
