/*
 * Copyright 2022. gudaoxuri
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

/*
 * 需要以下参数化构建参数
 * jenkins_agents 需要更新的Jenkins节点，多个以，逗号分隔，如不填写，默认为master
 * MAVEN_AGENT_URL 最新的 dew-maven-agent jar 包的地址；仓库路径：https://oss.sonatype.org/content/repositories/public/group/idealworld/dew/dew-maven-agent
 */
pipeline {
    agent any
    stages {
        stage('Dew maven agent update') {
            steps {
                script {
                    if (!env.jenkins_agents) {
                        jenkins_agents = "master"
                    }
                    for (jenkins_agent in jenkins_agents.tokenize(',')) {
                        stage("${jenkins_agent}") {
                            node("${jenkins_agent}") {
                                // MAVEN_AGENT_URL参数值在Jenkins的job的配置中修改
                                sh " mkdir -p /opt/maven/ && mkdir -p /opt/jar/    && curl -o /opt/jar/dew-maven-agent.jar ${MAVEN_AGENT_URL}"
                                sh "cd /opt/jar/ && ls -a "
                            }
                        }
                    }
                }
            }
        }
    }
}
