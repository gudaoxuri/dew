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

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.ReportHandler;
import group.idealworld.dew.devops.kernel.exception.ProjectProcessException;
import org.slf4j.Logger;

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

    private static Logger logger = DewLog.build(ShellHelper.class);

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
        logger.info("[" + flag + "] Exec : " + cmd);
        AtomicBoolean isSuccess = new AtomicBoolean(true);
        try {
            Future<Void> execF = $.shell.execute(cmd, env, null, null, false, new ReportHandler() {
                @Override
                public void errorlog(String line) {
                    logger.warn(line);
                }

                @Override
                public void outputlog(String line) {
                    logger.info(line);
                }

                @Override
                public void fail(String message) {
                    isSuccess.set(false);
                }
            });
            execF.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ProjectProcessException("[" + flag + "] Exec error : " + cmd, ex);
        } catch (ExecutionException ex) {
            throw new ProjectProcessException("[" + flag + "] Exec error : " + cmd, ex);
        }
        if (!isSuccess.get()) {
            // 命令执行过程错误，由上层业务判断是否抛异常
            if (retry < 3) {
                // 重试一次
                logger.warn("[" + flag + "] Exec error : " + cmd + " , Retry  : " + (retry + 1));
                return doExecCmd(flag, env, cmd, retry + 1);
            } else {
                logger.warn("[" + flag + "] Exec error : " + cmd);
            }
            return false;
        }
        return true;
    }

}
