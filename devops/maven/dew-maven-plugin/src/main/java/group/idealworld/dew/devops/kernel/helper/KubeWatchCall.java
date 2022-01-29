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

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.*;
import okhttp3.Call;

/**
 * Kubernetes watch 回调.
 *
 * @author gudaoxuri
 */
@FunctionalInterface
public interface KubeWatchCall {


    /**
     * Call.
     *
     * @param coreApi              the core api
     * @param appsApi              the apps api
     * @param networkingV1Api      the networking api
     * @param rbacAuthorizationApi the rbac authorization api
     * @param autoscalingApi       the autoscaling api
     * @return the call
     * @throws ApiException the api exception
     */
    Call call(CoreV1Api coreApi, AppsV1Api appsApi, NetworkingV1Api networkingV1Api,
              RbacAuthorizationV1Api rbacAuthorizationApi, AutoscalingV2beta2Api autoscalingApi) throws ApiException;

}
