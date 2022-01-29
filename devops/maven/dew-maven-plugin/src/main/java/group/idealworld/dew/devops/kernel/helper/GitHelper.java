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

package group.idealworld.dew.devops.kernel.helper;

import org.slf4j.Logger;

/**
 * Git辅助类.
 * <p>
 * 使用多实例支持是为方便替换GitOpt为Mock对象以进行集成测试，详见测试实现
 *
 * @author gudaoxuri
 */
public class GitHelper extends MultiInstProcessor {

    /**
     * Init.
     *
     * @param log the log
     */
    public static void init(Logger log) {
        multiInit("GIT", "",
                () -> new GitOpt(log), "");
    }

    /**
     * Fetch GitOpt instance.
     *
     * @return GitOpt instance
     */
    public static GitOpt inst() {
        return multiInst("GIT", "");
    }

}
