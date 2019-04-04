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


init_cluster(){
    echo "--------------------------------------"
    echo "# 初始化集群，创建集群角色，用于服务发现。"
    kubectl create clusterrole service-discovery-client \
    --verb=get,list,watch \
    --resource=pods,services,configmaps,endpoints
    echo "集群初始化完成。"
    echo "--------------------------------------"
    echo "=================================="
    exit;
}

SCHEME=https
REGISTRY_HOST=harbor.dew.ms
REGISTRY_ADMIN=admin
REGISTRY_ADMIN_PASSWORD=Harbor12345

PROJECT_NAMESPACE=devops-example
DEW_HARBOR_USER_NAME=${PROJECT_NAMESPACE}
DEW_HARBOR_USER_PASS=Dew\!123456
DEW_HARBOR_USER_EMAIL=${DEW_HARBOR_USER_NAME}@dew.ms

# ------------------
# Create a project
# ------------------

init_project_check(){
    echo "------------------------------------"
    echo "## 检查 Harbor 仓库是否正常..."
    echo
    read -e -p "请输入 Harbor 仓库地址： " registry_host
    if [[ "${registry_host}" != "" ]]; then
        REGISTRY_HOST=${registry_host}
    else
        echo "* 未输入 Harbor 仓库地址，使用默认 Harbor 仓库："
        echo "* ${REGISTRY_HOST}"
    fi
    echo "* 默认为 https 协议。"
    harbor_registry_health_check="curl ${SCHEME}://${REGISTRY_HOST}/health -k"
    # 检查harbor 仓库是否正常
    registry_status=`curl ${SCHEME}://${REGISTRY_HOST}/health -o /dev/nullrl -s -w %{http_code} -k`
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
    echo "输入Harbor仓库管理员账号"
    read -e registry_admin
    if [[ "${registry_admin}" = "" ]]; then
        echo "* 未输入Harbor仓库管理员账号，使用默认账号：${REGISTRY_ADMIN}"
    fi

    echo

    echo "# 密码长度在8-20之间，且需包含一个大写字母，一个小写字母和一个数字。"
    read -p "请输入管理员密码：" -e -s registry_password
    echo

    check_password=`echo "${registry_password}" | grep -P --color '(?=^.{8,20}$)(?=^[^\s]*$)(?=.*\d)(?=.*[A-Z])(?=.*[a-z])'| wc -l`
    while [[ "${check_password}" -eq 0 ]];do
        echo "密码格式不正确，请重新输入： "
        read -e -s registry_password
        check_password=`echo "${registry_password}" | grep -P --color '(?=^.{8,20}$)(?=^[^\s]*$)(?=.*\d)(?=.*[A-Z])(?=.*[a-z])'| wc -l`
    done

    if [[ "${registry_admin}"=="" ]]; then
        registry_admin=${REGISTRY_ADMIN}
    fi

    echo
    # 校验密码正确
    ADMIN_AUTHORIZATION=`echo -n ${registry_admin}:${registry_password} | base64`
    check_admin_status=`curl "${SCHEME}://${REGISTRY_HOST}/api/users" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -o /dev/nullrl -s -w %{http_code} -k`
    while [[ "${check_admin_status}" -eq 401 || "${check_admin_status}" -eq 403 ]];do
        echo "* 用户名或密码错误，或该账号不是管理员，请重新输入管理员用户名和密码："
        echo "请输入管理员用户名："
        read -e registry_admin
        echo "请输入密码"
        read -e -s registry_password
        ADMIN_AUTHORIZATION=`echo -n ${registry_admin}:${registry_password} | base64`
        check_admin_status=`curl "${SCHEME}://${REGISTRY_HOST}/api/users" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -o /dev/nullrl -s -w %{http_code} -k`
    done

    if [[ "${registry_admin}" != "" ]]; then
        REGISTRY_ADMIN=${registry_admin}
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
    check_project_exists=`curl "${SCHEME}://${REGISTRY_HOST}/api/projects?name=${project_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${project_name} | wc -l`
    while [[ "${check_project_exists}" -gt 0 ]];do
        read -p "项目名已存在，请重新输入：" -e project_name

        while [[ ! "${project_name}" =~ ${project_name_regex} ]]; do
            read -p "项目名格式不正确，请重新输入：" -e project_name
        done

        check_ns_exists=`kubectl get ns | grep -w ${project_name} | wc -l`
        while [[ "${check_ns_exists}" -gt 0 ]];do
            read -e -p "已有同名命名空间（namespace），请重新输入项目名：" project_name
            while [[ ! "${project_name}" =~ ${project_name_regex} ]]; do
                read -p "项目名格式不正确，请重新输入：" -e project_name
            done
            check_ns_exists=`kubectl get ns | grep -w ${project_name} | wc -l`
        done

        check_project_exists=`curl "${SCHEME}://${REGISTRY_HOST}/api/projects?name=${project_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${project_name} | wc -l`
    done

    if [[ "${project_name}" != "" ]]; then
        PROJECT_NAMESPACE=${project_name}
        DEW_HARBOR_USER_NAME=${PROJECT_NAMESPACE}
        DEW_HARBOR_USER_EMAIL=${DEW_HARBOR_USER_NAME}@dew.ms
    fi

    # 判断项目同名用户是否存在
    check_user_exists=`curl "${SCHEME}://${REGISTRY_HOST}/api/users?username=${project_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${project_name} | wc -l`
    while [[ "${check_user_exists}" -gt 0 ]]; do
        read -p "已有与项目同名用户存在，请输入一个用户名以和该项目进行绑定：" -e user_name
        check_user_exists=`curl "${SCHEME}://${REGISTRY_HOST}/api/users?username=${user_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${user_name} | wc -l`
        DEW_HARBOR_USER_NAME=${user_name}
    done
    echo

    # 校验邮箱
    echo "# 邮箱是用来和项目创建的用户进行绑定。"
    emailRegex="^([a-zA-Z0-9_\-\.\+]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5})$"
    read -e -p "请为该项目输入要绑定的邮箱： " user_email
    while [[ ! "${user_email}" =~ ${emailRegex} ]]; do
    read -p "邮箱格式不正确，请重新输入：" -e user_email
    done

    # 校验邮箱是否存在，以及邮箱格式的正确性
    check_email_exists=`curl "${SCHEME}://${REGISTRY_HOST}/api/users?email=${user_email}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${user_email} | wc -l`
    while [[ ${check_email_exists} -gt 0 ]];do
        read -p "邮箱已存在，请重新输入：" -e user_email
        while [[ ! "${user_email}" =~ ${emailRegex} ]]; do
            read -p "邮箱格式不正确，请重新输入：" -e user_email
        done
        check_email_exists=`curl "${SCHEME}://${REGISTRY_HOST}/api/users?email=${user_email}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${user_email} | wc -l`
    done

    if [[ "${user_email}" != "" ]]; then
        DEW_HARBOR_USER_EMAIL=${user_email}
    fi
}


project_create(){
    echo
    echo "# 开始为项目创建用户。"
    ADMIN_AUTHORIZATION=`echo -n ${REGISTRY_ADMIN}:${REGISTRY_ADMIN_PASSWORD} | base64`

    create_user_result=`curl -X POST "${SCHEME}://${REGISTRY_HOST}/api/users" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -H "Content-Type: application/json" -d "{ \"email\": \"${DEW_HARBOR_USER_EMAIL}\", \"username\": \"${DEW_HARBOR_USER_NAME}\", \"password\": \"${DEW_HARBOR_USER_PASS}\", \"realname\": \"${DEW_HARBOR_USER_NAME}\", \"comment\": \"init\"}" -o /dev/nullrl -s -w %{http_code} -k`
    if [[ "${create_user_result}" -ne 201 ]]; then
        echo "创建用户失败，脚本终止，请重试。"
        exit;
    fi
    echo "与项目同名的用户[${DEW_HARBOR_USER_NAME}]已创建成功,默认密码为${DEW_HARBOR_USER_PASS}。"
    echo

    echo "# 开始创建Harbor项目。"
    USER_AUTHORIZATION=`echo -n ${DEW_HARBOR_USER_NAME}:${DEW_HARBOR_USER_PASS} | base64`

    create_project_result_code=`curl -X POST "${SCHEME}://${REGISTRY_HOST}/api/projects" -H "accept: application/json" -H "authorization: Basic ${USER_AUTHORIZATION}" -H "Content-Type: application/json" -d "{ \"project_name\": \"${PROJECT_NAMESPACE}\"}" -o /dev/nullrl -s -w %{http_code} -k`
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
        --docker-server=${REGISTRY_HOST} \
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
        read -p "后端host不符合规范，请重新输入：" -e backend_host
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
              path: /${backend_services[${b}+2]}/?(.*)
"
        let b=b+3
        backend_yaml_values+=${yaml_value}
    done

    echo
    read -e -p "请输入前端host： " frontend_host
    while [[ ! "${frontend_host}" =~ ^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*$ ]] ;do
        read -p "前端host不符合规范，请重新输入： " -e frontend_host
    done

    echo
    echo "# 请输入前端服务名、端口号、接口前缀。如有多组，请以空格分隔。参数请符合DNS规范。"
    echo "# 服务名（servicename）由小写字母，数字和中划线-组成，且应以字母开头。"
    echo "# 例： servicea 8080 api serviceb 8081 rest servicec 8090 manage"
    echo
    read -e -p "请输入前端服务,如有多个，请以空格分隔： " frontend_service
    frontend_services=(${frontend_service})

    while [[ "${#frontend_services[@]}"%3 -eq 1 || "${#frontend_services[@]}" -eq 0 ]]; do
        read -p "服务名称和服务端口不可缺少，请重新输入服务名、端口号、接口前缀： " -e frontend_service
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
              path: /${frontend_services[${f}+2]}/?(.*)
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


# ------------------
# Select an option
# ------------------
echo ""
echo "=================== Dew DevOps Script ==================="
echo ""


PS3='Choose your option: '

select option in "Init cluster" "Create a project"

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
      init_project_check
      project_create_check
      project_create
      break;;
    esac
done
