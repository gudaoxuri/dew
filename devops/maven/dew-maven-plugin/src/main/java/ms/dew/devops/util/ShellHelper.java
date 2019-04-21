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

package ms.dew.devops.util;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.ReportHandler;
import ms.dew.devops.exception.ProcessException;
import ms.dew.devops.kernel.Dew;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Shell executor.
 *
 * @author gudaoxuri
 */
public class ShellHelper {

    private ShellHelper() {
    }

    /**
     * Exec cmd.
     *
     * @param flag the flag
     * @param env  环境变量
     * @param cmd  the cmd
     * @return the result
     */
    public static boolean execCmd(String flag, Map<String, String> env, String cmd) {
        return doExecCmd(flag, env, cmd, 1);
    }


    private static boolean doExecCmd(String flag, Map<String, String> env, String cmd, int retry) {
        Dew.log.info("[" + flag + "] Exec : " + cmd);
        AtomicBoolean isSuccess = new AtomicBoolean(true);
        try {
            Future<Void> execF = $.shell.execute(cmd, env, null, null, false, false, new ReportHandler() {
                @Override
                public void errorlog(String line) {
                    Dew.log.warn(line);
                    isSuccess.set(false);
                }

                @Override
                public void outputlog(String line) {
                    Dew.log.debug(line);
                }

                @Override
                public void fail(String message) {
                    isSuccess.set(false);
                }
            });
            execF.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ProcessException("[" + flag + "] Exec error : " + cmd, ex);
        } catch (IOException | ExecutionException ex) {
            throw new ProcessException("[" + flag + "] Exec error : " + cmd, ex);
        }
        if (!isSuccess.get()) {
            // 命令执行过程错误，由上层业务判断是否抛异常
            if (retry < 3) {
                // 重试一次
                Dew.log.warn("[" + flag + "] Exec error : " + cmd + " , Retry  : " + retry + 1);
                return doExecCmd(flag, env, cmd, retry + 1);
            } else {
                Dew.log.warn("[" + flag + "] Exec error : " + cmd);
            }
            return false;
        }
        return true;
    }

}
