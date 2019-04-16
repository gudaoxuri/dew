#!/bin/bash
#set -e
#
# Copyright 2019. the original author or authors.
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
# ======================================================

HARBOR_REGISTRY_HOST=harbor.dew.ms
HARBOR_REGISTRY_ADMIN=admin
HARBOR_REGISTRY_ADMIN_PASSWORD=Harbor12345
HARBOR_REGISTRY_PASSWORD_REGEX="(?=^.{8,20}$)(?=^[^\s]*$)(?=.*\d)(?=.*[A-Z])(?=.*[a-z])"
PROJECT_NAMESPACE=devops-example
DEW_HARBOR_USER_NAME=${PROJECT_NAMESPACE}
DEW_HARBOR_USER_PASS=Dew\!123456
DEW_HARBOR_USER_EMAIL=${DEW_HARBOR_USER_NAME}@dew.ms

KUBE_CONFIG=`echo $(cat ~/.kube/config | base64) | tr -d " "`

MINIO_HOST="minio.dew.ms:9000"
MINIO_ACCESS_KEY="dew"
MINIO_SECRET_KEY="Dew123456"
MINIO_BUCKET_NAME="dew"

GITLAB_URL="http://gitlab.dew.ms"
GITLAB_RUNNER_NAMESPACE="default"
GITLAB_RUNNER_NAME="dew-runner"
GITLAB_RUNNER_IMAGE="ubuntu:16.04"
GITLAB_RUNNER_REG_TOKEN=3mezus8cX9qAjkrNY4B
GITLAB_RUNNER_PROFILE=test


# ------------------
# Params dealing
# ------------------

answer_check(){
    case $2 in
    Y | y)
          echo "The answer is \"Yes\", continue.";;
    N | n)
          echo "The answer is \"No\", skip this step.";;
    *)
          echo "* The other value was entered, using the default answer \"$1\".";;
    esac
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

press_enter_continue(){
    echo
    read -s -p "* Press Enter to continue." enter
    echo
}


# ------------------
# Init
# ------------------


init_env_check(){
    echo "--------------------------------------"
    echo "# Checking Kubernetes cluster status."
    node_ready_check=`kubectl get node | grep -w Ready | wc -l`

    node_not_ready_check=`kubectl get node | grep -w NotReady | wc -l`
    node_x509_check=`kubectl get node | grep x509 | wc -l`
    if [[ "${node_not_ready_check}" -gt 0 || "${node_x509_check}" -gt 0 ]] ; then
        echo "kubectl get node"
        echo "`kubectl get node`"
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


add_helm_gitlab_repo(){
    echo "--------------------------------------"
    echo "# Adding the gitlab repository of Helm."
    check_gitlab_repo_exists=`helm repo list | grep -P 'gitlab\s*https://charts.gitlab.io'| wc -l`
    if [[ "${check_gitlab_repo_exists}" == 1 ]]; then
        echo "* \"gitlab\" has been added to your repositories."
        read -e -n1 -p "Do you need to update the helm repo? [Y/N]" answer_update_helm_repo
        while [[ "${answer_update_helm_repo}" == "" ]]; do
            read -e -n1 -p "Please answer [Y/N]:" answer_update_helm_repo
        done
        answer_check N ${answer_update_helm_repo}
        if [[ "${answer_update_helm_repo}" != "Y" && "${answer_update_helm_repo}" != "y" ]]; then
            answer_update_helm_repo="N"
        fi
        if [[ "${answer_update_helm_repo}" == "Y" || "${answer_update_helm_repo}" == "y" ]]; then
            echo
            echo "Updating the repo..."
            echo "Perhaps need a few minutes, please wait..."
            stty igncr
            helm repo update
            stty -igncr
            echo "Helm repo has been updated."
            press_enter_continue
        fi
    else
        stty igncr
        check_gitlab_repo_add=`helm repo add gitlab https://charts.gitlab.io | grep '"gitlab" has been added to your repositories' | wc -l`
        stty -igncr
        if [[ "${check_gitlab_repo_add}" -ne 1 ]]; then
            echo "Failed to add the gitlab repository, please check your Helm status.The script to end."
            exit;
        fi
        echo "\"gitlab\" has been added to your repositories."
        press_enter_continue
    fi
    echo "--------------------------------------"
}


minIO_status_check(){
    echo "--------------------------------------"
    echo "# Checking MinIO status."

    echo "MinIO address, e.g. ${MINIO_HOST}"
    read -e -p "Please input your MinIO address: " minIO_host
    if [[ "${minIO_host}" == "" ]]; then
        echo "* No MinIO address was entered, using the default MinIO address："
        echo "${MINIO_HOST}"
        minIO_host=${MINIO_HOST}
    fi
    MINIO_HOST=${minIO_host}

    check_minIo_status=`curl ${MINIO_HOST} -o /dev/nullrl -s -w %{http_code} `
    if [[ "${check_minIo_status}" != 403 ]]; then
        echo
        echo "MinIO was not able to be accessed.Please check your MinIO environment."
        echo "The script to end."
        exit;
    fi
    echo
    echo "MinIO is ready."
    echo

    echo "# Checking the access key and secret key."
    read -e -p "Please input your MinIO access key: " minio_access_key
    if [[ "${minio_access_key}" == "" ]]; then
        echo "* No access key was entered,using the default value \"${MINIO_ACCESS_KEY}\"."
        minio_access_key=${MINIO_ACCESS_KEY}
    fi
    read -e -s -p "Please input your MinIO secret key: " nimio_secret_key
    if [[ "${nimio_secret_key}" == "" ]]; then
        echo
        echo "* No secret key was entered,using the default value \"${MINIO_SECRET_KEY}\"."
        nimio_secret_key=${MINIO_SECRET_KEY}
    fi
    echo

    MINIO_ACCESS_KEY=${minio_access_key}
    MINIO_SECRET_KEY=${nimio_secret_key}

    minio_key_check=`curl -X POST "${MINIO_HOST}/minio/webrpc" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"id\":1,\"jsonrpc\":\"2.0\",\"params\":{\"username\":\"${MINIO_ACCESS_KEY}\",\"password\":\"${MINIO_SECRET_KEY}\"},\"method\":\"Web.Login\"}}" -s -k`

    while [[ "${minio_key_check}" =~ error ]]; do
        echo
        echo "* "`get_json_value "${minio_key_check}" message`
        read -e -p "Please input the right access key: " minio_access_key
        read -e -s -p "Please input the right secret key: " nimio_secret_key
        MINIO_ACCESS_KEY=${minio_access_key}
        MINIO_SECRET_KEY=${nimio_secret_key}

        minio_key_check=`curl -X POST "${MINIO_HOST}/minio/webrpc" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"id\":1,\"jsonrpc\":\"2.0\",\"params\":{\"username\":\"${MINIO_ACCESS_KEY}\",\"password\":\"${MINIO_SECRET_KEY}\"},\"method\":\"Web.Login\"}}" -s -k`
    done
    echo "The access key and secret key are right."

    echo
    read -e -p "# Please input your MinIO bucket name for gitlab storage: " minio_bucket_name
    if [[ "${minio_bucket_name}" == "" ]]; then
        echo "* No value was entered, using the default value \"${MINIO_BUCKET_NAME}\"."
        minio_bucket_name=${MINIO_BUCKET_NAME}
    fi
       MINIO_BUCKET_NAME=${minio_bucket_name}

    echo
    echo "--------------------------------------"
}

init_cluster(){
    echo "--------------------------------------"
    echo "# Initializing Kubernetes Cluster, creating the cluster role for service discovery."
    check_cluster_role_exist=`kubectl get clusterrole | grep -w service-discovery-client | wc -l`
    if [[ "${check_cluster_role_exist}" == 0 ]]; then
        kubectl create clusterrole service-discovery-client \
        --verb=get,list,watch \
        --resource=pods,services,configmaps,endpoints
    fi
    add_helm_gitlab_repo
    install_gitlab_runner_project
    echo
    echo "Kubernetes Cluster has been initialized."
    echo "--------------------------------------"
    exit;
}


# ------------------
# Create a project
# ------------------

harbor_status_check(){
    echo "------------------------------------"
    echo "## Checking Harbor status..."
    echo
    echo "# e.g. ${HARBOR_REGISTRY_HOST} or https://${HARBOR_REGISTRY_HOST} "
    read -e -p "Please input Harbor registry host： " registry_host
    if [[ "${registry_host}" != "" ]]; then
        HARBOR_REGISTRY_HOST=${registry_host}
    else
        echo "* No Harbor registry host was entered, using the default Harbor registry host："
        echo "* ${HARBOR_REGISTRY_HOST}"
    fi
    echo "* The default scheme is https."
    harbor_registry_health_check="curl ${HARBOR_REGISTRY_HOST}/health -k"
    registry_status=`curl ${HARBOR_REGISTRY_HOST}/health -o /dev/nullrl -s -w %{http_code} -k`
    if [[ "${registry_status}" -ne 200 ]]; then
        echo
        echo ${harbor_registry_health_check}
        echo "`${harbor_registry_health_check}`"
        echo "Harbor was not able to be accessed.Please check your Harbor environment."
        echo "The script to end."
        echo
        exit;
    fi
    echo
    echo "Harbor is ready."
    echo "------------------------------------"
}

project_create_check(){
    echo "Tips: Before creating your project, you need to initialize your Kubernetes cluster for service discovery."
    echo "____________________________________"
    echo
    read -e -p "Please input your Harbor registry admin account: " registry_admin
    if [[ "${registry_admin}" == "" ]]; then
        echo "* No Harbor registry admin account was entered, using the default admin account: ${HARBOR_REGISTRY_ADMIN}"
    fi

    echo

    echo "Please input your Harbor registry admin account password."
    echo "# The password should have the length between 8 and 20,"
    echo "# and contain an uppercase letter, a lowercase letter and a number."
    read -p "Input your Harbor admin password: " -e -s registry_password
    echo

    regex_password=${HARBOR_REGISTRY_PASSWORD_REGEX}
    check_password=`echo "${registry_password}" | grep -P ${regex_password}| wc -l`
    while [[ "${check_password}" -eq 0 ]];do
        echo
        read -e -s -p "The password format is not right, please retype: " registry_password
        check_password=`echo "${registry_password}" | grep -P ${regex_password}| wc -l`
    done

    if [[ "${registry_admin}" == "" ]]; then
        registry_admin=${HARBOR_REGISTRY_ADMIN}
    fi

    echo
    ADMIN_AUTHORIZATION=`echo -n ${registry_admin}:${registry_password} | base64`
    check_admin_status=`curl "${HARBOR_REGISTRY_HOST}/api/users" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -o /dev/nullrl -s -w %{http_code} -k`
    while [[ ${check_admin_status} -eq 401 || ${check_admin_status} -eq 403 ]];do
        echo
        echo "* Password or admin account maybe not right,or the account not admin.Please retype admin account and password."
        read -e -p "Please input Harbor registry admin account: " registry_admin
        read -e -s -p "Please input password: " registry_password
        ADMIN_AUTHORIZATION=`echo -n ${registry_admin}:${registry_password} | base64`
        check_admin_status=`curl "${HARBOR_REGISTRY_HOST}/api/users" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -o /dev/nullrl -s -w %{http_code} -k`
    done

    if [[ "${registry_admin}" != "" ]]; then
        HARBOR_REGISTRY_ADMIN=${registry_admin}
    fi
    if [[ "${registry_password}" != "" ]]; then
        HARBOR_REGISTRY_ADMIN_PASSWORD=${registry_password}
    fi
    echo

    echo
    echo "# The name of project would be used for creating Harbor project, Harbor user account and Kubernetes namespace."
    echo "# Project name must consist of lower case alphanumeric characters or '-' "
    echo "# and must start and end with an alphanumeric character,"
    echo "# and the length must be greater than two."
    read -e -p "Please input project name: " project_name

    project_name_regex="^([a-z0-9]+-?[a-z0-9]+)+$"
    while [[ ! "${project_name}" =~ ${project_name_regex} ]]; do
        read -e -p "Please input the right project name: " project_name
    done

    # Checking whether Kubernetes namespace exists.
    check_ns_exists=`kubectl get ns | grep -w ${project_name} | wc -l`
    while [[ "${check_ns_exists}" -gt 0 ]];do
        read -e -p "There is already existing the same namespace, please retype another project name: " project_name

        while [[ ! "${project_name}" =~ ${project_name_regex} ]]; do
            read -p "The project name format is not right,please retype: " -e project_name
        done

        check_ns_exists=`kubectl get ns | grep -w ${project_name} | wc -l`
    done
    # Checking whether Harbor project name exists.
    check_project_exists=`curl "${HARBOR_REGISTRY_HOST}/api/projects?name=${project_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${project_name} | wc -l`
    while [[ "${check_project_exists}" -gt 0 ]];do
        read -p "The project name already exists, please retype again: " -e project_name

        while [[ ! "${project_name}" =~ ${project_name_regex} ]]; do
            read -p "The project name format is not right,please retype again: " -e project_name
        done

        check_ns_exists=`kubectl get ns | grep -w ${project_name} | wc -l`
        while [[ "${check_ns_exists}" -gt 0 ]];do
            read -e -p "There is already existing the same namespace, please retype another project name: " project_name
            while [[ ! "${project_name}" =~ ${project_name_regex} ]]; do
                read -p "The project name format is not right,please retype again: " -e project_name
            done
            check_ns_exists=`kubectl get ns | grep -w ${project_name} | wc -l`
        done

        check_project_exists=`curl "${HARBOR_REGISTRY_HOST}/api/projects?name=${project_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${project_name} | wc -l`
    done

    if [[ "${project_name}" != "" ]]; then
        PROJECT_NAMESPACE=${project_name}
        DEW_HARBOR_USER_NAME=${PROJECT_NAMESPACE}
        DEW_HARBOR_USER_EMAIL=${DEW_HARBOR_USER_NAME}@dew.ms
    fi

    # Checking whether user account exists.
    check_user_exists=`curl "${HARBOR_REGISTRY_HOST}/api/users?username=${project_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${project_name} | wc -l`
    while [[ "${check_user_exists}" -gt 0 ]]; do
        read -p "There is already existing the same Harbor user account with project name.Please input another user name to bind with your project: " -e user_name
        check_user_exists=`curl "${HARBOR_REGISTRY_HOST}/api/users?username=${user_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${user_name} | wc -l`
        DEW_HARBOR_USER_NAME=${user_name}
    done
    echo

    read -e -n1 -p "If you would like to use your custom password for the project user account? [Y/N]" answer_custom_user_password
    while [[ "${answer_custom_user_password}" == "" ]]; do
        read -e -n1 -p "Please answer [Y/N]: " answer_custom_user_password
    done
    answer_check N ${answer_custom_user_password}
    if [[ "${answer_custom_user_password}" != "Y" && "${answer_custom_user_password}" != "y" ]]; then
        answer_custom_user_password="N"
    fi
    if [[ "${answer_custom_user_password}" == "Y" || "${answer_custom_user_password}" == "y" ]]; then
        read -e -s -p "Please input the password for the project user account: " user_account_password
        check_user_password=`echo "${user_account_password}" | grep -P ${regex_password}| wc -l`
        while [[ "${check_user_password}" -eq 0 ]];do
            echo
            read -e -s -p "The password format is not right, please retype: " user_account_password
            check_user_password=`echo "${user_account_password}" | grep -P ${regex_password}| wc -l`
        done
        DEW_HARBOR_USER_PASS=${user_account_password}
    else
        echo "* Using the default password \"${DEW_HARBOR_USER_PASS}\" for the project user account."
    fi
    echo

    # The e-mail format checking.
    echo
    echo "# E-mail is used for binding the Harbor user account that you created above."
    emailRegex="^([a-zA-Z0-9_\-\.\+]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5})$"
    read -e -p "Please input the e-mail for your Harbor project user account: " user_email
    while [[ ! "${user_email}" =~ ${emailRegex} ]]; do
    read -e -p "The e-mail format is not right, please retype again: " user_email
    done

    # Checking whether e-mail is registered.
    check_email_exists=`curl "${HARBOR_REGISTRY_HOST}/api/users?email=${user_email}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${user_email} | wc -l`
    while [[ "${check_email_exists}" -gt 0 ]];do
        read -p "The e-mail is already registered, please retype another: " -e user_email
        while [[ ! "${user_email}" =~ ${emailRegex} ]]; do
            read -p "The e-mail format is not right, please retype again:" -e user_email
        done
        check_email_exists=`curl "${HARBOR_REGISTRY_HOST}/api/users?email=${user_email}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${user_email} | wc -l`
    done

    if [[ "${user_email}" != "" ]]; then
        DEW_HARBOR_USER_EMAIL=${user_email}
    fi
}


project_create(){
    echo
    echo "# Starting to create the Harbor user account."
    ADMIN_AUTHORIZATION=`echo -n ${HARBOR_REGISTRY_ADMIN}:${HARBOR_REGISTRY_ADMIN_PASSWORD} | base64`

    create_user_result=`curl -X POST "${HARBOR_REGISTRY_HOST}/api/users" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -H "Content-Type: application/json" -d "{ \"email\": \"${DEW_HARBOR_USER_EMAIL}\", \"username\": \"${DEW_HARBOR_USER_NAME}\", \"password\": \"${DEW_HARBOR_USER_PASS}\", \"realname\": \"${DEW_HARBOR_USER_NAME}\", \"comment\": \"init\"}" -o /dev/nullrl -s -w %{http_code} -k`
    if [[ "${create_user_result}" -ne 201 ]]; then
        echo "Failed to create user account, the script to end, please retry it later."
        exit;
    fi

    echo "Created Harbor user account [${DEW_HARBOR_USER_NAME}] successfully.The password is ${DEW_HARBOR_USER_PASS}."
    echo

    echo "# Starting to create Harbor project."
    USER_AUTHORIZATION=`echo -n ${DEW_HARBOR_USER_NAME}:${DEW_HARBOR_USER_PASS} | base64`

    create_project_result_code=`curl -X POST "${HARBOR_REGISTRY_HOST}/api/projects" -H "accept: application/json" -H "authorization: Basic ${USER_AUTHORIZATION}" -H "Content-Type: application/json" -d "{ \"project_name\": \"${PROJECT_NAMESPACE}\"}" -o /dev/nullrl -s -w %{http_code} -k`
    if [[ "${create_project_result_code}" -ne 201 ]]; then
        echo "Failed to create project, the script to end, please retry it later."
        exit;
    fi
    echo "The project [${PROJECT_NAMESPACE}] is created successfully."
    echo

    echo "# Starting to initialize the project in the Kubernetes Cluster."
    kubectl create namespace ${PROJECT_NAMESPACE}

    kubectl create rolebinding default:service-discovery-client \
        -n ${PROJECT_NAMESPACE} \
        --clusterrole service-discovery-client \
        --serviceaccount ${PROJECT_NAMESPACE}:default

    kubectl -n ${PROJECT_NAMESPACE} create secret docker-registry dew-registry \
        --docker-server=${HARBOR_REGISTRY_HOST} \
        --docker-username=${DEW_HARBOR_USER_NAME} \
        --docker-password=${DEW_HARBOR_USER_PASS} \
        --docker-email=${DEW_HARBOR_USER_EMAIL}

    kubectl -n ${PROJECT_NAMESPACE} patch serviceaccount default \
        -p '{"imagePullSecrets": [{"name": "dew-registry"}]}'

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
    echo "# The nginx rewrite target is used for the annotation of Ingress."
    echo "# See example: https://github.com/kubernetes/ingress-nginx/tree/master/docs/examples/rewrite"
    echo "# All of the nginx ingress annotations: https://kubernetes.github.io/ingress-nginx/user-guide/nginx-configuration/annotations/"
    echo "# You can edit the ingress freely after the script to end."
    echo "# kubectl edit ingress dew-ingress -n ${PROJECT_NAMESPACE}"
    echo
    read -e -p "Please input nginx rewrite target:" nginx_rewrite_target

    read -e -p "Input the host of your backend: " backend_host
    while [[ ! "${backend_host}" =~ ^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*$ ]] ;do
        read -e -p "The host is not right,please retype: " backend_host
    done

    echo
    echo "# Please input your backend service name,service port and path in order."
    echo "# If you have one more service group, please user the space to separate."
    echo "# The service params should accord with the DNS label."
    echo "# Service name  must consist of lower case alphanumeric characters or '-', "
    echo "# start with an alphabetic character, and end with an alphanumeric character."
    echo "# e.g. servicea 8080 api serviceb 8081 rest servicec 8090 manage"
    echo
    read -e -p "Please input your backend services: " backend_service
    backend_services=(${backend_service})

    while [[ "${#backend_services[@]}"%3 -eq 1  || "${#backend_services[@]}" -eq 0 ]]; do
        read -e -p "Service port or name is indispensable, please retype your service params: " backend_service
        backend_services=(${backend_service})
    done

    b=0
    backend_yaml_values=""
    while [[ "${b}" -lt "${#backend_services[@]}" ]]; do
        while [[ ! "${backend_services[b]}" =~ ^[a-z]([a-z0-9]*)(-[a-z0-9]+)*$ ]]; do
            echo "The service name ["${backend_services[b]}"] format is not right，please retype another: "
            read -e service_name
            backend_services[b]=${service_name}
        done

        while [[ ! "${backend_services[${b}+1]}" =~ ^[a-z0-9]*$ ]]; do
            echo "The service port ["${backend_services[${b}+1]}"] format is not right，please retype another: "
            read -e service_port
            backend_services[${b}+1]=${service_port}
        done

        yaml_value="            - backend:
                serviceName: ${backend_services[b]}
                servicePort: ${backend_services[${b}+1]}
              path: ${backend_services[${b}+2]}
"
        let b=b+3
        backend_yaml_values+=${yaml_value}
    done

    echo
    read -e -p "Please input your frontend host: " frontend_host
    while [[ ! "${frontend_host}" =~ ^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*$ ]] ;do
        read -p "The host is not right, please retype again: " -e frontend_host
    done

    echo
    echo "# Please input your frontend service name,service port and path in order."
    echo "# If you have one more service groups, please user the space to separate."
    echo "# The service params should accord with the DNS label."
    echo "# Service name must consist of lower case alphanumeric characters or '-', "
    echo "# start with an alphabetic character, and end with an alphanumeric character."
    echo "# e.g. servicea 8080 api serviceb 8081 rest servicec 8090 manage"
    echo
    read -e -p "Please input your services: " frontend_service
    frontend_services=(${frontend_service})

    while [[ "${#frontend_services[@]}"%3 -eq 1  || "${#frontend_services[@]}" -eq 0 ]]; do
        read -p "The service port or name is indispensable, please retype your service params: " -e frontend_service
        frontend_services=(${frontend_service})
    done

    f=0
    frontend_yaml_values=""
    while [[ "${f}" -lt "${#frontend_services[@]}" ]]; do
        while [[ ! ${frontend_services[f]} =~ ^[a-z]([a-z0-9]*)(-[a-z0-9]+)*$ ]]; do
            echo "The service name ["${frontend_services[f]}"] format is not right, please retype another: "
            read -e service_name
            frontend_services[f]=${service_name}
        done
        while [[ ! "${frontend_services[${f}+1]}" =~ ^[a-z0-9]*$ ]]; do
            echo "Service Port ["${frontend_services[${f}+1]}"] format is not right, please retype another: "
            read -e service_port
            frontend_services[${f}+1]=${service_port}
        done
        yaml_value="            - backend:
                serviceName: ${frontend_services[f]}
                servicePort: ${frontend_services[${f}+1]}
              path: ${frontend_services[${f}+2]}
"
        let f=f+3
        frontend_yaml_values+=${yaml_value}
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
      - host: ${backend_host}
        http:
          paths:
${backend_yaml_values}
      - host: ${frontend_host}
        http:
          paths:
${frontend_yaml_values}
EOF

    echo
    check_ingress_exist=`kubectl get ing dew-ingress -n ${PROJECT_NAMESPACE} | wc -l`
    if [[ "${check_ingress_exist}" -eq 0 ]]; then
        echo -e "\033[31m * Failed to created Ingress, the script to end. Please the Ingress yourself. \033[1;m"
        exit;
    else
        echo "The creating of project [${PROJECT_NAMESPACE}] is completed."
        echo "The script to end."
        exit;
    fi

}


install_gitlab_runner_project(){

    echo
    read -e -p "# Please input the namespace for gitlab runner to install: " gitlab_runner_namespace

    if [[ "${gitlab_runner_namespace}" == "" ]]; then
        echo "* No namespace for gitlab runner was entered, using the \"${GITLAB_RUNNER_NAMESPACE}\" namespace."
        gitlab_runner_namespace="${GITLAB_RUNNER_NAMESPACE}"
    fi

    check_ns_exists=`kubectl get ns | grep -w ${gitlab_runner_namespace} | wc -l`
    while [[ "${check_ns_exists}" == 0 ]]; do
        read -e -n1 -p "* the namespace [${gitlab_runner_namespace}] doesn't exist,would you like to create it? [Y/N]: " answer_create_namespace
        while [[ "${answer_create_namespace}" == "" ]]; do
            read -e -n1 -p "Please answer [Y/N]: " answer_create_namespace
        done
        answer_check Y ${answer_create_namespace}
        if [[ "${answer_create_namespace}" != "N" && "${answer_create_namespace}" != "n" ]]; then
            answer_create_namespace="Y"
        fi
        if [[ "${answer_create_namespace}" == "Y" || "${answer_create_namespace}" == "y" ]]; then
            kubectl create ns ${gitlab_runner_namespace}
        fi
        if [[ "${answer_create_namespace}" == "N" || "${answer_create_namespace}" == "n" ]]; then
            read -e -p "Please input the namespace for gitlab runner to install: " gitlab_runner_namespace
            while [[ "${gitlab_runner_namespace}" == "" ]]; do
                read -e -p "Please input the namespace: " gitlab_runner_namespace
            done
        fi
        check_ns_exists=`kubectl get ns | grep -w ${gitlab_runner_namespace} | wc -l`
    done
    GITLAB_RUNNER_NAMESPACE=${gitlab_runner_namespace}

    echo
    read -e -n1 -p "# If you use MinIO as your gitlab storage? [Y/N]" answer_using_minio
    while [[ "${answer_using_minio}" == "" ]]; do
        read -e -n1 -p "Please answer [Y/N]: " answer_using_minio
    done
    answer_check Y ${answer_using_minio}
    if [[ ${answer_using_minio} != "N" &&  ${answer_using_minio} != "n" ]]; then
        answer_using_minio="Y"
    fi

    if [[ "${answer_using_minio}" == "Y" || "${answer_using_minio}" == "y" ]]; then
        minIO_status_check
        echo "# Create secret for MinIO."
        check_minio_secret_exists=`kubectl get secret -n ${GITLAB_RUNNER_NAMESPACE} | grep -w minio-access | wc -l`
        if [[ "${check_minio_secret_exists}" == 1 ]]; then
            echo "The secret \"minio-access\" already exists,using the created secret."
        else
            kubectl create secret generic minio-access -n ${GITLAB_RUNNER_NAMESPACE} \
                --from-literal=accesskey=${MINIO_ACCESS_KEY} \
                --from-literal=secretkey=${MINIO_SECRET_KEY}
        fi
    else
        echo "* You should install your gitlab runner chart with your configurations in the last step."
    fi

    echo
    echo "## Starting to install the gitlab-runner chart."
    echo "Fetch the chart of gitlab-runner..."
    stty igncr
    helm fetch --untar gitlab/gitlab-runner --version=0.3.0
    stty -igncr
    echo "The chart has been fetched."
    press_enter_continue

    echo
    read -n1 -e -p "# If you need to configure the Maven setting.xml? [Y/N] " answer_maven_setting
    while [[ "${answer_maven_setting}" == "" ]]; do
        read -e -n1 -p "Please answer [Y/N]: " answer_maven_setting
    done
    answer_check N ${answer_maven_setting}
    if [[ "${answer_maven_setting}" == "Y" || "${answer_maven_setting}" == "y" ]]; then
        answer_maven_setting="N"
    fi

    if [[ "${answer_maven_setting}" == "Y" || "${answer_maven_setting}" == "y" ]]; then
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
        echo "* Please edit the yaml file with your configurations."
        vi dew-maven-settings.yaml <EOF  < /dev/tty
        echo
        kubectl apply -f dew-maven-settings.yaml
    fi

    echo
    answer_edit_chart="N"
    if [[ "${answer_maven_setting}" == "N" || "${answer_maven_setting}" == "n" ]]; then
        read -n1 -e -p "# Do you need to edit the \"configmap.yaml\" of the gitlab-runner chart? [Y/N]" answer_edit_chart
        while [[ "${answer_edit_chart}" == "" ]]; do
            read -e -n1 -p "Please answer [Y/N]: " answer_edit_chart
        done
        answer_check N ${answer_edit_chart}
        if [[ "${answer_edit_chart}" != "Y" && "${answer_edit_chart}" != "y" ]]; then
            answer_edit_chart="N"
        fi
    fi

   if [[ "${answer_maven_setting}" == "Y" || "${answer_maven_setting}" == "y" || "${answer_edit_chart}" == "Y"|| "${answer_edit_chart}" == "y" ]]; then
        echo
        echo "# Edit the \"configmap.yaml\" of gitlab runner chart."
        echo "# Tips: add your configuration before \"# Start the runner\"."
        echo "# e.g."
        echo "  -----------"
        echo -e "    cat >>/home/gitlab-runner/.gitlab-runner/config.toml <<EOF
        [[runners.kubernetes.volumes.config_map]]
          name = \"dew-maven-settings\"
          mount_path = \"/opt/maven\"
    EOF
    # Start the runner"
        echo "  -----------"
        press_enter_continue
        vi gitlab-runner/templates/configmap.yaml +/"# Start the runner" <EOF  < /dev/tty
    fi

    echo
    read -e -p "# Please input your gitlab url, e.g. \"http://gitlab.dew.ms\" : " gitlab_url
    if [[ "${gitlab_url}" == "" ]]; then
        echo "* No gitlab url was entered, using the default url \"http://gitlab.dew.ms\"."
        gitlab_url=${GITLAB_URL}
    fi
    GITLAB_URL=${gitlab_url}

    echo
    echo "# The project name is used for the helm release name of your project gitlab-runner."
    read -e -p "# Please input your project name: " project_name
    while [[ "${project_name}" == "" ]]; do
        read -e -p "Please input your project name: " project_name
    done

    check_helm_runner_exists=`helm list | grep -w ${project_name} | wc -l`
    while [[ "${check_helm_runner_exists}" == 1 ]]; do
        echo "* Error: a release named ${project_name} already exists."
        read -e -p "Please input another project name: " project_name
        while [[ "${project_name}" == "" ]]; do
            read -e -p "Please input your project name: " project_name
        done
        check_helm_runner_exists=`helm list | grep -w ${project_name} | wc -l`
    done
    GITLAB_RUNNER_NAME=${project_name}

    echo
    echo "## The registration token for adding new Runners to the GitLab server."
    echo "# This must be retrieved from your GitLab instance."
    echo "# ref: https://docs.gitlab.com/ee/ci/runners/"
    echo "# e.g. ${GITLAB_RUNNER_REG_TOKEN}"
    read -e -p "# Please input the runner registration token of your gitlab project: " runner_registration_token
    while [[ "${runner_registration_token}" == "" ]]; do
        read -e -p "# Please input the runner registration token: " runner_registration_token
    done
    GITLAB_RUNNER_REG_TOKEN=${runner_registration_token}

    echo
    echo "# e.g. Using \"test\" to label your project environment. "
    read -e -p "# Please input your project profile: " project_profile
    while [[ "${project_profile}" == "" ]]; do
        read -e -p "Please input your project profile: " project_profile
    done
    GITLAB_RUNNER_PROFILE=${project_profile}

    echo
    echo "# Default container image to use for builds when none is specified."
    echo "# the default value is \"${GITLAB_RUNNER_IMAGE}\"."
    read -e -p "# Input your runner image:" gitlab_runner_image
    if [[ "${gitlab_runner_image}" == "" ]]; then
        echo "* No value was entered,using the default value \"${GITLAB_RUNNER_IMAGE}\"."
        gitlab_runner_image=${GITLAB_RUNNER_IMAGE}
    fi
    GITLAB_RUNNER_IMAGE=${gitlab_runner_image}

    # The settings for helm installation.
    gitlab_runner_helm_install_settings="helm install --name ${GITLAB_RUNNER_NAME} --namespace ${GITLAB_RUNNER_NAMESPACE} gitlab-runner \\
    --set gitlabUrl=${GITLAB_URL}\\
    --set runnerRegistrationToken=${GITLAB_RUNNER_REG_TOKEN} \\
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
    --set runners.env.dew_devops_docker_host=tcp://10.200.131.215:2375 \\
    --set runners.env.dew_devops_docker_registry_url=https://harbor.trc.com/v2 \\
    --set runners.env.dew_devops_docker_registry_username=dew \\
    --set runners.env.dew_devops_docker_registry_password=Dew123456 \\
    --set runners.env.dew_devops_profile=${GITLAB_RUNNER_PROFILE} \\
    --set runners.env.dew_devops_quiet=true \\
    --set runners.env.dew_devops_kube_config=${KUBE_CONFIG} \\ "

cat > gitlab-runner/gitlab-runner-helm-installation.sh <<EOF
#!/bin/bash

#  * If you don't want to use MinIO as your gitlab storage,you should change the values of "runners.cache".
#
# You could also edit the setting with your configurations.

${gitlab_runner_helm_install_settings}
EOF

    echo
    if [[ "${answer_maven_setting}" == "Y" || "${answer_maven_setting}" == "y" ]]; then
    echo '    --set runners.env.MAVEN_OPTS="-Dmaven.repo.local=.m2 -Dorg.apache.maven.user-settings=/opt/maven/settings.xml" \' >> gitlab-runner/gitlab-runner-helm-installation.sh
    fi

    read -n1 -e -p "# If you want to add your runner custom settings of helm installation? [Y/N] " answer_helm
    while [[ "${answer_helm}" == "" ]]; do
        read -e -n1 -p "Please answer [Y/N]: " answer_helm
    done
    answer_check N ${answer_helm}
    if [[ "${answer_helm}" != "Y" && "${answer_helm}" != "y"  ]]; then
        answer_helm="N"
    fi

    if [[ "${answer_helm}" == "Y" || "${answer_helm}" == "y" ||  "${answer_using_minio}" == "N" || "${answer_using_minio}" == "n" ]]; then
        vi gitlab-runner/gitlab-runner-helm-installation.sh <EOF  < /dev/tty
        sh gitlab-runner/gitlab-runner-helm-installation.sh
    elif [[ "${answer_helm}" == "N" || "${answer_helm}" == "n" ]]; then
        echo "Installing gitlab runner chart with the default settings."
        echo
        sh gitlab-runner/gitlab-runner-helm-installation.sh
    fi

   check_helm_runner_exists=`helm list | grep -w ${GITLAB_RUNNER_NAME} | wc -l`
   if [[ "${check_helm_runner_exists}" == 0 ]]; then
       echo
       echo -e "\033[31m * ERROR: \033[1;m""Failed to install gitlab runner! Please check your settings and execute it by yourself."
       echo
       cat gitlab-runner/gitlab-runner-helm-installation.sh
       echo
       echo "The script to end."
   else
       echo
       echo "* Finished to install gitlab-runner for project [${GITLAB_RUNNER_NAME}]."
   fi
   echo
   exit;

}



# ------------------
# Select an option
# ------------------
echo ""
echo "=================== Dew DevOps Script ==================="
echo ""


PS3='Choose your option: '

select option in "Init cluster" "Create a project" "Install a gitlab runner project"

do
    case ${option} in
     'Init cluster')
      echo "========== Init cluster =========="
      init_env_check
      init_cluster
      break;;
     'Create a project')
      echo "========== Create a Project =========="
      init_env_check
      harbor_status_check
      project_create_check
      project_create
      break;;
     'Install a gitlab runner project')
      echo "========== Install a gitlab runner for project =========="
      init_env_check
      break;;
    esac
done
