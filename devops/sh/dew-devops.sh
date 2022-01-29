#!/bin/bash
#
# Copyright 2022. the original author or authors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
set -o pipefail
set -u

HARBOR_REGISTRY_URL=https://harbor.dew.test
HARBOR_REGISTRY_ADMIN=admin
HARBOR_REGISTRY_ADMIN_PASSWORD=Harbor12345
HARBOR_REGISTRY_PASSWORD_REGEX="(?=^.{8,20}$)(?=^[^\s]*$)(?=.*\d)(?=.*[A-Z])(?=.*[a-z])"
PROJECT_NAMESPACE=devops-example
DEW_HARBOR_USER_NAME=${PROJECT_NAMESPACE}
DEW_HARBOR_USER_PASSWORD=Dew\!123456
DEW_HARBOR_USER_EMAIL=${DEW_HARBOR_USER_NAME}@dew.idealworld.group
HARBOR_PROJECT_PUBLIC_ENABLED="false"
CREATE_PROJECT_NAMESPACE_SKIP="false"
DOCKERD_URL=tcp://dockerd.dew.idealworld.group:2375

MINIO_HOST=minio.dew.test:9000
MINIO_ACCESS_KEY=dew
MINIO_SECRET_KEY=Dew123456
MINIO_BUCKET_NAME=dew

GITLAB_URL=http://gitlab.dew.test
GITLAB_RUNNER_NAMESPACE=devops
GITLAB_RUNNER_NAME=dew-runner
GITLAB_RUNNER_IMAGE=dewms/devops:3.0.0-Beta3
GITLAB_RUNNER_REG_TOKEN=3mezus8cX9qAjkrNY4B
GITLAB_RUNNER_PROFILE=test

KUBERNETES_CONFIG=$(echo $(base64 < ~/.kube/config) |  tr -d " ")
INGRESS_HOST_EXAMPLE="test.dew.test/api user-service:8080"

# ------------------
# Params dealing
# ------------------
GENERAL_INPUT_ANSWER=""

waiting_input_YN(){
    local tip=$1
    local defaultValue=$(echo $2 | tr a-z A-Z)
    GENERAL_INPUT_ANSWER=""
    if [[ "${defaultValue}" == "Y" ]]; then
        tip="${tip} [Y/n]"
    else
        tip="${tip} [y/N]"
    fi
    read -e -n1 -p "${tip}:" answer
    while [[ "${answer}" != "Y" && "${answer}" != "y" && "${answer}" != "N" && "${answer}" != "n" && "${answer}" != "" ]]; do
        read -e -n1 -p "${tip}:" answer
    done
    if [[ "${answer}" == "Y" || "${answer}" == "y"  || ( "${answer}" == "" && ${defaultValue} == "Y" ) ]]; then
        echo "* The answer is \"Yes\", continue."
        GENERAL_INPUT_ANSWER="Y"
    else
        echo "* The answer is \"No\", skip this step."
        GENERAL_INPUT_ANSWER="N"
    fi
}

waiting_input(){
    local tip=$1
    local defaultValue=$2
    local isRequired=${3:-}
    local isSecret=${4:-}
    local readCmd="-e -p"
    GENERAL_INPUT_ANSWER=""
    if [[ "${isSecret}" != "" ]]; then
        readCmd="-s ${readCmd}"
    fi
    if [[ "${defaultValue}" != "" ]]; then
        tip="${tip} [${defaultValue}]:"
    else
        tip="${tip}:"
    fi
    read ${readCmd} "${tip}" answer
    if [[ "${isSecret}" != "" ]]; then
        echo
    fi
    if [[ "${isRequired}" != "" ]]; then
        while [[ "${answer}" == "" ]]; do
            read ${readCmd} "${tip}" answer
            if [[ "${isSecret}" != "" ]]; then
                echo
            fi
        done
    fi
    if [[ "${answer}" == "" ]]; then
        echo "* The answer is ${defaultValue}."
        answer=${defaultValue}
    fi
    GENERAL_INPUT_ANSWER=${answer}
}

# e.g.
# not match regex: waiting_input_check_regex "tips" "regex" "notMatch" "iSecret" "defaultValue"
# need match regex: waiting_input_check_regex "tips" "regex" "Y" "iSecret" "defaultValue"
waiting_input_check_regex(){

    local tip=$1
    local regex=$2
    local needMatch=${3:-}
    local isSecret=${4:-}
    local defaultValue=${5:-}
    local readCmd="-e -p"
    GENERAL_INPUT_ANSWER=""
    if [[ "${isSecret}" != "" ]]; then
        readCmd="-s ${readCmd}"
    fi
    if [[ "${defaultValue}" != "" ]]; then
        tip="${tip} [${defaultValue}]"
    fi
    read ${readCmd} "${tip}:" answer
    if [[ ${answer} == "" && "${defaultValue}" != "" ]]; then
        answer=${defaultValue}
        echo "* No value was entered,using the default [${defaultValue}]."
    fi
    if [[ "${isSecret}" != "" ]]; then
        echo
    fi
    # If "needMatch" is not Y ,the answer would not match the regex.
    if [[ "${needMatch}" == "Y" ]]; then
        while [[ ! "${answer}" =~ ${regex} ]]; do
            read ${readCmd} "* The format of value is not right,please retype:" answer
            if [[ "${isSecret}" != "" ]]; then
                echo
            fi
        done
    elif [[ "${needMatch}" != "" && "${needMatch}" != "Y"  ]]; then
        while [[ "${answer}" =~ ${regex} ]]; do
            read ${readCmd} "* The format of value is not right,please retype:" answer
            if [[ "${isSecret}" != "" ]]; then
                echo
            fi
        done
    fi
    GENERAL_INPUT_ANSWER=${answer}
}

check_regex_match_with_retype(){
    local regex=$1
    local str=$2
    local strType=${3:-}
    while [[ ! "${str}" =~ ${regex} ]]; do
        read -e -p "* The format of ${strType} [${str}]is not right,please input another:" str
    done
    echo ${str}
}

check_regex_match_with_grep(){
    local tip=$1
    local regex=$2
    local strType=${3:-}
    local answer=""
    GENERAL_INPUT_ANSWER=""
    read -e -p "${tip}:" answer
    check_str_format_match=$(echo "${answer}" | grep -P ${regex} | wc -l)
    GENERAL_INPUT_ANSWER=${answer}
    while [[ ${check_str_format_match} != 1 ]]; do
        waiting_input "The format of ${strType} is not right,please retype" "" "Y"
        check_str_format_match=$(echo "${GENERAL_INPUT_ANSWER}" | grep -P ${regex} | wc -l)
    done
}

get_json_value(){
    json=$1
    key=$2
    re="\"($key)\":\"([^\"]*)\""
    if [[ ${json} =~ $re ]]; then
        #name="${BASH_REMATCH[1]}"
        value="${BASH_REMATCH[2]}"
        echo "${value}"
    fi
}

# Deal the input while executing a cmd which needs few minutes.
# the cmd should be located between 'stty igncr' and 'stty -igncr'.
dealing_wrong_input(){
    read -e -s -t 1
}

press_enter_continue(){
    read -n1 -s -p "* Please press [Enter] to continue."
}

INGRESS_HOST_YAML_VALUE=""
deal_ingress_backend_yaml(){
    INGRESS_HOST_YAML_VALUE=""
    local backend_str="$1"
    local url=$(echo ${backend_str} | cut -d ' ' -f1)
    local host=$(echo ${url} | cut -d '/' -f1)
    local svcPath=""
    if [[ ${url} =~ / ]]; then
        svcPath=$(echo ${url#*/})
    fi

    if [[ "${svcPath}" != "" ]]; then
        svcPath=/${svcPath}
    fi
    local backend=$(echo ${backend_str} | cut -d ' ' -f2)
    local svcName=$(echo ${backend} | cut -d ':' -f1)
    local svcPort=$(echo ${backend} | cut -d ':' -f2)

    # Check params.
    local host_regex="^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*$"
    local backend_service_name_regex="^[a-z]([a-z0-9]*)(-[a-z0-9]+)*$"
    local backend_service_port_regex="^[a-z0-9]+$"
    host=$(check_regex_match_with_retype ${host_regex} "${host}" "host")
    svcName=$(check_regex_match_with_retype ${backend_service_name_regex} "${svcName}" "service name")
    svcPort=$(check_regex_match_with_retype ${backend_service_port_regex} "${svcPort}" "service port")
    INGRESS_HOST_YAML_VALUE="
        - host: ${host}
          http:
            paths:
            - backend:
                serviceName: ${svcName}
                servicePort: ${svcPort}
              path: ${svcPath}"
}

# ------------------
# Check
# ------------------

check_kubernetes_env(){
    echo "--------------------------------------"
    echo "## Checking Kubernetes cluster status."
    node_ready_check=$(kubectl get node | grep -w Ready | wc -l)

    node_not_ready_check=$(kubectl get node | grep -w NotReady | wc -l)
    node_x509_check=$(kubectl get node | grep x509 | wc -l)
    if [[ "${node_not_ready_check}" -gt 0 || "${node_x509_check}" -gt 0 ]] ; then
        echo "kubectl get node"
        echo "$(kubectl get node)"
        echo
        echo "There are some Nodes NotReady!Please check your Kubernetes Cluster status."
        echo "--------------------------------------"
        exit;
    fi

    if [[ "${node_not_ready_check}" -eq 0  && "${node_ready_check}" -gt 0 ]] ; then
        echo "Nodes are ready."
    fi
    echo "--------------------------------------"
}

check_minio_status(){
    echo "--------------------------------------"
    echo "## Checking MinIO status."

    waiting_input "Please input your MinIO address" ${MINIO_HOST}
    MINIO_HOST=${GENERAL_INPUT_ANSWER}

    check_minIo_status=$(curl ${MINIO_HOST} -o /dev/nullrl -s -w %{http_code} )
    if [[ "${check_minIo_status}" != 403 ]]; then
        echo
        echo "* MinIO was not able to be accessed.Please check your MinIO environment."
        echo "The script to end."
        exit;
    fi
    echo
    echo "MinIO is ready."

    echo
    echo "# Checking the access key and secret key."
    waiting_input "Please input your MinIO access key" ${MINIO_ACCESS_KEY}
    MINIO_ACCESS_KEY=${GENERAL_INPUT_ANSWER}
    waiting_input "Please input your MinIO secret key" minio_secret_key "Y" "Y"
    MINIO_SECRET_KEY=${GENERAL_INPUT_ANSWER}

    echo
    local minio_key_check=$(curl -X POST "${MINIO_HOST}/minio/webrpc" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"id\":1,\"jsonrpc\":\"2.0\",\"params\":{\"username\":\"${MINIO_ACCESS_KEY}\",\"password\":\"${MINIO_SECRET_KEY}\"},\"method\":\"Web.Login\"}}" -s -k)

    while [[ "${minio_key_check}" =~ error ]]; do
        echo "* Your MinIo access key or secret may be not right."
        echo "* "$(get_json_value "${minio_key_check}" "message")
        waiting_input "Please input your MinIO access key" "" "Y"
        MINIO_ACCESS_KEY=${GENERAL_INPUT_ANSWER}
        waiting_input "Please input your MinIO secret key" "" "Y" "Y"
        MINIO_SECRET_KEY=${GENERAL_INPUT_ANSWER}
        minio_key_check=$(curl -X POST "${MINIO_HOST}/minio/webrpc" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"id\":1,\"jsonrpc\":\"2.0\",\"params\":{\"username\":\"${MINIO_ACCESS_KEY}\",\"password\":\"${MINIO_SECRET_KEY}\"},\"method\":\"Web.Login\"}}" -s -k)
    done
    echo "The access key and secret key are right."

    echo
    echo "# The MinIO bucket must be already created."
    waiting_input "Please input your MinIO bucket name for gitlab storage" ${MINIO_BUCKET_NAME} Y
    MINIO_BUCKET_NAME=${GENERAL_INPUT_ANSWER}

    echo
    echo "--------------------------------------"
}

check_harbor_status(){
    echo "------------------------------------"
    echo "## Checking Harbor status..."
    echo
    waiting_input "Please input Harbor registry url" ${HARBOR_REGISTRY_URL}
    HARBOR_REGISTRY_URL=${GENERAL_INPUT_ANSWER}
    harbor_registry_health_check="curl ${HARBOR_REGISTRY_URL}/health -k"
    registry_status=$(curl ${HARBOR_REGISTRY_URL}/health -o /dev/nullrl -s -w %{http_code} -k)
    if [[ "${registry_status}" -ne 200 ]]; then
        echo
        echo ${harbor_registry_health_check}
        echo "$(${harbor_registry_health_check})"
        echo "Harbor was not able to be accessed.Please check your Harbor environment."
        echo "The script to end."
        exit;
    fi
    echo
    echo "Harbor is ready."
    echo "------------------------------------"
}

check_harbor_admin_account(){

    waiting_input "Please input your Harbor registry admin account" ${HARBOR_REGISTRY_ADMIN}
    HARBOR_REGISTRY_ADMIN=${GENERAL_INPUT_ANSWER}

    echo
    echo "# Please input your Harbor registry admin account password."
    echo "# The password should have the length between 8 and 20,"
    echo "# and contain an uppercase letter, a lowercase letter and a number."

    read -e -s -p "Input your Harbor admin password: " registry_password

    local check_password=$(echo "${registry_password}" | grep -P ${HARBOR_REGISTRY_PASSWORD_REGEX}| wc -l)
    while [[ "${check_password}" -eq 0 ]];do
        echo
        read -e -s -p "The password format is not right, please retype: " registry_password
        check_password=$(echo "${registry_password}" | grep -P ${HARBOR_REGISTRY_PASSWORD_REGEX}| wc -l)
    done
    HARBOR_REGISTRY_ADMIN_PASSWORD=${registry_password}

    ADMIN_AUTHORIZATION=$(echo -n ${HARBOR_REGISTRY_ADMIN}:${HARBOR_REGISTRY_ADMIN_PASSWORD} | base64)
    check_admin_status=$(curl "${HARBOR_REGISTRY_URL}/api/users" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -o /dev/nullrl -s -w %{http_code} -k)
    while [[ ${check_admin_status} -eq 401 || ${check_admin_status} -eq 403 ]];do
        echo
        echo "* Password or admin account maybe not right,or the account not admin.Please retype admin account and password."
        waiting_input "Please input Harbor registry admin account" "" "Y"
        HARBOR_REGISTRY_ADMIN=${GENERAL_INPUT_ANSWER}
        waiting_input "Please input password" "" "Y" "Y"
        HARBOR_REGISTRY_ADMIN_PASSWORD=${GENERAL_INPUT_ANSWER}
        ADMIN_AUTHORIZATION=$(echo -n ${HARBOR_REGISTRY_ADMIN}:${HARBOR_REGISTRY_ADMIN_PASSWORD} | base64)
        check_admin_status=$(curl "${HARBOR_REGISTRY_URL}/api/users" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -o /dev/nullrl -s -w %{http_code} -k)
    done
}

check_harbor_password(){
  local user_name=$1
  local user_password=$2
  check_password=$(curl -X POST  -d "principal=${user_name}&password=${user_password}" "${HARBOR_REGISTRY_URL}/c/login"  -o /dev/nullrl -s -w %{http_code} -k)
    while [[ ${check_password} -eq 401 || ${check_password} -eq 403 ]];do
        echo
        echo "* Password or account maybe not right.Please retype account and password."
        waiting_input "Please input Harbor account" "" "Y"
        user_name=${GENERAL_INPUT_ANSWER}
        waiting_input "Please input password" "" "Y" "Y"
        user_password=${GENERAL_INPUT_ANSWER}
        check_password=$(curl -X POST  -d "principal=${user_name}&password=${user_password}" "${HARBOR_REGISTRY_URL}/c/login"  -o /dev/nullrl -s -w %{http_code} -k)
    done
}

check_harbor_project_auth(){
  local project_name=$1
  check_project_auth=$(curl -X GET "${HARBOR_REGISTRY_URL}/api/projects?name=${project_name}" -H "accept: application/json" -k -s | grep -w '"'${project_name}'"' | wc -l)
    if [[ ${check_project_auth} -ne 1 ]];then
        echo
        echo "The projects [${project_name}] maybe not public.You should input an account which belong it."
        waiting_input "Please input Harbor account" "" "Y"
        user_name=${GENERAL_INPUT_ANSWER}
        waiting_input "Please input password" "" "Y" "Y"
        user_password=${GENERAL_INPUT_ANSWER}
        check_harbor_user_project_auth ${user_name} ${user_password} ${project_name}
    fi
}

check_harbor_user_project_auth(){
  local user_name=$1
  local user_password=$2
  local project_name=$3
  USER_AUTHORIZATION=$(echo -n ${user_name}:${user_password} | base64)
  check_project_auth=$(curl -X GET "${HARBOR_REGISTRY_URL}/api/projects?name=${project_name}" -H "accept: application/json" -H "authorization: Basic ${USER_AUTHORIZATION}" -k -s | grep -w '"'${project_name}'"' | wc -l)
    while [[ ${check_project_auth} -ne 1 ]];do
        echo
        echo "* Password or account maybe not right,or account[${user_name}] not belong to project [${project_name}].Please retype account and password."
        waiting_input "Please input Harbor account" "" "Y"
        user_name=${GENERAL_INPUT_ANSWER}
        waiting_input "Please input password" "" "Y" "Y"
        user_password=${GENERAL_INPUT_ANSWER}
        USER_AUTHORIZATION=$(echo -n ${user_name}:${user_password} | base64)
        check_project_auth=$(curl -X GET "${HARBOR_REGISTRY_URL}/api/projects?name=${project_name}" -H "accept: application/json" -H "authorization: Basic ${USER_AUTHORIZATION}" -k -s | grep -w '"'${project_name}'"' | wc -l)
    done
    HARBOR_PROJECT_PUBLIC_ENABLED="false"
}

check_harbor_project_public_enabled(){
    local project_name=$1
    # If harbor project exists and is public,will not create secret for pull images.
    check_project_public=$(curl "${HARBOR_REGISTRY_URL}/api/projects?name=${project_name}" -H "accept: application/json" -k -s | grep -w ${project_name} | wc -l)
    if [[ ${check_project_public} == 1 ]]; then
        HARBOR_PROJECT_PUBLIC_ENABLED="true"
    fi
    # if project not exists,ask for if create public harbor project.
    check_project_exists=$(curl "${HARBOR_REGISTRY_URL}/api/projects?name=${project_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${project_name} | wc -l)
    if [[ ${check_project_exists} -lt 1 ]]; then
        waiting_input_YN  "Would you want your harbor project be public? " "N"
        public_enable_answer=${GENERAL_INPUT_ANSWER}
        if [[ ${public_enable_answer} == "Y" ]]; then
            HARBOR_PROJECT_PUBLIC_ENABLED="true"
        fi
    fi
}
# ------------------
# Init
# ------------------

init_kubernetes_cluster(){
    echo "--------------------------------------"
    echo "# Initializing Kubernetes Cluster..."
    echo
    echo "creating the cluster role ..."
    check_cluster_role_exist=$(kubectl get clusterrole | grep -w service-discovery-client | wc -l)
    if [[ "${check_cluster_role_exist}" == 0 ]]; then
        kubectl create clusterrole service-discovery-client \
        --verb=get,list,watch \
        --resource=pods,services,configmaps,endpoints
    fi
    echo
    echo "Kubernetes Cluster has been initialized."
    echo "--------------------------------------"
}

init_helm_gitlab_repo(){
    echo "--------------------------------------"
    echo "# Adding the gitlab repository of Helm."
    check_gitlab_repo_exists=$(helm repo list | grep -P 'gitlab\s*https://charts.gitlab.io'| wc -l)
    if [[ "${check_gitlab_repo_exists}" == 1 ]]; then
        echo "* \"gitlab\" has been added to your repositories."
        waiting_input_YN "Do you need to update the helm repo?" "N"
        local answer_update_helm_repo=${GENERAL_INPUT_ANSWER}
        if [[ "${answer_update_helm_repo}" == "Y" ]]; then
            echo
            echo "Updating the repo..."
            echo "Perhaps need a few minutes, please wait..."
            stty igncr
            helm repo update;
            stty -igncr
            echo "Helm repo has been updated."
            dealing_wrong_input
        fi
    else
        echo "Perhaps need a few minutes, please wait..."
        stty igncr
        check_gitlab_repo_add=$(helm repo add gitlab https://charts.gitlab.io | grep '"gitlab" has been added to your repositories' | wc -l)
        stty -igncr
        if [[ "${check_gitlab_repo_add}" -ne 1 ]]; then
            echo "Failed to add the gitlab repository, please check your Helm status.The script to end."
            exit;
        fi
        echo "\"gitlab\" has been added to your repositories."
        dealing_wrong_input
    fi
    echo "--------------------------------------"
}

init_gitlab_runner(){

    echo "# Harbor is used for gitlab runner to store images."
    check_harbor_status
    check_harbor_admin_account

    echo
    waiting_input_YN "If you use MinIO as your gitlab storage?" "Y"
    local answer_using_minio=${GENERAL_INPUT_ANSWER}
    if [[ "${answer_using_minio}" == "Y" ]]; then
        check_minio_status
    else
        echo "* You should install your gitlab runner chart with your configurations in the last step."
    fi

    echo
    waiting_input "Please input the namespace for gitlab runner to install" ${GITLAB_RUNNER_NAMESPACE}
    local gitlab_runner_namespace=${GENERAL_INPUT_ANSWER}

    local check_ns_exists=$(kubectl get ns | awk '{print $1}' | grep -P ^${gitlab_runner_namespace}\$ | wc -l)
    while [[ "${check_ns_exists}" == 0 ]]; do
        waiting_input_YN "* the namespace [${gitlab_runner_namespace}] doesn't exist,would you like to create it?" "Y"
        local answer_create_namespace=${GENERAL_INPUT_ANSWER}
        if [[ "${answer_create_namespace}" == "Y" ]]; then
            kubectl create ns ${gitlab_runner_namespace}
        else
            waiting_input "Please input the namespace for gitlab runner to install" ${GITLAB_RUNNER_NAMESPACE} "Y"
            gitlab_runner_namespace=${GENERAL_INPUT_ANSWER}
        fi
        check_ns_exists=$(kubectl get ns | grep -w ${gitlab_runner_namespace} | wc -l)
    done
    GITLAB_RUNNER_NAMESPACE=${gitlab_runner_namespace}

    echo
    echo "## Starting to install the gitlab-runner."
    echo "Fetch the chart of gitlab-runner..."
    stty igncr
    helm fetch --untar gitlab/gitlab-runner --version=0.3.0
    stty -igncr
    echo "The chart has been fetched."
    dealing_wrong_input
    echo

    echo
    waiting_input_YN "If you need to configure the Maven settings.xml?" "Y"
    local answer_maven_setting=${GENERAL_INPUT_ANSWER}

    if [[ "${answer_maven_setting}" == "Y" ]]; then
cat > dew-maven-settings.yaml <<EOF
# Please edit this yaml file with your configurations.

kind: ConfigMap
apiVersion: v1
metadata:
  name: dew-maven-settings
  namespace: ${GITLAB_RUNNER_NAMESPACE}
data:
  settings.xml: |-
     <?xml version="1.0" encoding="UTF-8"?>
     <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
         <servers>
             <!-- e.g. adding a private repository authentication,change it by yourself. -->
             <server>
                 <id>please-change-repo</id>
                 <username>please-change-username</username>
                 <password>please-change-password</password>
             </server>
         </servers>
     </settings>
EOF
        echo
        vi dew-maven-settings.yaml
    fi

    if [[ "${answer_maven_setting}" == "Y" ]]; then
        sed -i -e'/# Start the runner/i\    cat >>/home/gitlab-runner/.gitlab-runner/config.toml <<EOF\n        [[runners.kubernetes.volumes.config_map]]\n          name = \"dew-maven-settings\"\n          mount_path = \"/opt/maven\"\n    EOF' gitlab-runner/templates/configmap.yaml
    fi

    echo
    waiting_input "Please input your gitlab url" ${GITLAB_URL}
    GITLAB_URL=${GENERAL_INPUT_ANSWER}

    echo
    echo "# The name is used for the helm release name of your gitlab-runner."
    waiting_input "Please input your gitlab-runner name" ${GITLAB_RUNNER_NAME}
    GITLAB_RUNNER_NAME=${GENERAL_INPUT_ANSWER}
    check_helm_runner_name_exists=$(helm list | awk '{print $1}' | cut -d: -f1 | grep ^${GITLAB_RUNNER_NAME}$ | wc -l)
    while [[ "${check_helm_runner_name_exists}" -ge 1 ]]; do
        echo "* Error: a release named ${GITLAB_RUNNER_NAME} already exists."
        waiting_input "Please input another project name" "" "Y"
        GITLAB_RUNNER_NAME=${GENERAL_INPUT_ANSWER}
        check_helm_runner_name_exists=$(helm list | awk '{print $1}' | cut -d: -f1 | grep ^${GITLAB_RUNNER_NAME}$ | wc -l)
    done

    echo
    echo "# * The registration token for adding new Runners to the GitLab server."
    echo "# This must be retrieved from your GitLab instance."
    echo "# ref: https://docs.gitlab.com/ee/ci/runners/"
    echo "# e.g. ${GITLAB_RUNNER_REG_TOKEN}"
    waiting_input "Please input the registration token of your gitlab runner" "" "Y"
    GITLAB_RUNNER_REG_TOKEN=${GENERAL_INPUT_ANSWER}

    echo
    echo "The runner profile is used for label your runner environment,and"
    echo "it also is related with your project profile,please set it right."
    echo "# e.g. Using \"test\" to label your runner environment. "
    waiting_input "Please input your runner profile" "" "Y"
    GITLAB_RUNNER_PROFILE=${GENERAL_INPUT_ANSWER}

    echo
    echo "# Default the runner container image to use for builds when none is specified."
    echo "# Using the [${GITLAB_RUNNER_IMAGE}] for the latest feature,or "
    echo "# using it as the base image of your custom runner container image."
    waiting_input "Please Input your runner image" ${GITLAB_RUNNER_IMAGE}
    GITLAB_RUNNER_IMAGE=${GENERAL_INPUT_ANSWER}

    echo
    echo "# The DockerD url is used for dew-maven-plugin to build your project images."
    echo "# e.g. ${DOCKERD_URL}"
    waiting_input "Please input your DockerD service url" "" "Y"
    DOCKERD_URL=${GENERAL_INPUT_ANSWER}

    # The settings for helm installation.
    gitlab_runner_helm_install_settings="helm install --name ${GITLAB_RUNNER_NAME} --namespace ${GITLAB_RUNNER_NAMESPACE} gitlab-runner \\
    --set gitlabUrl=${GITLAB_URL}\\
    --set runnerRegistrationToken=${GITLAB_RUNNER_REG_TOKEN} \\
    --set concurrent=20 \\
    --set rbac.create=true \\
    --set rbac.clusterWideAccess=true \\
    --set runners.tags=${GITLAB_RUNNER_PROFILE} \\
    --set runners.image=${GITLAB_RUNNER_IMAGE} \\
    --set runners.cache.cacheType=s3 \\
    --set runners.cache.cacheShared=true \\
    --set runners.cache.s3ServerAddress=${MINIO_HOST} \\
    --set runners.cache.s3BucketName=${MINIO_BUCKET_NAME} \\
    --set runners.cache.s3CacheInsecure=true \\
    --set runners.cache.secretName=minio-access \\
    --set runners.env.dew_devops_docker_host=${DOCKERD_URL} \\
    --set runners.env.dew_devops_docker_registry_url=${HARBOR_REGISTRY_URL}/v2 \\
    --set runners.env.dew_devops_docker_registry_username=${HARBOR_REGISTRY_ADMIN} \\
    --set runners.env.dew_devops_docker_registry_password=${HARBOR_REGISTRY_ADMIN_PASSWORD} \\
    --set runners.env.dew_devops_profile=${GITLAB_RUNNER_PROFILE} \\
    --set runners.env.dew_devops_quiet=true \\
    --set runners.env.dew_devops_kube_config=${KUBERNETES_CONFIG} \\"

cat > gitlab-runner/gitlab-runner-helm-installation.sh <<EOF
#!/bin/bash

#  * If you don't want to use MinIO as your gitlab storage,you should change the values of "runners.cache".
#
# You could also edit the setting with your configurations.

${gitlab_runner_helm_install_settings}
EOF


    echo
    waiting_input_YN "If you want to add your runner custom settings of helm installation?" "N"
    local answer_helm=${GENERAL_INPUT_ANSWER}
    if [[ "${answer_helm}" == "Y" ||  "${answer_using_minio}" == "N" ]]; then
        if [[ "${answer_using_minio}" == "N" ]]; then
            echo "* Without using MinIO for gitlab runner,please edit the helm settings:"
        fi
        vi gitlab-runner/gitlab-runner-helm-installation.sh
    elif [[ "${answer_helm}" == "N" ]]; then
        echo "* Installing gitlab runner chart with the default settings."
    fi

    if [[ "${answer_maven_setting}" == "Y" ]]; then
        kubectl apply -f dew-maven-settings.yaml
    fi

    if [[ "${answer_using_minio}" == "Y" ]]; then
        echo "# Create secret for MinIO."
        check_minio_secret_exists=$(kubectl get secret -n ${GITLAB_RUNNER_NAMESPACE} |  awk '{print $1}' | cut -d: -f1 | grep ^minio-access$ | wc -l)
        if [[ "${check_minio_secret_exists}" == 1 ]]; then
            echo "The secret \"minio-access\" already exists,using the created secret."
        else
            kubectl create secret generic minio-access -n ${GITLAB_RUNNER_NAMESPACE} \
                --from-literal=accesskey=${MINIO_ACCESS_KEY} \
                --from-literal=secretkey=${MINIO_SECRET_KEY}
        fi
    fi

    echo
    sh gitlab-runner/gitlab-runner-helm-installation.sh

    check_helm_runner_name_exists=$(helm list | awk '{print $1}' | cut -d: -f1 | grep ^${GITLAB_RUNNER_NAME}$ | wc -l)
    if [[ "${check_helm_runner_name_exists}" == 0 ]]; then
        echo
        cat gitlab-runner/gitlab-runner-helm-installation.sh
        echo
        echo -e "\033[31m * ERROR: \033[1;m""Failed to install gitlab runner! Please check your settings and execute it by yourself."
        echo "The script to end."
    else
        echo
        echo "* Finished to install gitlab-runner [${GITLAB_RUNNER_NAME}]."
    fi
    echo
    exit;
}

# ------------------
# Create a project
# ------------------
project_namespace_exists_check(){
    local namespace=$1
    echo "$(kubectl get ns | awk '{print $1}'| grep -P '^'${namespace}'$' | wc -l)"
}

project_namespace_create_check(){
    local namespace=$1
    check_ns_exists=$(kubectl get ns | awk '{print $1}'| grep -P '^'${namespace}'$' | wc -l)

    while [[ "${check_ns_exists}" -gt 0 ]];do
        waiting_input_check_regex "* There is already existing the same namespace, please retype another namespace" ${namespace} "Y" ""
        namespace=${GENERAL_INPUT_ANSWER}
        check_ns_exists=$(kubectl get ns | awk '{print $1}'| grep -P '^'${namespace}'$'| wc -l)
    done
}

project_harbor_user_create_check(){
    local project_name=$1
    # Checking whether user account exists.
    check_user_exists=$(curl "${HARBOR_REGISTRY_URL}/api/users?username=${project_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${project_name} | wc -l)
    while [[ "${check_user_exists}" -gt 0 ]]; do
        echo "There is already existing the same Harbor user account with project name."
        read -p "Please input another user name to bind with your project: " -e user_name
        check_user_exists=$(curl "${HARBOR_REGISTRY_URL}/api/users?username=${user_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${user_name} | wc -l)
        DEW_HARBOR_USER_NAME=${user_name}
    done

    echo
    waiting_input_YN "If you would like to use your custom password for the project user account?" N
    local answer_custom_user_password=${GENERAL_INPUT_ANSWER}
    if [[ "${answer_custom_user_password}" == "Y" ]]; then
        read -e -s -p "Please input the password for the project user account: " user_account_password
        check_user_password=$(echo "${user_account_password}" | grep -P ${HARBOR_REGISTRY_PASSWORD_REGEX}| wc -l)
        while [[ "${check_user_password}" -eq 0 ]];do
            echo
            read -e -s -p "The password format is not right, please retype: " user_account_password
            check_user_password=$(echo "${user_account_password}" | grep -P ${HARBOR_REGISTRY_PASSWORD_REGEX}| wc -l)
        done
        DEW_HARBOR_USER_PASSWORD=${user_account_password}
    else
        echo "* Using the default password \"${DEW_HARBOR_USER_PASSWORD}\" for the project user account."
    fi
    echo

    # The e-mail format checking.
    echo
    echo "# E-mail is used for binding the Harbor user account that you created above."
    local emailRegex="^([a-zA-Z0-9_-]+)@([a-zA-Z0-9_-]+)\.([a-zA-Z]{2,5})$"
    waiting_input_check_regex "Please input the e-mail for your Harbor project user account" "${emailRegex}" "Y" "" "${DEW_HARBOR_USER_EMAIL}"
    local user_email=${GENERAL_INPUT_ANSWER}

    # Checking whether e-mail is registered.
    local check_email_exists=$(curl "${HARBOR_REGISTRY_URL}/api/users?email=${user_email}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${user_email} | wc -l)
    while [[ "${check_email_exists}" -gt 0 ]];do
        waiting_input_check_regex "The e-mail is already registered, please retype another" "${emailRegex}" "Y" "" "${DEW_HARBOR_USER_EMAIL}"
        user_email=${GENERAL_INPUT_ANSWER}
        check_email_exists=$(curl "${HARBOR_REGISTRY_URL}/api/users?email=${user_email}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${user_email} | wc -l)
    done
    DEW_HARBOR_USER_EMAIL=${user_email}
}

project_create_check(){
    echo "Tips: Before creating your project, you need to initialize your Kubernetes cluster."
    echo
    check_harbor_admin_account

    echo
    echo

    echo "# The [profile] is used for creating the Kubernetes namespace prefix."
    echo "# The name of [project] would be used for creating Harbor project, Harbor user account."
    echo "# If [profile] is null,the value of [project] will be used for Kubernetes namespace;"
    echo "# Conversely,the namespace will be <profile>-<project> "
    echo "# Project name must consist of lower case alphanumeric characters or '-' "
    echo "# and must start and end with an alphanumeric character,"
    echo "# and the length must be greater than two."

    local project_name_regex="^([a-z0-9]+-?[a-z0-9]+)+$"
    local profile_regex="^([a-z0-9]?-?[a-z0-9]+)+$"
    read -e -p "Please input profile: " profile
    if [[ ${profile} != "" ]]; then
        while [[ ${profile} != "" && ! "${profile}" =~ ${profile_regex} ]]; do
            read -e -p "* The format of [profile] is not right,please retype:" profile
        done
    fi

    waiting_input_check_regex "Please input project name" ${project_name_regex} "Y" ""
    local project_name=${GENERAL_INPUT_ANSWER}

    # Checking whether Kubernetes namespace exists.
    if [[ ${profile} == "" ]]; then
        PROJECT_NAMESPACE=${project_name}
    else
        PROJECT_NAMESPACE=${profile}-${project_name}
    fi
    check_ns_exists=$(project_namespace_exists_check ${PROJECT_NAMESPACE})
    if [[ ${check_ns_exists} -gt 0 ]]; then
        echo "Namespace [${PROJECT_NAMESPACE}] already exists."
        waiting_input_YN "Would you like to update the imagePullSecret for [${PROJECT_NAMESPACE}] namespace ?" "N"
           local answer_update_secret=${GENERAL_INPUT_ANSWER}
         if [[ ${answer_update_secret} == "N" ]]; then
             echo "The script finished."
             exit;
         fi
         CREATE_PROJECT_NAMESPACE_SKIP="true"
    fi
    check_harbor_project_public_enabled ${project_name}
    # Checking whether Harbor project name exists.
    check_project_exists=$(curl "${HARBOR_REGISTRY_URL}/api/projects?name=${project_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${project_name} | wc -l)
    if  [[ "${check_project_exists}" -gt 0 ]];then
        echo "Harbor project [${project_name}] already exists."
        check_harbor_project_auth ${project_name}
        NEED_CREATE_PROJECT_USER="false"
    else
        DEW_HARBOR_USER_NAME=${project_name}
        DEW_HARBOR_USER_EMAIL=${DEW_HARBOR_USER_NAME}@dew.idealworld.group
        project_harbor_user_create_check ${project_name}
        NEED_CREATE_PROJECT_USER="true"
    fi

}

project_create(){

    if [[ ${NEED_CREATE_PROJECT_USER} == "true" ]]; then
        if [[ ${HARBOR_PROJECT_PUBLIC_ENABLED} == "false" ]]; then
            echo
            echo "# Starting to create the Harbor user account."
            ADMIN_AUTHORIZATION=$(echo -n ${HARBOR_REGISTRY_ADMIN}:${HARBOR_REGISTRY_ADMIN_PASSWORD} | base64)
            create_user_result=$(curl -X POST "${HARBOR_REGISTRY_URL}/api/users" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -H "Content-Type: application/json" -d "{ \"email\": \"${DEW_HARBOR_USER_EMAIL}\", \"username\": \"${DEW_HARBOR_USER_NAME}\", \"password\": \"${DEW_HARBOR_USER_PASSWORD}\", \"realname\": \"${DEW_HARBOR_USER_NAME}\", \"comment\": \"init\"}" -o /dev/nullrl -s -w %{http_code} -k)
            if [[ "${create_user_result}" -ne 201 ]]; then
                echo "Failed to create user account, the script to end, please retry it later."
                exit;
            fi
            echo "Created Harbor user account [${DEW_HARBOR_USER_NAME}] successfully.The password is ${DEW_HARBOR_USER_PASSWORD}."
            USER_AUTHORIZATION=$(echo -n ${DEW_HARBOR_USER_NAME}:${DEW_HARBOR_USER_PASSWORD} | base64)
        else
            USER_AUTHORIZATION=${ADMIN_AUTHORIZATION}
        fi

        echo
        echo "# Starting to create Harbor project."
        create_project_result_code=$(curl -X POST "${HARBOR_REGISTRY_URL}/api/projects" -H "accept: application/json" -H "authorization: Basic ${USER_AUTHORIZATION}" -H "Content-Type: application/json" -d "{ \"project_name\": \"${DEW_HARBOR_USER_NAME}\",\"metadata\":{\"public\":\"${HARBOR_PROJECT_PUBLIC_ENABLED}\"}}" -o /dev/nullrl -s -w %{http_code} -k)
        if [[ "${create_project_result_code}" -ne 201 ]]; then
            echo "Failed to create project, the script to end, please retry it later."
            exit;
        fi
        echo "The project [${PROJECT_NAMESPACE}] is created successfully."
    fi
    echo
    if [[ ${CREATE_PROJECT_NAMESPACE_SKIP} == "false" ]]; then
        echo "# Starting to initialize the project in the Kubernetes Cluster."
        kubectl create namespace ${PROJECT_NAMESPACE}
    fi

    check_rolebinding_exists=$(kubectl get rolebinding -n ${PROJECT_NAMESPACE} | awk '{print $1}'| grep -P '^'default:service-discovery-client'$' | wc -l)
    if [[ ${check_rolebinding_exists} != 1 ]]; then
        kubectl create rolebinding default:service-discovery-client \
            -n ${PROJECT_NAMESPACE} \
            --clusterrole service-discovery-client \
            --serviceaccount ${PROJECT_NAMESPACE}:default
    fi

    if [[ ${HARBOR_PROJECT_PUBLIC_ENABLED} == "false" ]]; then
        check_secret_exists=$(kubectl get secret -n ${PROJECT_NAMESPACE} | awk '{print $1}'| grep -P '^'dew-registry'$' | wc -l)
        if [[ ${check_secret_exists} == 1 ]]; then
          kubectl delete secret -n ${PROJECT_NAMESPACE} dew-registry
        fi
        kubectl -n ${PROJECT_NAMESPACE} create secret docker-registry dew-registry \
            --docker-server=${HARBOR_REGISTRY_URL} \
            --docker-username=${DEW_HARBOR_USER_NAME} \
            --docker-password=${DEW_HARBOR_USER_PASSWORD}

        kubectl -n ${PROJECT_NAMESPACE} patch serviceaccount default \
            -p '{"imagePullSecrets": [{"name": "dew-registry"}]}'
    fi

    waiting_input_YN "If you want to create the Ingress for your project?" "Y"
    local answer_create_ingress=${GENERAL_INPUT_ANSWER}
    if [[ "${answer_create_ingress}" == "Y" ]]; then
        project_ingress_create
        echo
        local check_ingress_exist=$(kubectl get ing dew-ingress -n ${PROJECT_NAMESPACE} | wc -l)
        if [[ "${check_ingress_exist}" -eq 0 ]]; then
            echo -e "\033[31m * Failed to created Ingress, the script to end. Please create the Ingress yourself. \033[1;m"
            exit;
        fi
    fi

    echo "Finished to create project [${PROJECT_NAMESPACE}]."
    exit;
}

project_ingress_create(){
    # Creating Ingress
    # More details: https://kubernetes.io/docs/concepts/services-networking/ingress/
    #######################
    # apiVersion: extensions/v1beta1
    # kind: Ingress
    # metadata:
    #   annotations:
    #     # For more details: https://kubernetes.github.io/ingress-nginx/user-guide/nginx-configuration/annotations/
    #     # The example: https://github.com/kubernetes/ingress-nginx/tree/master/docs/examples/rewrite
    #     nginx.ingress.kubernetes.io/rewrite-target: /\$1
    #   name: dew-ingress
    #   namespace: $PROJECT_NAMESPACE
    # spec:
    #   rules:
    #     # Your custom rules.
    #######################
    echo
    echo "# Starting to create Ingress in the Kubernetes Cluster."

    echo
    echo "# The Nginx rewrite target is used for the annotation of Ingress."
    echo "# See example: https://github.com/kubernetes/ingress-nginx/tree/master/docs/examples/rewrite"
    echo "# All of the nginx ingress annotations: https://kubernetes.github.io/ingress-nginx/user-guide/nginx-configuration/annotations/"
    echo "# You can edit the Ingress freely using below cmd after the script to end."
    echo "# $ kubectl edit ingress dew-ingress -n ${PROJECT_NAMESPACE}"
    echo
    read -e -p "Please input Nginx rewrite target(not required):" nginx_rewrite_target

    echo
    echo "# Ingress backend host params is used for custom the host of Kubernetes Ingress rules."
    echo "# The params should accord with the DNS label."
    echo "# Service name must consist of lower case alphanumeric characters or '-', "
    echo "# start with an alphabetic character, and end with an alphanumeric character."
    echo "# Using the space to separate your host url, Kubernetes service name and service port."
    echo "# [your.host/path service-name:service-port]"
    echo "# e.g.  ${INGRESS_HOST_EXAMPLE}"
    echo "* You could add another group of Ingress backend host params after Input."
    echo

    local backend_str_regex="^[a-z0-9-_.]+/?.+\s[a-z0-9-]+:{1}[a-z0-9]+$"

    check_regex_match_with_grep "Please input Ingress backend host params" ${backend_str_regex} "Ingress backend host params"
    deal_ingress_backend_yaml "${GENERAL_INPUT_ANSWER}"
    local ingress_ingress_host_yaml=${INGRESS_HOST_YAML_VALUE}

    waiting_input_YN "Would you like add another group Ingress host?" "N"
    answer_add_ingress_host=${GENERAL_INPUT_ANSWER}
    while [[ ${answer_add_ingress_host} == "Y" ]]; do
        check_regex_match_with_grep "Please input a group of your Ingress backend host params" ${backend_str_regex} "Ingress backend host params"
        deal_ingress_backend_yaml "${GENERAL_INPUT_ANSWER}"
        ingress_ingress_host_yaml+=${INGRESS_HOST_YAML_VALUE}
        waiting_input_YN "Would you like add another group Ingress host?" "N"
        answer_add_ingress_host=${GENERAL_INPUT_ANSWER}
    done

    # Creating Ingress
    cat <<EOF | kubectl -n ${PROJECT_NAMESPACE} apply -f -
    apiVersion: extensions/v1beta1
    kind: Ingress
    metadata:
      annotations:
        nginx.ingress.kubernetes.io/rewrite-target: /${nginx_rewrite_target}
      name: dew-ingress
    spec:
      rules:
${ingress_ingress_host_yaml}
EOF
}


# ------------------
# Select an option
# ------------------
echo ""
echo "=================== Dew DevOps Script ==================="
echo ""

PS3='Choose your option: '

select option in "Init cluster" "Install Gitlab runner" "Create a project"

do
    case ${option} in
     'Init cluster')
      echo "========== Init cluster =========="
      echo "# * Create the cluster role for service discovery"
      check_kubernetes_env
      init_kubernetes_cluster
      echo "=================================="
      break;;
       'Install Gitlab runner')
      echo "====== Install Gitlab runner ======"
      echo "# * Install the gitlab runner"
      check_kubernetes_env
      init_helm_gitlab_repo
      init_gitlab_runner
      echo "=================================="
      break;;
     'Create a project')
      echo "======== Create a Project ========"
      check_kubernetes_env
      check_harbor_status
      project_create_check
      project_create
      echo "=================================="
      break;;
    esac
done
