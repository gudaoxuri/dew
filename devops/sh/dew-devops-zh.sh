#!/bin/bash
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

HARBOR_SCHEME=https
HARBOR_REGISTRY_HOST=harbor.dew.ms
HARBOR_REGISTRY_ADMIN=admin
HARBOR_REGISTRY_ADMIN_PASSWORD=Harbor12345
HARBOR_REGISTRY_PASSWORD_REGEX="(?=^.{8,20}$)(?=^[^\s]*$)(?=.*\d)(?=.*[A-Z])(?=.*[a-z])"
PROJECT_NAMESPACE=devops-example
DEW_HARBOR_USER_NAME=${PROJECT_NAMESPACE}
DEW_HARBOR_USER_PASS=Dew\!123456
DEW_HARBOR_USER_EMAIL=${DEW_HARBOR_USER_NAME}@dew.ms

MINIO_HOST="10.200.131.182:9000"
MINIO_ACCESS_KEY="dew"
MINIO_SECRET_KEY="Dew123456"
MINIO_BUCKET_NAME="dew"

GITLAB_URL="http://gitlab.dew.ms"
GITLAB_RUNNER_NAMESPACE="default"
GITLAB_RUNNER_IMAGE="ubuntu:16.04"
GITLAB_PROJECT_NAME="dew-project-name"
GITLAB_RUNNER_REG_TOKEN=3mezus8cX9qAjkrNY4B
GITLAB_RUNNER_PROJECT_PROFILE=test

# ------------------
# Params dealing
# ------------------

answer_check(){
    case $2 in
    Y | y)
          echo "\"Yes\"，继续执行。";;
    N | n)
          echo "\"No\"，跳过此步骤。";;
    *)
          echo "* 输入为其他内容，使用默认回答\"$1\"。";;
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
    read -s -p "* 请按回车键[Enter]继续。" enter
    echo
}


# ------------------
# Init
# ------------------


init_env_check(){
    # 进行节点状态检查
    echo "--------------------------------------"
    echo "# 检查Kubernetes集群状况。"
    node_ready_check=`kubectl get node | grep -w Ready | wc -l`

    node_not_ready_check=`kubectl get node | grep -w NotReady | wc -l`
    node_x509_check=`kubectl get node | grep x509 | wc -l`
    if [[ "${node_not_ready_check}" -gt 0 || "${node_x509_check}" -gt 0 ]] ; then
        echo "kubectl get node"
        echo "`kubectl get node`"
        echo
        echo "节点状态异常，脚本终止！请检查Kubernetes集群状态后继续。"
        echo "--------------------------------------"
        exit;
    fi

    if [[ "${node_not_ready_check}" -eq 0 && "${node_ready_check}" -gt 0 ]] ; then
        echo "Kubernetes集群环境正常。"
    fi
    echo "--------------------------------------"
}


add_helm_gitlab_repo(){
    echo "--------------------------------------"
    echo "# 添加gitlab的Helm仓库。"
    check_gitlab_repo_exists=`helm repo list | grep -P 'gitlab\s*https://charts.gitlab.io'| wc -l`
    if [[ "${check_gitlab_repo_exists}" == 1 ]]; then
        echo "* \"gitlab\"仓库已存在。"
        read -e -n1 -p "是否需要更新Helm仓库？ [Y/N]" answer_update_helm_repo
        while [[ "${answer_update_helm_repo}" == "" ]]; do
            read -e -n1 -p "请回答 [Y/N]:" answer_update_helm_repo
        done
        answer_check N ${answer_update_helm_repo}
        if [[ "${answer_update_helm_repo}" != "Y" && "${answer_update_helm_repo}" != "y" ]]; then
            answer_update_helm_repo="N"
        fi
        if [[ "${answer_update_helm_repo}" == "Y" || "${answer_update_helm_repo}" == "y" ]]; then
            echo
            echo "更新仓库中..."
            echo "可能需要一段时间，请等待..."
            stty igncr
            helm repo update
            stty -igncr
            echo "Helm仓库更新完成。"
            press_enter_continue
        fi
    else
        check_gitlab_repo_add=`helm repo add gitlab https://charts.gitlab.io | grep '"gitlab" has been added to your repositories' | wc -l`
        if [[ "${check_gitlab_repo_add}" -ne 1 ]]; then
            echo "* 添加gitlab的Helm仓库失败，请检查Helm状态。脚本中断。"
            exit;
        fi
        echo "\"gitlab\" 仓库添加完毕。"
    fi
    echo "--------------------------------------"
}


minIO_status_check(){
    echo "--------------------------------------"
    echo "# 检查MinIO状态。"

    echo "# MinIO地址，例：${MINIO_HOST} "
    read -e -p "请输入MinIO地址： " minIO_host
    if [[ "${minIO_host}" == "" ]]; then
        echo "* 没有输入值，使用默认的MinIO地址："
        echo "${MINIO_HOST}"
        minIO_host=${MINIO_HOST}
    fi
    MINIO_HOST=${minIO_host}

    check_minIo_status=`curl ${MINIO_HOST} -o /dev/nullrl -s -w %{http_code} `
    if [[ "${check_minIo_status}" != 403 ]]; then
        echo
        echo "MinIO不可访问，请检查MInIO环境状态。"
        echo "脚本中断。"
        exit;
    fi
    echo
    echo "MinIO状态正常。"
    echo

    echo "# 检查 access key 和 secret key 是否正确。"
    read -e -p "请输入 MinIO access key: " minio_access_key
    if [[ "${minio_access_key}" == "" ]]; then
        echo "* 没有 access key 输入，使用默认值\"${MINIO_ACCESS_KEY}\"。"
        minio_access_key=${MINIO_ACCESS_KEY}
    fi
    read -e -s -p "请输入 MinIO secret key: " nimio_secret_key
    if [[ "${nimio_secret_key}" == "" ]]; then
        echo
        echo "* 没有 secret key 输入，使用默认值\"${MINIO_SECRET_KEY}\"。"
        nimio_secret_key=${MINIO_SECRET_KEY}
    fi
    echo

    MINIO_ACCESS_KEY=${minio_access_key}
    MINIO_SECRET_KEY=${nimio_secret_key}

    minio_key_check=`curl -X POST "${MINIO_HOST}/minio/webrpc" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"id\":1,\"jsonrpc\":\"2.0\",\"params\":{\"username\":\"${MINIO_ACCESS_KEY}\",\"password\":\"${MINIO_SECRET_KEY}\"},\"method\":\"Web.Login\"}}" -s -k`

    while [[ "${minio_key_check}" =~ error ]]; do
        echo
        echo "* "`get_json_value "${minio_key_check}" message`
        read -e -p "请输入正确的 access key: " minio_access_key
        read -e -s -p "请输入正确的 secret key: " nimio_secret_key
        MINIO_ACCESS_KEY=${minio_access_key}
        MINIO_SECRET_KEY=${nimio_secret_key}

        minio_key_check=`curl -X POST "${MINIO_HOST}/minio/webrpc" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"id\":1,\"jsonrpc\":\"2.0\",\"params\":{\"username\":\"${MINIO_ACCESS_KEY}\",\"password\":\"${MINIO_SECRET_KEY}\"},\"method\":\"Web.Login\"}}" -s -k`
    done
    echo "校验 access key 和 secret key 通过。"

    echo
    read -e -p "# 请输入用来存储 gitlab runner 数据的 MinIO bucket 名字： " minio_bucket_name
    if [[ "${minio_bucket_name}" == "" ]]; then
        echo "* 没有值被输入，使用默认值 \"${MINIO_BUCKET_NAME}\"。"
        minio_bucket_name=${MINIO_BUCKET_NAME}
    fi
       MINIO_BUCKET_NAME=${minio_bucket_name}

    echo
    echo "--------------------------------------"
}

init_cluster(){
    echo "--------------------------------------"
    echo "# 初始化集群，创建集群角色，用于服务发现。"
    check_cluster_role_exist=`kubectl get clusterrole | grep -w service-discovery-client | wc -l`
    if [[ "${check_cluster_role_exist}" == 0 ]]; then
        kubectl create clusterrole service-discovery-client \
        --verb=get,list,watch \
        --resource=pods,services,configmaps,endpoints
    fi
    echo
    echo "集群初始化完成。"
    echo "--------------------------------------"
    echo "=================================="
    exit;
}


# ------------------
# Create a project
# ------------------

harbor_status_check(){
    echo "------------------------------------"
    echo "## 检查 Harbor 仓库是否正常..."
    echo
    echo "# e.g. ${HARBOR_REGISTRY_HOST}"
    read -e -p "请输入 Harbor 仓库地址： " registry_host
    if [[ "${registry_host}" != "" ]]; then
        HARBOR_REGISTRY_HOST=${registry_host}
    else
        echo "* 未输入 Harbor 仓库地址，使用默认 Harbor 仓库："
        echo "* ${HARBOR_REGISTRY_HOST}"
    fi
    echo "* 默认为 https 协议。"
    harbor_registry_health_check="curl ${HARBOR_REGISTRY_HOST}/health -k"
    # 检查harbor 仓库是否正常
    registry_status=`curl ${HARBOR_REGISTRY_HOST}/health -o /dev/nullrl -s -w %{http_code} -k`
    if [[ "${registry_status}" -ne 200 ]]; then
        echo
        echo ${harbor_registry_health_check}
        echo "`${harbor_registry_health_check}`"
        echo "harbor仓库访问失败，脚本终止。请检查配置是否有误。"
        echo
        exit;
    fi
    echo
    echo "Harbor 仓库环境正常。"
    echo "------------------------------------"
}

project_create_check(){
    echo "Tips: 创建项目之前注意要初始化集群。"
    echo "____________________________________"
    echo
    # 校验harbor相关参数
    read -e -p "输入Harbor仓库管理员账号" registry_admin
    if [[ "${registry_admin}" = "" ]]; then
        echo "* 未输入Harbor仓库管理员账号，使用默认账号：${HARBOR_REGISTRY_ADMIN}"
    fi

    echo

    echo "# 密码长度在8-20之间，且需包含一个大写字母，一个小写字母和一个数字。"
    read -e -s -p "请输入管理员密码：" registry_password
    echo

    regex_password="(?=^.{8,20}$)(?=^[^\s]*$)(?=.*\d)(?=.*[A-Z])(?=.*[a-z])"
    check_password=`echo "${registry_password}" | grep -P ${regex_password}| wc -l`
    while [[ "${check_password}" -eq 0 ]];do
        read -e -s -p "密码格式不正确，请重新输入： " registry_password
        check_password=`echo "${registry_password}" | grep -P ${regex_password}| wc -l`
    done

    if [[ "${registry_admin}"=="" ]]; then
        registry_admin=${HARBOR_REGISTRY_ADMIN}
    fi

    echo
    # 校验密码正确
    ADMIN_AUTHORIZATION=`echo -n ${registry_admin}:${registry_password} | base64`
    check_admin_status=`curl "${HARBOR_REGISTRY_HOST}/api/users" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -o /dev/nullrl -s -w %{http_code} -k`
    while [[ "${check_admin_status}" -eq 401 || "${check_admin_status}" -eq 403 ]];do
        echo "* 用户名或密码错误，或该账号不是管理员，请重新输入管理员用户名和密码："
        read -e -p "请输入管理员用户名：" registry_admin
        read -e -s -p "请输入密码" registry_password
        ADMIN_AUTHORIZATION=`echo -n ${registry_admin}:${registry_password} | base64`
        check_admin_status=`curl "${HARBOR_REGISTRY_HOST}/api/users" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -o /dev/nullrl -s -w %{http_code} -k`
    done

    if [[ "${registry_admin}" != "" ]]; then
        HARBOR_REGISTRY_ADMIN=${registry_admin}
    fi
    if [[ "${registry_password}" != "" ]]; then
        REGISTRY_ADMIN_PASSWORD=${registry_password}
    fi
    echo

    echo "# 项目名将被用来在harbor仓库中创建同名项目，默认也会创建一个具有管理该项目权限的Harbor用户。另外在Kubernetes中也会创建同名namespace。"
    echo "# 项目名应以小写字母，数字，中划线-组成，且至少两个字符并以字母或数字开头，不能以-结尾。"
    echo
    read -e -p "请输入项目名： " project_name

    # 输入项目名
    project_name_regex="^([a-z0-9]+-?[a-z0-9]+)+$"
    while [[ ! "${project_name}" =~ ${project_name_regex} ]]; do
        read -e -p "请输入正确格式的项目名: " project_name
    done

    # 校验项目名的命名空间是否存在
    check_ns_exists=`kubectl get ns | grep -w ${project_name} | wc -l`
    while [[ "${check_ns_exists}" -gt 0 ]];do
        read -e -p "已有同名命名空间（namespace），请重新输入项目名：" project_name

        while [[ ! "${project_name}" =~ ${project_name_regex} ]]; do
            read -p "项目名格式不正确，请重新输入：" -e project_name
        done

        check_ns_exists=`kubectl get ns | grep -w ${project_name} | wc -l`
    done
    # 校验项目名是否存在 harbor
    check_project_exists=`curl "${HARBOR_REGISTRY_HOST}/api/projects?name=${project_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${project_name} | wc -l`
    while [[ "${check_project_exists}" -gt 0 ]];do
        read -p "项目名已存在，请重新输入：" -e project_name

        while [[ ! "${project_name}" =~ ${project_name_regex} ]]; do
            read -p "项目名格式不正确，请重新输入：" -e project_name
        done

        check_ns_exists=`kubectl get ns | grep -w ${project_name} | wc -l`
        while [[ "${check_ns_exists}" -gt 0 ]];do
            read -e -p "已有同名命名空间（namespace），请重新输入项目名：" project_name
            while [[ ! "${project_name}" =~ ${project_name_regex} ]]; do
                read -e -p "项目名格式不正确，请重新输入：" project_name
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

    # 判断项目同名用户是否存在
    check_user_exists=`curl "${HARBOR_REGISTRY_HOST}/api/users?username=${project_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${project_name} | wc -l`
    while [[ "${check_user_exists}" -gt 0 ]]; do
        read -p "已有与项目同名用户存在，请输入一个用户名以和该项目进行绑定：" -e user_name
        check_user_exists=`curl "${HARBOR_REGISTRY_HOST}/api/users?username=${user_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${user_name} | wc -l`
        DEW_HARBOR_USER_NAME=${user_name}
    done
    echo

    read -e -n1 -p "是否要修改项目管理者用户初始密码？ [Y/N]" answer_custom_user_password
    answer_check ${answer_custom_user_password} N
    if [[ "${answer_custom_user_password}" != "Y" && "${answer_custom_user_password}" != "y" ]]; then
        answer_custom_user_password="N"
    fi
    if [[ "${answer_custom_user_password}" == "Y" || "${answer_custom_user_password}" == "y" ]]; then
        read -e -s -p "请输入密码: " user_account_password
        check_user_password=`echo "${user_account_password}" | grep -P ${regex_password}| wc -l`
        while [[ "${check_user_password}" -eq 0 ]];do
            echo
            read -e -s -p "密码格式不正确，请重新输入： " user_account_password
            check_user_password=`echo "${user_account_password}" | grep -P ${regex_password}| wc -l`
        done
        DEW_HARBOR_USER_PASS=${user_account_password}
    else
        echo "* 使用默认密码\"${DEW_HARBOR_USER_PASS}\"。"
    fi
    echo

    # 校验邮箱
    echo "# 邮箱是用来和项目创建的用户进行绑定。"
    emailRegex="^([a-zA-Z0-9_\-\.\+]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5})$"
    read -e -p "请为该项目输入要绑定的邮箱： " user_email
    while [[ ! "${user_email}" =~ ${emailRegex} ]]; do
    read -e -p "邮箱格式不正确，请重新输入：" user_email
    done

    # 校验邮箱是否存在，以及邮箱格式的正确性
    check_email_exists=`curl "${HARBOR_REGISTRY_HOST}/api/users?email=${user_email}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${user_email} | wc -l`
    while [[ ${check_email_exists} -gt 0 ]];do
        read -e -p "邮箱已存在，请重新输入：" user_email
        while [[ ! "${user_email}" =~ ${emailRegex} ]]; do
            read -e -p "邮箱格式不正确，请重新输入：" user_email
        done
        check_email_exists=`curl "${HARBOR_REGISTRY_HOST}/api/users?email=${user_email}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${user_email} | wc -l`
    done

    if [[ "${user_email}" != "" ]]; then
        DEW_HARBOR_USER_EMAIL=${user_email}
    fi
}


project_create(){
    echo
    echo "# 开始为项目创建用户。"
    ADMIN_AUTHORIZATION=`echo -n ${HARBOR_REGISTRY_ADMIN}:${REGISTRY_ADMIN_PASSWORD} | base64`

    create_user_result=`curl -X POST "${HARBOR_REGISTRY_HOST}/api/users" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -H "Content-Type: application/json" -d "{ \"email\": \"${DEW_HARBOR_USER_EMAIL}\", \"username\": \"${DEW_HARBOR_USER_NAME}\", \"password\": \"${DEW_HARBOR_USER_PASS}\", \"realname\": \"${DEW_HARBOR_USER_NAME}\", \"comment\": \"init\"}" -o /dev/nullrl -s -w %{http_code} -k`
    if [[ "${create_user_result}" -ne 201 ]]; then
        echo "创建用户失败，脚本终止，请重试。"
        exit;
    fi
    echo "与项目同名的用户[${DEW_HARBOR_USER_NAME}]已创建成功,默认密码为${DEW_HARBOR_USER_PASS}。"
    echo

    echo "# 开始创建Harbor项目。"
    USER_AUTHORIZATION=`echo -n ${DEW_HARBOR_USER_NAME}:${DEW_HARBOR_USER_PASS} | base64`

    create_project_result_code=`curl -X POST "${HARBOR_REGISTRY_HOST}/api/projects" -H "accept: application/json" -H "authorization: Basic ${USER_AUTHORIZATION}" -H "Content-Type: application/json" -d "{ \"project_name\": \"${PROJECT_NAMESPACE}\"}" -o /dev/nullrl -s -w %{http_code} -k`
    if [[ "${create_project_result_code}" -ne 201 ]]; then
        echo "创建项目失败，脚本终止，请重试。"
        exit;
    fi
    echo "项目[${PROJECT_NAMESPACE}]创建成功。"
    echo

    # 创建新的命名空间，用于资源隔离
    echo "# 开始在 Kubernetes 集群中初始化同名项目。"
    kubectl create namespace ${PROJECT_NAMESPACE}

    # 绑定服务发现角色到该命名空间的默认用户
    kubectl create rolebinding default:service-discovery-client \
        -n ${PROJECT_NAMESPACE} \
        --clusterrole service-discovery-client \
        --serviceaccount ${PROJECT_NAMESPACE}:default

    # 创建Docker Registry密钥
    kubectl -n ${PROJECT_NAMESPACE} create secret docker-registry dew-registry \
        --docker-server=${HARBOR_REGISTRY_HOST} \
        --docker-username=${DEW_HARBOR_USER_NAME} \
        --docker-password=${DEW_HARBOR_USER_PASS} \
        --docker-email=${DEW_HARBOR_USER_EMAIL}

    # 为该命名空间下的默认用户绑定Docker Registry密钥
    kubectl -n ${PROJECT_NAMESPACE} patch serviceaccount default \
        -p '{"imagePullSecrets": [{"name": "dew-registry"}]}'

    # 创建Ingress
    # 详见 https://kubernetes.io/docs/concepts/services-networking/ingress/
    #######################
    # apiVersion: extensions/v1beta1
    # kind: Ingress
    # metadata:
    #   annotations:
    #     # 所有注解见 https://kubernetes.github.io/ingress-nginx/user-guide/nginx-configuration/annotations/
    #     # 重写示例见 https://github.com/kubernetes/ingress-nginx/tree/master/docs/examples/rewrite
    #     nginx.ingress.kubernetes.io/rewrite-target: /\$1
    #   name: dew-ingress
    #   namespace: $PROJECT_NAMESPACE
    # spec:
    #   rules:
    #     # 自定义规则
    #######################
    echo
    echo "# 开始在 Kubernetes 集群中创建 Ingress。"

    echo
    echo "# nginx重写前缀是用来给 Ingress 注解 nginx.ingress.kubernetes.io/rewrite-target 填值。"
    echo "# 重写示例见 https://github.com/kubernetes/ingress-nginx/tree/master/docs/examples/rewrite"
    echo "# 所有注解见 https://kubernetes.github.io/ingress-nginx/user-guide/nginx-configuration/annotations/"
    echo "# 如需要其他注解，请在脚本执行完成后使用以下命令自行编辑。"
    echo "# kubectl edit ingress dew-ingress -n ${PROJECT_NAMESPACE}"
    echo
    read -e -p "请输入nginx重写前缀：" nginx_rewrite_target

    read -e -p "请输入后端host：" backend_host
    while [[ ! ${backend_host} =~ ^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*$ ]] ;do
        read -e -p "后端host不符合规范，请重新输入：" backend_host
    done

    echo
    echo "# 请按顺序输入后端服务名、端口号、接口前缀。如有多组，请以空格分隔。参数请符合DNS规范。"
    echo "# 服务名（servicename）由小写字母，数字和中划线-组成，且应以字母开头。"
    echo "# 例： services 8080 api serviceb 8081 rest servicec 8090 manage"
    echo
    read -e -p "请输入后端服务,如有多个，请以空格分隔： " backend_service
    backend_services=(${backend_service})

    while [[ "${#backend_services[@]}"%3 -eq 1 || "${#backend_services[@]}" -eq 0 ]]; do
        read -p "服务名称和服务端口不可缺少，请重新输入服务名、端口号、接口前缀： " -e backend_service
        backend_services=(${backend_service})
    done

    b=0
    backend_yaml_values=""
    while [[ "${b}" -lt "${#backend_services[@]}" ]]; do
        while [[ ! "${backend_services[b]}" =~ ^[a-z]([a-z0-9]*)(-[a-z0-9]+)*$ ]]; do
            echo "服务名["${backend_services[b]}"]不符合规范，请输入一个规范的服务名。"
            read -e service_name
            backend_services[b]=${service_name}
        done

        while [[ ! "${backend_services[${b}+1]}" =~ ^[a-z0-9]*$ ]]; do
            echo "服务端口["${backend_services[${b}+1]}"]不符合规范,请输入一个规范的端口。"
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
    read -e -p "请输入前端host： " frontend_host
    while [[ ! "${frontend_host}" =~ ^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*$ ]] ;do
        read -e -p "前端host不符合规范，请重新输入： " frontend_host
    done

    echo
    echo "# 请输入前端服务名、端口号、接口前缀。如有多组，请以空格分隔。参数请符合DNS规范。"
    echo "# 服务名（servicename）由小写字母，数字和中划线-组成，且应以字母开头。"
    echo "# 例： servicea 8080 api serviceb 8081 rest servicec 8090 manage"
    echo
    read -e -p "请输入前端服务,如有多个，请以空格分隔： " frontend_service
    frontend_services=(${frontend_service})

    while [[ "${#frontend_services[@]}"%3 -eq 1 || "${#frontend_services[@]}" -eq 0 ]]; do
        read -e -p "服务名称和服务端口不可缺少，请重新输入服务名、端口号、接口前缀： " frontend_service
        frontend_services=(${frontend_service})
    done

    f=0
    frontend_yaml_values=""
    while [[ "${f}" -lt "${#frontend_services[@]}" ]]; do
        while [[ ! "${frontend_services[f]}" =~ ^[a-z]([a-z0-9]*)(-[a-z0-9]+)*$ ]]; do
            echo "服务名["${frontend_services[f]}"]不符合规范，请输入一个规范的服务名。"
            read -e service_name
            frontend_services[f]=${service_name}
        done
        while [[ ! "${frontend_services[${f}+1]}" =~ ^[a-z0-9]*$ ]]; do
            echo "服务端口["${frontend_services[${f}+1]}"]不符合规范,请输入一个规范的端口。"
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

    # 创建Ingress

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

    # 确认Ingress是否创建成功
    echo
    check_ingress_exist=`kubectl get ing dew-ingress -n ${PROJECT_NAMESPACE} | wc -l`
    if [[ "${check_ingress_exist}" -eq 0 ]]; then
        echo -e "\033[31m * Ingress 创建失败，脚本结束，请自行为该项目创建Ingress。\033[1;m"
        exit;
    else
        echo "项目[${PROJECT_NAMESPACE}]创建完成，脚本结束。"
        exit;
    fi

    echo "=================================="
}

install_gitlab_runner_project(){

    echo
    read -e -p "# 请输入用来安装 gitlab runner 的命名空间（namespace）：" gitlab_runner_namespace

    if [[ "${gitlab_runner_namespace}" == "" ]]; then
        echo "* 没有值输入，使用默认的命名空间\"${GITLAB_RUNNER_NAMESPACE}\"。"
        gitlab_runner_namespace="${GITLAB_RUNNER_NAMESPACE}"
    fi

    check_ns_exists=`kubectl get ns | grep -w ${gitlab_runner_namespace} | wc -l`
    while [[ "${check_ns_exists}" == 0 ]]; do
        read -e -n1 -p "* 命名空间[${gitlab_runner_namespace}] 不存在，是否需要创建此命名空间？ [Y/N]： " answer_create_namespace
        while [[ "${answer_create_namespace}" == "" ]]; do
            read -e -n1 -p "请回答 [Y/N]: " answer_create_namespace
        done
        answer_check Y ${answer_create_namespace}
        if [[ "${answer_create_namespace}" != "N" && "${answer_create_namespace}" != "n" ]]; then
            answer_create_namespace="Y"
        fi
        if [[ "${answer_create_namespace}" == "Y" || "${answer_create_namespace}" == "y" ]]; then
            kubectl create ns ${gitlab_runner_namespace}
        fi
        if [[ "${answer_create_namespace}" == "N" || "${answer_create_namespace}" == "n" ]]; then
            read -e -p "请输入用来安装 gitlab runner 的命名空间（namespace）：" gitlab_runner_namespace
            while [[ "${gitlab_runner_namespace}" == "" ]]; do
                read -e -p "请输入命名空间： " gitlab_runner_namespace
            done
        fi
        check_ns_exists=`kubectl get ns | grep -w ${gitlab_runner_namespace} | wc -l`
    done
    GITLAB_RUNNER_NAMESPACE=${gitlab_runner_namespace}

    echo
    read -e -n1 -p "# 是否使用 MinIO 来存储 gitlab runner 数据？ [Y/N]" answer_using_minio
    while [[ "${answer_using_minio}" == "" ]]; do
        read -e -n1 -p "请回答 [Y/N]: " answer_using_minio
    done
    answer_check Y ${answer_using_minio}
    if [[ ${answer_using_minio} != "N" &&  ${answer_using_minio} != "n" ]]; then
        answer_using_minio="Y"
    fi

    if [[ "${answer_using_minio}" == "Y" || "${answer_using_minio}" == "y" ]]; then
        minIO_status_check
        echo "# 创建为MinIO创建密钥（secret）。"
        check_minio_secret_exists=`kubectl get secret -n ${GITLAB_RUNNER_NAMESPACE} | grep -w minio-access | wc -l`
        if [[ "${check_minio_secret_exists}" == 1 ]]; then
            echo "密钥\"minio-access\"已存在，直接使用已创建的。"
        else
            kubectl create secret generic minio-access -n ${GITLAB_RUNNER_NAMESPACE} \
                --from-literal=accesskey=${MINIO_ACCESS_KEY} \
                --from-literal=secretkey=${MINIO_SECRET_KEY}
        fi
    else
        echo "* 您应该在最后的gitlab runner Helm 安装步骤中配置自己的存储工具。"
    fi

    echo
    echo "## 开始安装 gitlab-runner chart."
    echo "下载 gitlab-runner chart ..."
    stty igncr
    helm fetch --untar gitlab/gitlab-runner
    stty -igncr
    echo "Chart下载完毕。"
    press_enter_continue

    echo
    read -n1 -e -p "# 是否需要配置 Maven settings.xml? [Y/N] " answer_maven_setting
    while [[ "${answer_maven_setting}" == "" ]]; do
        read -e -n1 -p "请回答 [Y/N]: " answer_maven_setting
    done
    answer_check N ${answer_maven_setting}
    if [[ "${answer_maven_setting}" == "Y" || "${answer_maven_setting}" == "y" ]]; then
        answer_maven_setting="N"
    fi

    if [[ "${answer_maven_setting}" == "Y" || "${answer_maven_setting}" == "y" ]]; then
cat > dew-maven-settings.yaml <<EOF
# 请根据需要来修改此文件。

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
             <!-- 示例，添加一个私有库认证 -->
             <server>
                 <id>please-change-repo</id>
                 <username>please-change-username</username>
                 <password>please-change-password</password>
             </server>
         </servers>
     </settings>
EOF
        echo
        echo "* 请根据需要来配置Maven的ConfigMap。"
        vi dew-maven-settings.yaml <EOF  < /dev/tty
        echo
        kubectl apply -f dew-maven-settings.yaml
    fi

    echo
    answer_edit_chart="N"
    if [[ "${answer_maven_setting}" == "N" || "${answer_maven_setting}" == "n" ]]; then
        read -n1 -e -p "# 是否需要编辑 gitlab-runner chart的\"configmap.yaml\" ？ [Y/N]" answer_edit_chart
        while [[ "${answer_edit_chart}" == "" ]]; do
            read -e -n1 -p "请回答 [Y/N]: " answer_edit_chart
        done
        answer_check N ${answer_edit_chart}
        if [[ "${answer_edit_chart}" != "Y" && "${answer_edit_chart}" != "y" ]]; then
            answer_edit_chart="N"
        fi
    fi

   if [[ "${answer_maven_setting}" == "Y" || "${answer_maven_setting}" == "y" || "${answer_edit_chart}" == "Y"|| "${answer_edit_chart}" == "y" ]]; then
        echo
        echo "# 编辑 gitlab-runner chart的\"configmap.yaml\"。"
        echo "# Tips: 请在\"# Start the runner\"之前添加配置。"
        echo "# 例："
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
    read -e -p "# 请输入gitlab地址，例：\"http://gitlab.dew.ms\" : " gitlab_url
    if [[ "${gitlab_url}" == "" ]]; then
        echo "* 没有值被输入，使用默认值\"http://gitlab.dew.ms\"。"
        gitlab_url=${GITLAB_URL}
    fi
    GITLAB_URL=${gitlab_url}

    echo
    echo "# 项目名是被用来作为项目的gitlab runner的名称。"
    read -e -p "# 请输入项目名： " project_name
    while [[ "${project_name}" == "" ]]; do
        read -e -p "请输入项目名： " project_name
    done

    check_helm_runner_exists=`helm list | grep -w ${project_name} | wc -l`
    while [[ "${check_helm_runner_exists}" == 1 ]]; do
        echo "* 项目名 ${project_name} 已存在。"
        read -e -p "请输入新的项目名： " project_name
        while [[ "${project_name}" == "" ]]; do
            read -e -p "请输入新的项目名： " project_name
        done
        check_helm_runner_exists=`helm list | grep -w ${project_name} | wc -l`
    done
    GITLAB_PROJECT_NAME=${project_name}

    echo
    echo "## The registration token for adding new Runners to the GitLab server."
    echo "# This must be retrieved from your GitLab instance."
    echo "# ref: https://docs.gitlab.com/ee/ci/runners/"
    echo "# e.g. ${GITLAB_RUNNER_REG_TOKEN}"
    read -e -p "# 请输入项目对应的 runner registration token： " runner_registration_token
    while [[ "${runner_registration_token}" == "" ]]; do
        read -e -p "# 请输入 runner registration token： " runner_registration_token
    done
    GITLAB_RUNNER_REG_TOKEN=${runner_registration_token}

    echo
    echo "# 例： 使用\"test\"来标识项目的运行环境。"
    read -e -p "# 请输入项目的运行环境标识： " project_profile
    while [[ "${project_profile}" == "" ]]; do
        read -e -p " 请输入项目的运行环境标识： " project_profile
    done
    GITLAB_RUNNER_PROJECT_PROFILE=${project_profile}

    echo
    echo "# Default container image to use for builds when none is specified."
    echo "# 默认值是\"${GITLAB_RUNNER_IMAGE}\"."
    read -e -p "# 请输入 runner镜像： " gitlab_runner_image
    if [[ "${gitlab_runner_image}" == "" ]]; then
        echo "* 没有值输入，使用默认值\"${GITLAB_RUNNER_IMAGE}\"。"
        gitlab_runner_image=${GITLAB_RUNNER_IMAGE}
    fi
    GITLAB_RUNNER_IMAGE=${gitlab_runner_image}

    # gitlab runner 的 Helm 安装参数。
    gitlab_runner_helm_install_settings="helm install --name ${GITLAB_PROJECT_NAME} --namespace ${GITLAB_RUNNER_NAMESPACE} gitlab-runner \\
    --set gitlabUrl=${GITLAB_URL}\\
    --set runnerRegistrationToken=${GITLAB_RUNNER_REG_TOKEN} \\
    --set rbac.create=true \\
    --set rbacWideAccess=true \\
    --set runners.tags=${GITLAB_RUNNER_PROJECT_PROFILE} \\
    --set runners.image=${GITLAB_RUNNER_IMAGE} \\
    --set runners.cache.cacheType=s3 \\
    --set runners.cache.cacheShared=true \\
    --set runners.cache.s3ServerAddress=${MINIO_HOST} \\
    --set runners.cache.s3BucketName=${MINIO_BUCKET_NAME} \\
    --set runners.cache.s3CacheInsecure=true \\
    --set runners.cache.secretName=minio-access \\"

cat > gitlab-runner/gitlab-runner-helm-installation.sh <<EOF
#!/bin/bash

#  * 如果不使用MinIO进行存储，请修改"runners.cache"的值。
#
# 可以根据需要添加相关配置。

${gitlab_runner_helm_install_settings}
EOF

    echo
    if [[ "${answer_maven_setting}" == "Y" || "${answer_maven_setting}" == "y" ]]; then
    echo '    --set runners.env.MAVEN_OPTS="-Dmaven.repo.local=.m2 -Dorg.apache.maven.user-settings=/opt/maven/settings.xml" \' >> gitlab-runner/gitlab-runner-helm-installation.sh
    fi

    read -n1 -e -p "# 是否想要修改gitlab runner的Helm 安装参数？ [Y/N] " answer_helm
    while [[ "${answer_helm}" == "" ]]; do
        read -e -n1 -p "请回答 [Y/N]: " answer_helm
    done
    answer_check N ${answer_helm}
    if [[ "${answer_helm}" != "Y" && "${answer_helm}" != "y"  ]]; then
        answer_helm="N"
    fi

    if [[ "${answer_helm}" == "Y" || "${answer_helm}" == "y" ||  "${answer_using_minio}" == "N" || "${answer_using_minio}" == "n" ]]; then
        vi gitlab-runner/gitlab-runner-helm-installation.sh <EOF  < /dev/tty
        sh gitlab-runner/gitlab-runner-helm-installation.sh
    elif [[ "${answer_helm}" == "N" || "${answer_helm}" == "n" ]]; then
        echo "使用默认值安装 gitlab runner。"
        echo
        sh gitlab-runner/gitlab-runner-helm-installation.sh
    fi

   check_helm_runner_exists=`helm list | grep -w ${GITLAB_PROJECT_NAME} | wc -l`
   if [[ "${check_helm_runner_exists}" == 0 ]]; then
       echo
       echo -e "\033[31m * ERROR: \033[1;m"" gitlab runner 安装失败！ 请检查相关配置，然后自行安装。"
       echo
       cat gitlab-runner/gitlab-runner-helm-installation.sh
       echo
       echo "脚本终止。"
   else
       echo
       echo "* [${GITLAB_PROJECT_NAME}] gitlab-runner 安装完成。脚本运行结束。"
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
      add_helm_gitlab_repo
      install_gitlab_runner_project
      break;;
    esac
done
