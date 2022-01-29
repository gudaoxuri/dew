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

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import group.idealworld.dew.devops.kernel.exception.GitDiffException;
import org.slf4j.Logger;

import java.util.List;

/**
 * Git操作函数类.
 *
 * @author gudaoxuri
 */
public class GitOpt {

    protected Logger log;

    /**
     * Instantiates a new Git opt.
     *
     * @param log the log
     */
    protected GitOpt(Logger log) {
        this.log = log;
    }

    /**
     * Diff list.
     *
     * @param startCommitHash the start commit hash
     * @param endCommitHash   the end commit hash
     * @return diff list
     */
    public List<String> diff(String startCommitHash, String endCommitHash) {
        Resp<List<String>> changedFilesR = $.shell.execute("git diff --name-only " + startCommitHash + " " + endCommitHash);
        if (changedFilesR.ok()) {
            List<String> changedFiles = changedFilesR.getBody();
            log.info("Found " + changedFiles.size() + " changed files by git diff --name-only " + startCommitHash + " " + endCommitHash);
            return changedFiles;
        } else if (changedFilesR.getMessage().contains("fatal: bad object") || changedFilesR.getMessage().contains("Abnormal termination")) {
            // 如果 git diff 执行时报错，将异常抛出，方便之后继续部署。
            // 此报错常见于代码回退后，使用 Gitlab Runner 全量重新拉取代码部署时，无法获取commit id；
            // 使用 Jenkins 部署，增量拉取代码未见异常。
            throw new GitDiffException(changedFilesR.getMessage());
        } else {
            throw new RuntimeException(changedFilesR.getMessage());
        }
    }

    /**
     * 获取分支名称.
     * <p>
     * 警告：在HEAD detached状态无效
     *
     * @return 分支名称 current branch
     */
    public String getCurrentBranch() {
        Resp<List<String>> result = $.shell.execute("git symbolic-ref --short -q HEAD");
        if (result.ok()) {
            return result.getBody().get(0);
        } else {
            return "";
        }
    }

    /**
     * Gets current commit.
     *
     * @return the current commit
     */
    public String getCurrentCommit() {
        return $.shell.execute("git rev-parse HEAD").getBody().get(0);
    }

    /**
     * Gets scm url.
     *
     * @return the scm url
     */
    public String getScmUrl() {
        return $.shell.execute("git config --get remote.origin.url").getBody().get(0);
    }

}
