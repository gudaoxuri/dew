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

package group.idealworld.dew.idempotent.strategy;

import group.idealworld.dew.core.cluster.exception.NotImplementedException;

/**
 * Bloom filter processor.
 *
 * @author gudaoxuri
 */
public class BloomFilterProcessor implements IdempotentProcessor {

    @Override
    public StatusEnum process(String optType, String optId, StatusEnum initStatus, long expireMs) {
        throw new NotImplementedException();
    }

    @Override
    public boolean confirm(String optType, String optId) {
        throw new NotImplementedException();
    }

    @Override
    public boolean cancel(String optType, String optId) {
        throw new NotImplementedException();
    }

}
