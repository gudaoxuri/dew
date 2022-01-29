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

package group.idealworld.dew.devops.kernel.util;

import java.security.Permission;
import java.util.function.Consumer;

/**
 * The type Exit monitor processor.
 *
 * @author gudaoxuri
 */
public class ExitMonitorProcessor {

    private static Integer EXIT_STATUS = 0;

    /**
     * Hook.
     *
     * @param procFun the proc fun
     */
    public static void hook(Consumer<Integer> procFun) {
        System.setSecurityManager(new ExitMonitorSecurityManager());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            procFun.accept(EXIT_STATUS);
        }));
    }

    private static class ExitMonitorSecurityManager extends SecurityManager {

        @Override
        public void checkPermission(Permission perm) {
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
        }

        @Override
        public void checkExit(int status) {
            EXIT_STATUS = status;
            super.checkExit(status);
        }

    }

}
