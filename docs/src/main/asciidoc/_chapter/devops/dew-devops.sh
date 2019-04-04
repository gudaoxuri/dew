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
    echo "--------------------------------------"
    echo "# Checking Kubernetes cluster status."
    node_ready_check=`kubectl get node | grep -w Ready | wc -l`

    node_not_ready_check=`kubectl get node | grep -w NotReady | wc -l`
    node_x509_check=`kubectl get node | grep x509 | wc -l`
    if [[ ${node_not_ready_check} -gt 0 || ${node_x509_check} -gt 0 ]] ; then
        echo "kubectl get node"
        echo "`kubectl get node`"
        echo
        echo "There are some Nodes NotReady!Please check your Kubernetes Cluster status."
        echo "--------------------------------------"
        exit;
    fi

    if [[ ${node_not_ready_check} -eq 0  && ${node_ready_check} -gt 0 ]] ; then
        echo "Nodes are ready."
    fi
    echo "--------------------------------------"
}


init_cluster(){
    echo "--------------------------------------"
    echo "# Initializing Kubernetes Cluster, creating the cluster role for service discovery."
    kubectl create clusterrole service-discovery-client \
    --verb=get,list,watch \
    --resource=pods,services,configmaps,endpoints
    echo "Kubernetes Cluster has initialized."
    echo "--------------------------------------"
    echo "=================================="
    exit;
}

SCHEME=https
REGISTRY_HOST=harbor.dew.ms
REGISTRY_ADMIN=admin
REGISTRY_ADMIN_PASSWORD=Harbor12345

DEW_NAMESPACE=devops-example
DEW_DOCKER_USER_NAME=${DEW_NAMESPACE}
DEW_DOCKER_USER_PASS=Dew\!123456
DEW_DOCKER_USER_EMAIL=${DEW_DOCKER_USER_NAME}@dew.ms

# ------------------
# Create a project
# ------------------

init_project_check(){
    echo "------------------------------------"
    echo "## Checking Harbor status..."
    echo
    read -e -p "Please input Harbor registry host： " registry_host
    if [[ ${registry_host} != "" ]]; then
        REGISTRY_HOST=${registry_host}
    else
        echo "* No Harbor registry host was entered, using the default Harbor registry host："
        echo "* ${REGISTRY_HOST}"
    fi
    echo "* The default scheme is https."
    harbor_registry_health_check="curl ${SCHEME}://${REGISTRY_HOST}/health -k"
    registry_status=`curl ${SCHEME}://${REGISTRY_HOST}/health -o /dev/nullrl -s -w %{http_code} -k`
    if [[ registry_status -ne 200 ]]; then
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
    echo "Tips: Before creating your project, you need to XXX your Kubernetes cluster for service discovery."
    echo "____________________________________"
    echo
    echo "Please input your harbor registry admin"
    read -e registry_admin
    if [[ ${registry_admin} = "" ]]; then
        echo "* No Harbor registry admin account was entered, using the default admin account: ${REGISTRY_ADMIN}"
    fi

    echo

    echo "Please input your Harbor registry admin account password."
    echo "# The password should have the length between 8 and 20,"
    echo "# and contain an uppercase letter, a lowercase letter and a number."
    read -p "Input your Harbor admin password: " -e -s registry_password
    echo

    check_password=`echo "${registry_password}" | grep -P --color '(?=^.{8,20}$)(?=^[^\s]*$)(?=.*\d)(?=.*[A-Z])(?=.*[a-z])'| wc -l`
    while [[ ${check_password} -eq 0 ]];do
        echo "The password format is not right, please retype: "
        read -e -s registry_password
        check_password=`echo "${registry_password}" | grep -P --color '(?=^.{8,20}$)(?=^[^\s]*$)(?=.*\d)(?=.*[A-Z])(?=.*[a-z])'| wc -l`
    done

    if [[ ${registry_admin}=="" ]]; then
        registry_admin=${REGISTRY_ADMIN}
    fi

    echo
    ADMIN_AUTHORIZATION=`echo -n ${registry_admin}:${registry_password} | base64`
    check_admin_status=`curl "${SCHEME}://${REGISTRY_HOST}/api/users" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -o /dev/nullrl -s -w %{http_code} -k`
    while [[ ${check_admin_status} -eq 401 || ${check_admin_status} -eq 403 ]];do
        echo "* Password or admin account maybe not right,or the account not admin.Please retype admin account and password."
        echo "Please input Harbor registry admin account: "
        read -e registry_admin
        echo "Please input password: "
        read -e -s registry_password
        ADMIN_AUTHORIZATION=`echo -n ${registry_admin}:${registry_password} | base64`
        check_admin_status=`curl "${SCHEME}://${REGISTRY_HOST}/api/users" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -o /dev/nullrl -s -w %{http_code} -k`
    done

    if [[ ${registry_admin} != "" ]]; then
        REGISTRY_ADMIN=${registry_admin}
    fi
    if [[ ${registry_password} != "" ]]; then
        REGISTRY_ADMIN_PASSWORD=${registry_password}
    fi
    echo

    echo "# The name of project would be used for creating harbor project, harbor user account and Kubernetes namespaces."
    echo "# Project name must consist of lower case alphanumeric characters or '-' "
    echo "# and must start and end with an alphanumeric characters,"
    echo "# and the length must be greater than two."
    read -e -p "Please input project name: " project_name

    while [[ ! ${project_name} =~ ^([a-z0-9]+-?[a-z0-9]+)+$ ]]; do
        read -e -p "Please input the right project name: " project_name
    done

    # Checking whether Kubernetes namespace exists.
    check_ns_exists=`kubectl get ns | grep -w ${project_name} | wc -l`
    while [[ ${check_ns_exists} -gt 0 ]];do
        echo "There is already existing the same namespace, please retype another project name."
        read -e project_name

        while [[ ! ${project_name} =~ ^([a-z0-9]+-?[a-z0-9]+)+$ ]]; do
            read -p "The project name format is not right,please retype:" -e project_name
        done

        check_ns_exists=`kubectl get ns | grep -w ${project_name} | wc -l`
    done
    # Checking whether Harbor project name exists.
    check_project_exists=`curl "${SCHEME}://${REGISTRY_HOST}/api/projects?name=${project_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${project_name} | wc -l`
    while [[ ${check_project_exists} -gt 0 ]];do
        read -p "The project name already exists, please retype again: " -e project_name

        while [[ ! ${project_name} =~ ^([a-z0-9]+-?[a-z0-9]+)+$ ]]; do
            read -p "The project name format is not right,please retype again: " -e project_name
        done

        check_ns_exists=`kubectl get ns | grep -w ${project_name} | wc -l`
        while [[ ${check_ns_exists} -gt 0 ]];do
            echo "There is already existing the same namespace, please retype another project name."
            read -e project_name
            check_ns_exists=`kubectl get ns | grep -w ${project_name} | wc -l`
        done

        check_project_exists=`curl "${SCHEME}://${REGISTRY_HOST}/api/projects?name=${project_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${project_name} | wc -l`
    done

    if [[ ${project_name} != "" ]]; then
        DEW_NAMESPACE=${project_name}
        DEW_DOCKER_USER_NAME=${DEW_NAMESPACE}
        DEW_DOCKER_USER_EMAIL=${DEW_DOCKER_USER_NAME}@dew.ms
    fi

    # Checking whether user account exists.
    check_user_exists=`curl "${SCHEME}://${REGISTRY_HOST}/api/users?username=${project_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${project_name} | wc -l`
    while [[ ${check_user_exists} -gt 0 ]]; do
        read -p "There is already existing the same Harbor user account with project name.Please input another user name to bind with your project: " -e user_name
        check_user_exists=`curl "${SCHEME}://${REGISTRY_HOST}/api/users?username=${user_name}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${user_name} | wc -l`
        DEW_DOCKER_USER_NAME=${user_name}
    done
    echo

    # The e-mail format checking.
    echo "# E-mail is used for binding the Harbor user account that you created above."
    emailRegex="^([a-zA-Z0-9_\-\.\+]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5})$"
    read -e -p "Please input the e-mail for your Harbor project user account: " user_email
    while [[ ! ${user_email} =~ ${emailRegex} ]]; do
    read -p "The e-mail format is not right, please retype again: " -e user_email
    done

    # Checking whether e-mail is registered.
    check_email_exists=`curl "${SCHEME}://${REGISTRY_HOST}/api/users?email=${user_email}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${user_email} | wc -l`
    while [[ ${check_email_exists} -gt 0 ]];do
        read -p "The e-mail is already registered, please retype another: " -e user_email
        while [[ ! ${user_email} =~ ${emailRegex} ]]; do
            read -p "The e-mail format is not right, please retype again:" -e user_email
        done
        check_email_exists=`curl "${SCHEME}://${REGISTRY_HOST}/api/users?email=${user_email}" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -k -s | grep -w ${user_email} | wc -l`
    done

    if [[ ${user_email} != "" ]]; then
        DEW_DOCKER_USER_EMAIL=${user_email}
    fi
}


project_create(){
    echo
    echo "# Starting to create the Harbor user account."
    ADMIN_AUTHORIZATION=`echo -n ${REGISTRY_ADMIN}:${REGISTRY_ADMIN_PASSWORD} | base64`

    create_user_result=`curl -X POST "${SCHEME}://${REGISTRY_HOST}/api/users" -H "accept: application/json" -H "authorization: Basic ${ADMIN_AUTHORIZATION}" -H "Content-Type: application/json" -d "{ \"email\": \"${DEW_DOCKER_USER_EMAIL}\", \"username\": \"${DEW_DOCKER_USER_NAME}\", \"password\": \"${DEW_DOCKER_USER_PASS}\", \"realname\": \"${DEW_DOCKER_USER_NAME}\", \"comment\": \"init\"}" -o /dev/nullrl -s -w %{http_code} -k`
    if [[ ${create_user_result} -ne 201 ]]; then
        echo "Failed to create user account, the script to end, please retry."
        exit;
    fi
    echo "Created Harbor user account [${DEW_DOCKER_USER_NAME}] successfully.The deafault password is ${DEW_DOCKER_USER_PASS}."
    echo

    echo "# Starting to create Harbor project."
    USER_AUTHORIZATION=`echo -n ${DEW_DOCKER_USER_NAME}:${DEW_DOCKER_USER_PASS} | base64`

    create_project_result_code=`curl -X POST "${SCHEME}://${REGISTRY_HOST}/api/projects" -H "accept: application/json" -H "authorization: Basic ${USER_AUTHORIZATION}" -H "Content-Type: application/json" -d "{ \"project_name\": \"${DEW_NAMESPACE}\"}" -o /dev/nullrl -s -w %{http_code} -k`
    if [[ ${create_project_result_code} -ne 201 ]]; then
        echo "Failed to create project, the script to end, please retry."
        exit;
    fi
    echo "The project [${DEW_NAMESPACE}] is created successfully."
    echo

    echo "# Starting to initialize the project in the Kubernetes Cluster."
    kubectl create namespace ${DEW_NAMESPACE}

    kubectl create rolebinding default:service-discovery-client \
        -n ${DEW_NAMESPACE} \
        --clusterrole service-discovery-client \
        --serviceaccount ${DEW_NAMESPACE}:default

    kubectl -n ${DEW_NAMESPACE} create secret docker-registry dew-registry \
        --docker-server=${REGISTRY_HOST} \
        --docker-username=${DEW_DOCKER_USER_NAME} \
        --docker-password=${DEW_DOCKER_USER_PASS} \
        --docker-email=${DEW_DOCKER_USER_EMAIL}

    kubectl -n ${DEW_NAMESPACE} patch serviceaccount default \
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
    #   namespace: $DEW_NAMESPACE
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
    echo "# kubectl edit ingress dew-ingress -n ${DEW_NAMESPACE}"
    echo
    read -e -p "Please input nginx rewrite target:" nginx_rewrite_target

    read -e -p "Input the host of your backend: " backend_host
    while [[ ! ${backend_host} =~ ^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*$ ]] ;do
        read -p "The host is not right,please retype: " -e backend_host
    done

    echo
    echo "# Please input your backend service name,service port and path in order."
    echo "# If you have one more service group, please user the space to separate."
    echo "# The service params should accord with the DNS label."
    echo "# Service name  must consist of lower case alphanumeric characters or '-', "
    echo "# start with an alphabetic character, and end with an alphanumeric character."
    echo "# e.g. services 8080 api serviceb 8081 rest servicec 8090 manage"
    echo
    read -e -p "Please input your backend services: " backend_service
    backend_services=(${backend_service})
    
    while [[ ${#backend_services[@]}%3 -eq 1 ]]; do
        read -p "Service port is indispensable, please retype your service params: " -e backend_service
        backend_services=(${backend_service})
    done

    b=0
    backend_yaml_values=""
    while [[ ${b} -lt ${#backend_services[@]} ]]; do
        while [[ ! ${backend_services[b]} =~ ^[a-z]([a-z0-9]*)(-[a-z0-9]+)*$ ]]; do
            echo "The service name ["${backend_services[b]}"] format is not right，please retype another: "
            read -e service_name
            backend_services[b]=${service_name}
        done

        while [[ ! ${backend_services[${b}+1]} =~ ^[a-z0-9]*$ ]]; do
            echo "The service port ["${backend_services[${b}+1]}"] format is not right，please retype another: "
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
    read -e -p "Please input your frontend host: " frontend_host
    while [[ ! ${frontend_host} =~ ^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*$ ]] ;do
        read -p "The host is not right, please retype again: " -e frontend_host
    done

    echo
    echo "# Please input your frontend service name,service port and path in order."
    echo "# If you have one more service group, please user the space to separate."
    echo "# The service params should accord with the DNS label."
    echo "# Service name  must consist of lower case alphanumeric characters or '-', "
    echo "# start with an alphabetic character, and end with an alphanumeric character."
    echo "# e.g. services 8080 api serviceb 8081 rest servicec 8090 manage"
    echo
    read -e -p "Please input your services: " frontend_service
    frontend_services=(${frontend_service})

    while [[ ${#frontend_services[@]}%3 -eq 1 ]]; do
        read -p "Service port is indispensable, please retype your service params: " -e frontend_service
        frontend_services=(${frontend_service})
    done

    f=0
    frontend_yaml_values=""
    while [[ ${f} -lt ${#frontend_services[@]} ]]; do
        while [[ ! ${frontend_services[f]} =~ ^[a-z]([a-z0-9]*)(-[a-z0-9]+)*$ ]]; do
            echo "The service name ["${frontend_services[f]}"] format is not right, please retype another: "
            read -e service_name
            frontend_services[f]=${service_name}
        done
        while [[ ! ${frontend_services[${f}+1]} =~ ^[a-z0-9]*$ ]]; do
            echo "Service Port ["${frontend_services[${f}+1]}"] format is not right, please retype another:"
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

    # Creating Ingress
    cat <<EOF | kubectl -n ${DEW_NAMESPACE} apply -f -
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
    check_ingress_exist=`kubectl get ing dew-ingress -n ${DEW_NAMESPACE} | wc -l`
    if [[ ${check_ingress_exist} -eq 0 ]]; then
        echo -e "\033[31m * Failed to created Ingress, the script to end. Please the Ingress yourself. \033[1;m"
        exit;
    else
        echo "The creating of project [${DEW_NAMESPACE}] is completed."
        echo "The script to end."
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
