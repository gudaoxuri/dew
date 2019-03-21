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

package com.tairanchina.csp.dew.helper;

import com.ecfront.dew.common.$;
import org.apache.maven.plugin.logging.Log;

import java.util.List;

public class GitHelper {

    private static Log log;

    public static void init(Log log) {
        GitHelper.log = log;
    }

    public static List<String> diff(String startCommitHash, String endCommitHash, String gitRootDirectory) {
        List<String> changedFiles = $.shell.execute("cd  " + gitRootDirectory + " && git diff --name-only " + startCommitHash + " " + endCommitHash).getBody();
        log.info("Found " + changedFiles.size() + " changed files by git diff --name-only " + startCommitHash + " and " + endCommitHash);
        return changedFiles;
    }

    public static String getCurrentBranch(String gitRootDirectory) {
        return $.shell.execute("cd  " + gitRootDirectory + " && git symbolic-ref --short -q HEAD").getBody().get(0);
    }

    public static String getCurrentCommit(String gitRootDirectory) {
        return $.shell.execute("cd  " + gitRootDirectory + " && git rev-parse HEAD").getBody().get(0);
    }

    public static String getScmUrl(String gitRootDirectory) {
        return $.shell.execute("cd  " + gitRootDirectory + " && git config --get remote.origin.url").getBody().get(0);
    }

}
