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
# Create a project
# ------------------
project_create_prepare(){
    echo "----------------------------"
    echo "Project name can only be lowercase english alphabet / numbers / - "
    echo "Please input project name:"
    read project_name
    echo $project_name
}

# ------------------
# Init
# ------------------

# xxxx
REGISTRY_HOST=harbor.dew.env
REGISTRY_ADMIN=admin
REGISTRY_ADMIN_PASSWORD=Harbor12345

init_env_check(){
     echo "xxxxx"
    # exit
    init_env_show
}

init_env_show(){
  echo "dddd"
}

# ------------------
# Select a option
# ------------------
echo ""
echo "=================== Dew DevOps Script ==================="
echo ""

init_env_check

PS3='Choose your option: '

select option in "Create a project" "Remove a project" "Init cluster"

do
    case $option in
     'Create a project')
      echo "========== Create a Project =========="
      project_create_prepare
      break;;
     'Remove a project')
      echo "Standing by";;
     'Init cluster')
      break;;
    esac
done
