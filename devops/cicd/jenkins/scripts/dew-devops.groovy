pipeline {
    agent any
    environment {
        mvnHome = tool 'maven'                          // 请根据实际情况进行修改
        nodeHome = "/opt/nvm/versions/node/v8.5.0/bin"  // 请根据实际情况进行修改
        PATH = "${mvnHome}/bin:${PATH}:${nodeHome}"
        credentialsId = "${env.CREDENTIALSID_GIT}"
    }
    stages {
        stage("Preparation") {
            steps {
                script {
                    // 把要部署的代码分支的origin前缀去除
                    branch = sh(
                            script: "echo ${env.branch} | sed -e 's|origin/||g'",
                            returnStdout: true
                    ).trim()
                    // 设置使用的Jenkins节点，profile，Kubernetes集群 进行部署
                    if (env.JOB_NAME.toLowerCase().contains("prd") || env.JOB_NAME.toLowerCase().contains("prod")) {
                        profile = "prd"
                        kube_config = "${env.DEW_DEVOPS_KUBE_CONFIG_PRD}"
                    } else if (env.JOB_NAME.toLowerCase().contains("uat")) {
                        profile = "${env.DEW_DEVOPS_PROJECT_PROFILE_UAT}"
                        kube_config = "${emv.DEW_DEVOPS_KUBE_CONFIG_UAT}"
                    } else if (env.JOB_NAME.toLowerCase().contains("test")) {
                        profile = "${env.DEW_DEVOPS_PROJECT_PROFILE_TEST}"
                        kube_config = "${env.DEW_DEVOPS_KUBE_CONFIG_TEST}"
                    }

                    //若参数化构建的 profile 有值，则使用传值
                    if (env.profile) {
                        profile = "${env.profile}"
                    }
                    if (env.kube_config) {
                        kube_config = "${env.kube_config}"
                    } else {
                        // 根据 profile 的值来设置对应环境的 kube_config
                        if (profile && profile.toLowerCase() == "uat") {
                            kube_config = "${env.DEW_DEVOPS_KUBE_CONFIG_UAT}"
                        } else if (profile.toLowerCase() == "test") {
                            kube_config = "${env.DEW_DEVOPS_KUBE_CONFIG_TEST}"
                        } else if (profile.toLowerCase() == "prd" || profile.toLowerCase() == "prod") {
                            kube_config = "${env.DEW_DEVOPS_KUBE_CONFIG_PRD}"
                        }
                    }
                    if (env.profile == "prd" || env.profile == "prod" || env.profile == "PRD" || env.profile == "PROD") {
                        // 生产环境的 agent 都为 master
                        agentLabel = "master"
                    }
                    //若参数化构建的 jenkins_agent 有值，则使用传值;为空则默认使用 master 节点
                    if (env.jenkins_agent) {
                        agentLabel = env.jenkins_agent
                    } else {
                        agentLabel = "master"
                    }
                    echo "使用【${agentLabel}】节点进行部署。profile为：【${profile}】"
                    if (kube_config == "null") {
                        echo "Warning: kube_config未设值。"
                    }

                    // 设置部署时使用的线程数
                    if (env.devops_mvn_thread) {
                        devops_mvn_thread_cmd = "-T ${devops_mvn_thread} "
                    } else {
                        devops_mvn_thread_cmd = ""
                    }
                    // 设置部署类型
                    if (env.devops_phase && env.devops_phase == "deploy") {
                        devops_mvn_phase_cmd = "deploy"
                    } else if (env.devops_phase == "unrelease") {
                        devops_mvn_phase_cmd = "dew:unrelease"
                    } else if (env.devops_phase == "rollback") {
                        devops_mvn_phase_cmd = "dew:rollback"
                    } else if (env.devops_phase == "restart" || env.devops_phase == "refresh") {
                        devops_mvn_phase_cmd = "dew:refresh"
                    } else if (env.devops_phase == "autoscale") {
                        devops_mvn_phase_cmd = "dew:scale -Ddew_devops_scale_auto=true "
                    } else if (env.devops_phase == "scale") {
                        devops_mvn_phase_cmd = "dew:scale"
                    } else if (env.devops_phase == "debug") {
                        devops_mvn_phase_cmd = "dew:debug"
                    } else {
                        devops_mvn_phase_cmd = "deploy"
                    }

                    // 指定项目
                    if (env.devops_assign_services) {
                        devops_mvn_assign_services_cmd = "-Ddew_devops_assignation_projects=${env.devops_assign_services}"
                    } else {
                        devops_mvn_assign_services_cmd = ""
                    }
                    // 是否有附加maven命令
                    if (env.devops_appended_cmd) {
                        devops_mvn_append_cmd = "${env.devops_appended_cmd}"
                    } else {
                        devops_mvn_append_cmd = ""
                    }

                    // 如果使用交互式回滚方式，则先显示历史版本
                    if (env.rollback_input_enable && env.rollback_input_enable == 'true') {
                        history = 'true'
                    }
                    // 如果启用debug调试，则进入debug模式
                    if (env.maven_debug && env.maven_debug == "true") {
                        mvn_cmd = "mvnDebug"
                    } else {
                        mvn_cmd = "mvn"
                    }

                    devops_mvn_based_cmd = "${devops_mvn_thread_cmd} -U ${devops_mvn_assign_services_cmd} ${devops_mvn_append_cmd} -Dmaven.test.skip=true -Ddew_devops_profile=${profile} -Ddew_devops_quiet=true -Ddew_devops_kube_config=${kube_config} -Ddew_devops_docker_host=${DEW_DEVOPS_DOCKERD_HOST} -Ddew_devops_docker_registry_url=${DEW_DEVOPS_HARBOR_HOST}/v2 -Ddew_devops_docker_registry_username=${DEW_DEVOPS_HARBOR_USERNAME} -Ddew_devops_docker_registry_password=${DEW_DEVOPS_HARBOR_PASSWORD} "
                }
            }
        }
        stage('Execution') {
            agent {
                label "${agentLabel}"
            }
            stages {
                stage('Checkout & clean') {
                    steps {
                        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${credentialsId}", var: 'credentialsId']]]) {
                            echo "Checkout code : ${env.git_repo} branch: ${branch}"
                            git branch: "${branch}", credentialsId: "${credentialsId}", url: "${env.git_repo}"
                        }
                        script {
                            if (env.checkout_enabled != "true") {
                                sh "mvn clean && ls -a"
                            }
                        }
                    }
                }
                stage('DevOps') {
                    when {
                        not {
                            environment name: 'checkout_enabled', value: 'true'
                        }
                    }
                    stages {
                        stage('Deploy') {
                            when {
                                not {
                                    environment name: 'devops_phase', value: 'rollback'
                                }
                            }
                            steps {
                                wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [
                                        [password: "${DEW_DEVOPS_HARBOR_PASSWORD}", var: 'DEW_DEVOPS_HARBOR_PASSWORD'],
                                        [password: "${kube_config}", var: 'kube_config']
                                ]]) {
                                    script {
                                        devops_mvn_based_cmd = "${devops_mvn_thread_cmd} -U ${devops_mvn_assign_services_cmd} ${devops_mvn_append_cmd} -Dmaven.test.skip=true -Ddew_devops_profile=${profile} -Ddew_devops_quiet=true -Ddew_devops_kube_config=${kube_config} -Ddew_devops_docker_host=${DEW_DEVOPS_DOCKERD_HOST} -Ddew_devops_docker_registry_url=${DEW_DEVOPS_HARBOR_HOST}/v2 -Ddew_devops_docker_registry_username=${DEW_DEVOPS_HARBOR_USERNAME} -Ddew_devops_docker_registry_password=${DEW_DEVOPS_HARBOR_PASSWORD} "
                                        if (env.devops_phase && env.devops_phase == "redeploy") {
                                            devops_mvn_phase_cmd = "dew:unrelease"
                                            sh "${mvn_cmd} -P devops ${devops_mvn_phase_cmd} ${devops_mvn_based_cmd}"
                                            devops_mvn_phase_cmd = "deploy"
                                            sh "${mvn_cmd} -P devops ${devops_mvn_phase_cmd} ${devops_mvn_based_cmd}"
                                        } else {
                                            sh "${mvn_cmd} -P devops ${devops_mvn_phase_cmd} ${devops_mvn_based_cmd}"
                                        }
                                    }
                                }
                            }
                        }
                        stage('Rollback') {
                            when {
                                environment name: 'devops_phase', value: 'rollback'
                            }
                            stages {
                                stage('version history') {
                                    when {
                                        // 进入条件： env.history == 'true' 或 history == 'true'
                                        // 即: 指定显示历史版本，或使用交互式回滚模式时，进入此stage
                                        expression {
                                            env.history == 'true' || history == 'true'
                                        }
                                    }
                                    steps {
                                        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [
                                                [password: "${DEW_DEVOPS_HARBOR_PASSWORD}", var: 'DEW_DEVOPS_HARBOR_PASSWORD'],
                                                [password: "${kube_config}", var: 'kube_config']
                                        ]]) {
                                            script {
                                                devops_mvn_rollback_cmd = "-Ddew_devops_version_history=true"
                                                sh "${mvn_cmd} -P devops ${devops_mvn_phase_cmd} ${devops_mvn_rollback_cmd} ${devops_mvn_based_cmd}"
                                            }
                                        }
                                    }
                                }
                                stage('rollback version') {
                                    when {
                                        // 当不使用交互式方式进行回滚时，进行指定版本回滚模式
                                        // 进入条件：is_exit == false & rollback_input_enable = false & devops_rollback_version有值
                                        allOf {
                                            environment name: 'rollback_input_enable', value: 'false'
                                            expression {
                                                env.devops_rollback_version != null && env.devops_rollback_version != ""
                                            }
                                        }
                                    }
                                    steps {
                                        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [
                                                [password: "${DEW_DEVOPS_HARBOR_PASSWORD}", var: 'DEW_DEVOPS_HARBOR_PASSWORD'],
                                                [password: "${kube_config}", var: 'kube_config']
                                        ]]) {
                                            script {
                                                devops_mvn_rollback_cmd = "-Ddew_devops_rollback_version=${env.devops_rollback_version} "
                                                if (env.devops_assign_services) {
                                                    devops_mvn_rollback_cmd = "${devops_mvn_rollback_cmd} ${devops_mvn_assign_services_cmd}"
                                                }
                                                sh "${mvn_cmd} -P devops ${devops_mvn_phase_cmd} ${devops_mvn_rollback_cmd} ${devops_mvn_based_cmd}"
                                            }
                                        }
                                    }
                                }
                                stage('rollback input') {
                                    when {
                                        environment name: 'rollback_input_enable', value: 'true'
                                    }
                                    steps {
                                        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [
                                                [password: "${DEW_DEVOPS_HARBOR_PASSWORD}", var: 'DEW_DEVOPS_HARBOR_PASSWORD'],
                                                [password: "${kube_config}", var: 'kube_config']
                                        ]]) {
                                            script {
                                                timeout(time: 90, unit: 'SECONDS') {
                                                    echo "*******************************************************************************"
                                                    echo "***********             请点击下方链接，进行回滚操作             **************"
                                                    rollbackInput = input(
                                                            id: 'answerInput',
                                                            message: '请输入要回滚的版本',
                                                            ok: 'ok',
                                                            parameters: [
                                                                    string(name: 'version', description: '要回滚的版本'),
                                                                    string(name: 'service', description: '要回滚的服务')
                                                            ])
                                                }
                                                if (rollbackInput['version']) {
                                                    devops_mvn_rollback_cmd = "-Ddew_devops_rollback_version=${rollbackInput['version']} "
                                                } else {
                                                    echo "回滚版本不能为空!构建终止。"
                                                    return
                                                }
                                                if (rollbackInput['service']) {
                                                    devops_mvn_rollback_cmd = "${devops_mvn_rollback_cmd} -Ddew_devops_assignation_projects=${rollbackInput['service']}"
                                                }
                                                sh "${mvn_cmd} -P devops ${devops_mvn_phase_cmd} ${devops_mvn_rollback_cmd} ${devops_mvn_based_cmd}"

                                                timeout(time: 30, unit: 'SECONDS') {
                                                    echo "*******************************************************************************"
                                                    echo "***********           请点击下方链接，选择是否继续回滚           **************"
                                                    answerInput = input(
                                                            id: 'answerInput',
                                                            message: '是否继续进行回滚',
                                                            ok: 'ok',
                                                            parameters: [
                                                                    [$class: 'BooleanParameterDefinition', defaultValue: false, name: 'answer', description: '如需继续，请勾选']
                                                            ])
                                                }
                                                while (answerInput == true) {
                                                    timeout(time: 30, unit: 'SECONDS') {
                                                        echo "*******************************************************************************"
                                                        echo "***********             请点击下方链接，进行回滚操作             **************"
                                                        rollbackInput = input(id: 'rollbackInput', message: '请输入需要回退的模块和版本', parameters: [
                                                                [$class: 'StringParameterDefinition', defaultValue: '', description: '需要回退的版本', name: 'version'],
                                                                [$class: 'StringParameterDefinition', defaultValue: '', description: '需要回退的模块', name: 'service']
                                                        ])
                                                    }
                                                    if (rollbackInput['version']) {
                                                        devops_mvn_rollback_cmd = "-Ddew_devops_rollback_version=${rollbackInput['version']} "
                                                    } else {
                                                        echo "回滚版本不能为空!构建终止。"
                                                        return
                                                    }
                                                    if (rollbackInput['service']) {
                                                        devops_mvn_rollback_cmd = "${devops_mvn_rollback_cmd} -Ddew_devops_assignation_projects=${rollbackInput['service']}"
                                                    }
                                                    sh "${mvn_cmd} -P devops ${devops_mvn_phase_cmd} ${devops_mvn_rollback_cmd} ${devops_mvn_based_cmd}"

                                                    timeout(time: 30, unit: 'SECONDS') {
                                                        echo "*******************************************************************************"
                                                        echo "***********           请点击下方链接，选择是否继续回滚           **************"
                                                        answerInput = input(
                                                                id: 'answerInput',
                                                                message: '是否继续进行回滚',
                                                                ok: 'ok',
                                                                parameters: [
                                                                        [$class: 'BooleanParameterDefinition', defaultValue: false, name: 'answer', description: '如需继续，请勾选']
                                                                ])
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

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
 参数化构建参数：
   · 部署相关参数
        profile                 部署的代码profile；若不指定，默认根据job名匹配设值
        kube_config             Kubernetes集群的kube config的base64值；默认根据job名设值
        jenkins_agent           运行Job的Jenkins节点；默认根据job名设值，job名包含test的为slave，包含uat/prd/prod的为master；profile为prd/prod的默认为master
        devops_mvn_thread       默认为单线程
        devops_phase            默认为deploy
        devops_assign_services  默认为空，多个逗号分隔
        devops_appended_cmd     附加执行命令，scale的附加命令可在此处填写；默认为空
        maven_debug             是否启用mvnDebug
        · 回滚相关参数
             history                    是否显示历史部署版本 (rollback_input_enable=true时，也显示history)
             rollback_input_enable      是否启用交互式回滚方式
             devops_rollback_version    指定的回滚版本，默认为空
   · git相关参数
        checkout_enabled        是否只checkout代码，默认为false。
        branch                  checkout的代码分支，默认值自定义
        git_repo                checkout的代码repo，ssh类型的

    TIPS：更详细说明见 README.adoc
*/
