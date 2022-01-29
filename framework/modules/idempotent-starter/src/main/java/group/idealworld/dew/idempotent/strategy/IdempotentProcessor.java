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


/**
 * The interface Idempotent processor.
 *
 * @author gudaoxuri
 */
public interface IdempotentProcessor {

    /**
     * Process status enum.
     *
     * @param optType    the opt type
     * @param optId      the opt id
     * @param initStatus the init status
     * @param expireMs   the expire ms
     * @return the status enum
     */
    StatusEnum process(String optType, String optId, StatusEnum initStatus, long expireMs);

    /**
     * Confirm boolean.
     *
     * @param optType the opt type
     * @param optId   the opt id
     * @return the boolean
     */
    boolean confirm(String optType, String optId);

    /**
     * Cancel boolean.
     *
     * @param optType the opt type
     * @param optId   the opt id
     * @return the boolean
     */
    boolean cancel(String optType, String optId);

}
