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

import com.ecfront.dew.common.$;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import io.kubernetes.client.Exec;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.PortForward;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.*;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import io.kubernetes.client.util.Watch;
import io.kubernetes.client.util.Yaml;
import org.slf4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Kubernetes操作函数类.
 *
 * @author gudaoxuri
 * @see <a href="https://github.com/kubernetes-client/java">Kubernetes Client</a>
 */
public class KubeOpt {

    /**
     * The Watch map.
     */
    private final Map<String, Watch> watchMap = new ConcurrentHashMap<>();
    /**
     * The Executor service.
     */
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Log.
     */
    protected Logger log;
    /**
     * kuberentes native API Client.
     */
    private ApiClient client;
    /**
     * kuberentes native Core api.
     */
    private CoreV1Api coreApi;
    /**
     * kuberentes native Apps api.
     */
    private AppsV1Api appsApi;
    /**
     * kuberentes native Networking api.
     */
    private NetworkingV1Api networkingV1Api;
    /**
     * kuberentes native RBAC authorization api.
     */
    private RbacAuthorizationV1Api rbacAuthorizationApi;
    /**
     * kuberentes native Autoscaling api.
     */
    private AutoscalingV2beta2Api autoscalingApi;
    /**
     * kuberentes native Pod logs.
     */
    private PodLogs podLogs;

    /**
     * Instantiates a new Kube opt.
     *
     * @param log              the log
     * @param base64KubeConfig the base64 kube config
     */
    protected KubeOpt(Logger log, String base64KubeConfig) {
        this.log = log;
        YamlHelper.init(log);
        try {
            client = Config.fromConfig(KubeConfig.loadKubeConfig(new StringReader($.security.decodeBase64ToString(base64KubeConfig, "UTF-8"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        client.setReadTimeout(0);
        Configuration.setDefaultApiClient(client);
        coreApi = new CoreV1Api(client);
        appsApi = new AppsV1Api(client);
        networkingV1Api = new NetworkingV1Api(client);
        rbacAuthorizationApi = new RbacAuthorizationV1Api(client);
        autoscalingApi = new AutoscalingV2beta2Api(client);
        podLogs = new PodLogs(client);
    }

    /**
     * Watch.
     * <p>
     *
     * @param <T>      the type parameter
     * @param call     the call
     * @param callback the callback
     * @param clazz    the clazz
     * @return watch Id
     * @throws ApiException the api exception
     */
    public <T> String watch(KubeWatchCall call, Consumer<Watch.Response<T>> callback, Class<T> clazz) throws ApiException {
        String watchId = $.field.createShortUUID();
        TypeToken<?> typeToken = null;
        if (clazz == V1Namespace.class) {
            typeToken = new TypeToken<Watch.Response<V1Namespace>>() {
            };
        } else if (clazz == V1Ingress.class) {
            typeToken = new TypeToken<Watch.Response<V1Ingress>>() {
            };
        } else if (clazz == V1Service.class) {
            typeToken = new TypeToken<Watch.Response<V1Service>>() {
            };
        } else if (clazz == V1ServiceAccount.class) {
            typeToken = new TypeToken<Watch.Response<V1ServiceAccount>>() {
            };
        } else if (clazz == V1Deployment.class) {
            typeToken = new TypeToken<Watch.Response<V1Deployment>>() {
            };
        } else if (clazz == V1Pod.class) {
            typeToken = new TypeToken<Watch.Response<V1Pod>>() {
            };
        } else if (clazz == V1Secret.class) {
            typeToken = new TypeToken<Watch.Response<V1Secret>>() {
            };
        } else if (clazz == V1ConfigMap.class) {
            typeToken = new TypeToken<Watch.Response<V1ConfigMap>>() {
            };
        } else if (clazz == V1DaemonSet.class) {
            typeToken = new TypeToken<Watch.Response<V1DaemonSet>>() {
            };
        } else if (clazz == V1Role.class) {
            typeToken = new TypeToken<Watch.Response<V1Role>>() {
            };
        } else if (clazz == V1RoleBinding.class) {
            typeToken = new TypeToken<Watch.Response<V1RoleBinding>>() {
            };
        } else if (clazz == V1ClusterRole.class) {
            typeToken = new TypeToken<Watch.Response<V1ClusterRole>>() {
            };
        } else if (clazz == V1ClusterRoleBinding.class) {
            typeToken = new TypeToken<Watch.Response<V1ClusterRoleBinding>>() {
            };
        }
        Watch<T> watch = Watch.createWatch(client, call.call(coreApi, appsApi, networkingV1Api, rbacAuthorizationApi, autoscalingApi),
                typeToken.getType());
        watchMap.put(watchId, watch);
        executorService.execute(() -> {
            try {
                watch.forEach(callback);
            } catch (RuntimeException e) {
                if (!watchMap.containsKey(watchId)) {
                    if (e instanceof IllegalStateException && e.getMessage() != null && e.getMessage().equalsIgnoreCase("closed")) {
                        // Do Nothing
                    } else {
                        throw e;
                    }
                } else {
                    throw e;
                }
            }
        });
        return watchId;
    }

    /**
     * Stop watch.
     *
     * @param watchId the watch id
     */
    public void stopWatch(String watchId) {
        Watch watch = watchMap.get(watchId);
        watchMap.remove(watchId);
        try {
            watch.close();
        } catch (Exception ignore) {
            log.warn("Stop watch error.", ignore);
        }
    }

    /**
     * To resource.
     *
     * @param <T>   the type parameter
     * @param body  the body
     * @param clazz the clazz
     * @return the resource
     */
    public <T> T toResource(String body, Class<T> clazz) {
        return Yaml.loadAs(body, clazz);
    }

    /**
     * To string.
     *
     * @param body the body
     * @return the resource
     */
    public String toString(Object body) {
        return Yaml.dump(body);
    }

    /**
     * Create.
     *
     * @param body the body
     * @throws ApiException the api exception
     */
    public void create(Object body) throws ApiException {
        create(Yaml.dump(body));
    }

    /**
     * Create.
     *
     * @param body the body
     * @throws ApiException the api exception
     */
    public void create(String body) throws ApiException {
        KubeBasicRes basicRes = YamlHelper.toObject(KubeBasicRes.class, body);
        try {
            switch (KubeRES.parse(basicRes.getKind())) {
                case NAME_SPACE:
                    var namespaceObj = Yaml.loadAs(body, V1Namespace.class);
                    coreApi.createNamespace(namespaceObj, null, null, null);
                    break;
                case INGRESS:
                    var ingressObj = Yaml.loadAs(body, V1Ingress.class);
                    networkingV1Api.createNamespacedIngress(ingressObj.getMetadata().getNamespace(), ingressObj, null, null, null);
                    break;
                case SERVICE:
                    var serviceObj = Yaml.loadAs(body, V1Service.class);
                    coreApi.createNamespacedService(serviceObj.getMetadata().getNamespace(), serviceObj, null, null, null);
                    break;
                case DEPLOYMENT:
                    var deploymentObj = Yaml.loadAs(body, V1Deployment.class);
                    appsApi.createNamespacedDeployment(deploymentObj.getMetadata().getNamespace(), deploymentObj, null, null, null);
                    break;
                case POD:
                    var podObj = Yaml.loadAs(body, V1Pod.class);
                    coreApi.createNamespacedPod(podObj.getMetadata().getNamespace(), podObj, null, null, null);
                    break;
                case SECRET:
                    var secretObj = Yaml.loadAs(body, V1Secret.class);
                    coreApi.createNamespacedSecret(secretObj.getMetadata().getNamespace(), secretObj, null, null, null);
                    break;
                case CONFIG_MAP:
                    var configMapObj = Yaml.loadAs(body, V1ConfigMap.class);
                    coreApi.createNamespacedConfigMap(configMapObj.getMetadata().getNamespace(), configMapObj, null, null, null);
                    break;
                case SERVICE_ACCOUNT:
                    var serviceAccountObj = Yaml.loadAs(body, V1ServiceAccount.class);
                    coreApi.createNamespacedServiceAccount(serviceAccountObj.getMetadata().getNamespace(), serviceAccountObj, null, null, null);
                    break;
                case DAEMON_SET:
                    var daemonSetObj = Yaml.loadAs(body, V1DaemonSet.class);
                    appsApi.createNamespacedDaemonSet(daemonSetObj.getMetadata().getNamespace(), daemonSetObj, null, null, null);
                    break;
                case ROLE:
                    var roleObj = Yaml.loadAs(body, V1Role.class);
                    rbacAuthorizationApi.createNamespacedRole(roleObj.getMetadata().getNamespace(), roleObj, null, null, null);
                    break;
                case ROLE_BINDING:
                    var roleBindingObj = Yaml.loadAs(body, V1RoleBinding.class);
                    rbacAuthorizationApi.createNamespacedRoleBinding(roleBindingObj.getMetadata().getNamespace(), roleBindingObj, null, null, null);
                    break;
                case CLUSTER_ROLE:
                    var clusterRoleObj = Yaml.loadAs(body, V1ClusterRole.class);
                    rbacAuthorizationApi.createClusterRole(clusterRoleObj, null, null, null);
                    break;
                case CLUSTER_ROLE_BINDING:
                    var clusterRoleBindingObj = Yaml.loadAs(body, V1ClusterRoleBinding.class);
                    rbacAuthorizationApi.createClusterRoleBinding(clusterRoleBindingObj, null, null, null);
                    break;
                case HORIZONTAL_POD_AUTOSCALER:
                    var hpaObj = Yaml.loadAs(body, V2beta2HorizontalPodAutoscaler.class);
                    autoscalingApi.createNamespacedHorizontalPodAutoscaler(hpaObj.getMetadata().getNamespace(), hpaObj, null, null, null);
                    break;
                default:
                    throw new ApiException("The resource [" + basicRes.getKind() + "] operation NOT implementation");
            }
        } catch (ApiException e) {
            log.error("Create error for \r\n" + Yaml.dump(body), e);
            throw e;
        }
    }

    /**
     * Exist.
     *
     * @param name the name
     * @param res  the res
     * @return <b>true</b> if exist
     * @throws ApiException the api exception
     */
    public boolean exist(String name, KubeRES res) throws ApiException {
        return exist(name, "", res);
    }

    /**
     * Exist.
     *
     * @param name      the name
     * @param namespace the namespace
     * @param res       the res
     * @return <b>true</b> if exist
     * @throws ApiException the api exception
     */
    public boolean exist(String name, String namespace, KubeRES res) throws ApiException {
        return read(name, namespace, res, String.class) != null;
    }

    /**
     * List.
     *
     * @param <T>   the type parameter
     * @param res   the res
     * @param clazz the clazz
     * @return resource list
     * @throws ApiException the api exception
     */
    public <T> List<T> list(KubeRES res, Class<T> clazz) throws ApiException {
        return list("", "", res, clazz);
    }

    /**
     * List.
     *
     * @param <T>           the type parameter
     * @param labelSelector the label selector
     * @param namespace     the namespace
     * @param res           the res
     * @param clazz         the clazz
     * @return resource list
     * @throws ApiException the api exception
     */
    public <T> List<T> list(String labelSelector, String namespace, KubeRES res, Class<T> clazz) throws ApiException {
        Object resource;
        switch (res) {
            case NAME_SPACE:
                resource = coreApi.listNamespace(null, true, null, null, labelSelector, Integer.MAX_VALUE, null, null, null, null).getItems();
                break;
            case INGRESS:
                resource = networkingV1Api.listNamespacedIngress(namespace, null, true, null, null, labelSelector, Integer.MAX_VALUE, null, null,
                        null, null).getItems();
                break;
            case SERVICE:
                resource = coreApi.listNamespacedService(namespace, null, true, null, null, labelSelector, Integer.MAX_VALUE, null, null, null,
                        null).getItems();
                break;
            case DEPLOYMENT:
                resource = appsApi.listNamespacedDeployment(namespace, null, true, null, null, labelSelector, Integer.MAX_VALUE, null, null, null,
                        null).getItems();
                break;
            case REPLICA_SET:
                resource = appsApi.listNamespacedReplicaSet(namespace, null, true, null, null, labelSelector, Integer.MAX_VALUE, null, null, null,
                        null).getItems();
                break;
            case POD:
                resource =
                        coreApi.listNamespacedPod(namespace, null, true, null, null, labelSelector, Integer.MAX_VALUE, null, null, null, null).getItems();
                break;
            case SECRET:
                resource = coreApi.listNamespacedSecret(namespace, null, true, null, null, labelSelector, Integer.MAX_VALUE, null, null, null,
                        null).getItems();
                break;
            case CONFIG_MAP:
                resource = coreApi.listNamespacedConfigMap(namespace, null, true, null, null, labelSelector, Integer.MAX_VALUE, null, null, null,
                        null).getItems();
                break;
            case SERVICE_ACCOUNT:
                resource = coreApi.listNamespacedServiceAccount(namespace, null, true, null, null, labelSelector, Integer.MAX_VALUE, null, null,
                        null, null).getItems();
                break;
            case DAEMON_SET:
                resource = appsApi.listNamespacedDaemonSet(namespace, null, true, null, null, labelSelector, Integer.MAX_VALUE, null, null, null,
                        null).getItems();
                break;
            case ROLE:
                resource = rbacAuthorizationApi.listNamespacedRole(namespace, null, true, null, null, labelSelector, Integer.MAX_VALUE, null, null,
                        null, null).getItems();
                break;
            case ROLE_BINDING:
                resource = rbacAuthorizationApi.listNamespacedRoleBinding(namespace, null, true, null, null, labelSelector, Integer.MAX_VALUE, null
                        , null, null, null).getItems();
                break;
            case CLUSTER_ROLE:
                resource =
                        rbacAuthorizationApi.listClusterRole(null, true, null, null, labelSelector, Integer.MAX_VALUE, null, null, null, null).getItems();
                break;
            case CLUSTER_ROLE_BINDING:
                resource = rbacAuthorizationApi.listClusterRoleBinding(null, true, null, null, labelSelector, Integer.MAX_VALUE, null, null, null,
                        null).getItems();
                break;
            case HORIZONTAL_POD_AUTOSCALER:
                resource = autoscalingApi.listNamespacedHorizontalPodAutoscaler(namespace, null, true, null, null, labelSelector, Integer.MAX_VALUE
                        , null, null, null, null).getItems();
                break;
            default:
                throw new ApiException("The resource [" + res.getVal() + "] operation NOT implementation");
        }
        if (clazz == String.class) {
            return (List<T>) ((List) resource).stream().map(item -> Yaml.dump(item)).collect(Collectors.toList());
        } else {
            return (List<T>) resource;
        }
    }

    /**
     * Read.
     *
     * @param <T>   the type parameter
     * @param name  the name
     * @param res   the res
     * @param clazz the clazz
     * @return the resource
     * @throws ApiException the api exception
     */
    public <T> T read(String name, KubeRES res, Class<T> clazz) throws ApiException {
        return read(name, "", res, clazz);
    }

    /**
     * Read.
     *
     * @param <T>       the type parameter
     * @param name      the name
     * @param namespace the namespace
     * @param res       the res
     * @param clazz     the clazz
     * @return the resource
     * @throws ApiException the api exception
     */
    public <T> T read(String name, String namespace, KubeRES res, Class<T> clazz) throws ApiException {
        Object resource;
        try {
            switch (res) {
                case NAME_SPACE:
                    resource = coreApi.readNamespace(name, null);
                    break;
                case INGRESS:
                    resource = networkingV1Api.readNamespacedIngress(name, namespace, null);
                    break;
                case SERVICE:
                    resource = coreApi.readNamespacedService(name, namespace, null);
                    break;
                case DEPLOYMENT:
                    resource = appsApi.readNamespacedDeployment(name, namespace, null);
                    break;
                case REPLICA_SET:
                    resource = appsApi.readNamespacedReplicaSet(name, namespace, null);
                    break;
                case POD:
                    resource = coreApi.readNamespacedPod(name, namespace, null);
                    break;
                case SECRET:
                    resource = coreApi.readNamespacedSecret(name, namespace, null);
                    break;
                case CONFIG_MAP:
                    resource = coreApi.readNamespacedConfigMap(name, namespace, null);
                    break;
                case SERVICE_ACCOUNT:
                    resource = coreApi.readNamespacedServiceAccount(name, namespace, null);
                    break;
                case DAEMON_SET:
                    resource = appsApi.readNamespacedDaemonSet(name, namespace, null);
                    break;
                case ROLE:
                    resource = rbacAuthorizationApi.readNamespacedRole(name, namespace, null);
                    break;
                case ROLE_BINDING:
                    resource = rbacAuthorizationApi.readNamespacedRoleBinding(name, namespace, null);
                    break;
                case CLUSTER_ROLE:
                    resource = rbacAuthorizationApi.readClusterRole(name, null);
                    break;
                case CLUSTER_ROLE_BINDING:
                    resource = rbacAuthorizationApi.readClusterRoleBinding(name, null);
                    break;
                case HORIZONTAL_POD_AUTOSCALER:
                    resource = autoscalingApi.readNamespacedHorizontalPodAutoscaler(name, namespace, null);
                    break;
                default:
                    throw new ApiException("The resource [" + res.getVal() + "] operation NOT implementation");
            }
            if (clazz == String.class) {
                return (T) Yaml.dump(resource);
            } else {
                return (T) resource;
            }
        } catch (ApiException e) {
            // TODO 优雅的判断是否存在
            if (e.getCode() == 404) {
                return null;
            }
            throw e;
        }
    }

    /**
     * Replace.
     *
     * @param body the body
     * @throws ApiException the api exception
     */
    public void replace(Object body) throws ApiException {
        replace(Yaml.dump(body));
    }

    /**
     * Replace.
     *
     * @param body the body
     * @throws ApiException the api exception
     */
    public void replace(String body) throws ApiException {
        KubeBasicRes basicRes = YamlHelper.toObject(KubeBasicRes.class, body);
        try {
            switch (KubeRES.parse(basicRes.getKind())) {
                case NAME_SPACE:
                    var namespaceObj = Yaml.loadAs(body, V1Namespace.class);
                    coreApi.replaceNamespace(namespaceObj.getMetadata().getName(), namespaceObj, null, null, null);
                    break;
                case INGRESS:
                    var ingressObj = Yaml.loadAs(body, V1Ingress.class);
                    networkingV1Api.replaceNamespacedIngress(ingressObj.getMetadata().getName(), ingressObj.getMetadata().getNamespace(),
                            ingressObj, null, null, null);
                    break;
                case SERVICE:
                    var serviceObj = Yaml.loadAs(body, V1Service.class);
                    coreApi.replaceNamespacedService(serviceObj.getMetadata().getName(), serviceObj.getMetadata().getNamespace(), serviceObj, null,
                            null, null);
                    break;
                case DEPLOYMENT:
                    var deploymentObj = Yaml.loadAs(body, V1Deployment.class);
                    appsApi.replaceNamespacedDeployment(deploymentObj.getMetadata().getName(), deploymentObj.getMetadata().getNamespace(),
                            deploymentObj, null, null, null);
                    break;
                case POD:
                    var podObj = Yaml.loadAs(body, V1Pod.class);
                    coreApi.replaceNamespacedPod(podObj.getMetadata().getName(), podObj.getMetadata().getNamespace(), podObj, null, null, null);
                    break;
                case SECRET:
                    var secretObj = Yaml.loadAs(body, V1Secret.class);
                    coreApi.replaceNamespacedSecret(secretObj.getMetadata().getName(), secretObj.getMetadata().getNamespace(), secretObj, null,
                            null, null);
                    break;
                case CONFIG_MAP:
                    var configMapObj = Yaml.loadAs(body, V1ConfigMap.class);
                    coreApi.replaceNamespacedConfigMap(configMapObj.getMetadata().getName(), configMapObj.getMetadata().getNamespace(),
                            configMapObj, null, null, null);
                    break;
                case SERVICE_ACCOUNT:
                    var serviceAccountObj = Yaml.loadAs(body, V1ServiceAccount.class);
                    coreApi.replaceNamespacedServiceAccount(serviceAccountObj.getMetadata().getName(),
                            serviceAccountObj.getMetadata().getNamespace(), serviceAccountObj, null, null, null);
                    break;
                case DAEMON_SET:
                    var daemonSetObj = Yaml.loadAs(body, V1DaemonSet.class);
                    appsApi.replaceNamespacedDaemonSet(daemonSetObj.getMetadata().getName(), daemonSetObj.getMetadata().getNamespace(),
                            daemonSetObj, null, null, null);
                    break;
                case ROLE:
                    var roleObj = Yaml.loadAs(body, V1Role.class);
                    rbacAuthorizationApi.replaceNamespacedRole(roleObj.getMetadata().getName(), roleObj.getMetadata().getNamespace(), roleObj, null
                            , null, null);
                    break;
                case ROLE_BINDING:
                    var roleBindingObj = Yaml.loadAs(body, V1RoleBinding.class);
                    rbacAuthorizationApi.replaceNamespacedRoleBinding(roleBindingObj.getMetadata().getName(),
                            roleBindingObj.getMetadata().getNamespace(), roleBindingObj, null, null, null);
                    break;
                case CLUSTER_ROLE:
                    var clusterRoleObj = Yaml.loadAs(body, V1ClusterRole.class);
                    rbacAuthorizationApi.replaceClusterRole(clusterRoleObj.getMetadata().getName(), clusterRoleObj, null, null, null);
                    break;
                case CLUSTER_ROLE_BINDING:
                    var clusterRoleBindingObj = Yaml.loadAs(body, V1ClusterRoleBinding.class);
                    rbacAuthorizationApi.replaceClusterRoleBinding(clusterRoleBindingObj.getMetadata().getName(), clusterRoleBindingObj, null, null
                            , null);
                    break;
                case HORIZONTAL_POD_AUTOSCALER:
                    var hpaObj = Yaml.loadAs(body, V2beta2HorizontalPodAutoscaler.class);
                    autoscalingApi.replaceNamespacedHorizontalPodAutoscaler(hpaObj.getMetadata().getName(), hpaObj.getMetadata().getNamespace(),
                            hpaObj, null, null, null);
                    break;
                default:
                    throw new ApiException("The resource [" + basicRes.getKind() + "] operation NOT implementation");
            }
        } catch (ApiException e) {
            log.error("Replace error for \r\n" + Yaml.dump(body), e);
            throw e;
        }
    }

    /**
     * Patch.
     *
     * @param name     the name
     * @param patchers the patchers
     * @param res      the res
     * @throws ApiException the api exception
     */
    public void patch(String name, List<String> patchers, KubeRES res) throws ApiException {
        patch(name, patchers, "", res);
    }

    /**
     * Patch.
     *
     * @param name      the name
     * @param patchers  the patchers
     * @param namespace the namespace
     * @param res       the res
     * @throws ApiException the api exception
     * @see <a href="http://jsonpatch.com/">Jsonpatch</a>
     */
    public void patch(String name, List<String> patchers, String namespace, KubeRES res) throws ApiException {
        String jsonPatchers = patchers.stream().collect(Collectors.joining(",", "[", "]"));
        V1Patch v1Patch = new V1Patch(jsonPatchers);
        try {
            switch (res) {
                case NAME_SPACE:
                    coreApi.patchNamespace(name, v1Patch, null, null, null, null);
                    break;
                case INGRESS:
                    networkingV1Api.patchNamespacedIngress(name, namespace, v1Patch, null, null, null, null);
                    break;
                case SERVICE:
                    coreApi.patchNamespacedService(name, namespace, v1Patch, null, null, null, null);
                    break;
                case DEPLOYMENT:
                    appsApi.patchNamespacedDeployment(name, namespace, v1Patch, null, null, null, null);
                    break;
                case POD:
                    coreApi.patchNamespacedPod(name, namespace, v1Patch, null, null, null, null);
                    break;
                case SECRET:
                    coreApi.patchNamespacedSecret(name, namespace, v1Patch, null, null, null, null);
                    break;
                case CONFIG_MAP:
                    coreApi.patchNamespacedConfigMap(name, namespace, v1Patch, null, null, null, null);
                    break;
                case SERVICE_ACCOUNT:
                    coreApi.patchNamespacedServiceAccount(name, namespace, v1Patch, null, null, null, null);
                    break;
                case DAEMON_SET:
                    appsApi.patchNamespacedDaemonSet(name, namespace, v1Patch, null, null, null, null);
                    break;
                case ROLE:
                    rbacAuthorizationApi.patchNamespacedRole(name, namespace, v1Patch, null, null, null, null);
                    break;
                case ROLE_BINDING:
                    rbacAuthorizationApi.patchNamespacedRoleBinding(name, namespace, v1Patch, null, null, null, null);
                    break;
                case CLUSTER_ROLE:
                    rbacAuthorizationApi.patchClusterRole(name, v1Patch, null, null, null, null);
                    break;
                case CLUSTER_ROLE_BINDING:
                    rbacAuthorizationApi.patchClusterRoleBinding(name, v1Patch, null, null, null, null);
                    break;
                case HORIZONTAL_POD_AUTOSCALER:
                    autoscalingApi.patchNamespacedHorizontalPodAutoscaler(name, namespace, v1Patch, null, null, null, null);
                    break;
                default:
                    throw new ApiException("The resource [" + res.getVal() + "] operation NOT implementation");
            }
        } catch (ApiException e) {
            log.error("Patch error for \r\n" + $.json.toJsonString(patchers), e);
            throw e;
        }
    }

    /**
     * Apply.
     *
     * @param body the body
     * @throws ApiException the api exception
     */
    public void apply(Object body) throws ApiException {
        apply(Yaml.dump(body));
    }

    /**
     * Apply.
     *
     * @param body the body
     * @throws ApiException the api exception
     */
    public void apply(String body) throws ApiException {
        KubeBasicRes basicRes = YamlHelper.toObject(KubeBasicRes.class, body);
        if (exist(basicRes.getMetadata().getName(), basicRes.getMetadata().getNamespace(), KubeRES.parse(basicRes.getKind()))) {
            replace(body);
        } else {
            create(body);
        }
    }

    /**
     * Fetch log.
     *
     * @param name      the name
     * @param namespace the namespace
     * @return log list
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    public List<String> log(String name, String namespace) throws ApiException, IOException {
        return log(name, null, namespace, 0);
    }

    /**
     * Fetch Log.
     *
     * @param name      the name
     * @param namespace the namespace
     * @param tailLines the tail lines
     * @return log list
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    public List<String> log(String name, String namespace, int tailLines) throws ApiException, IOException {
        return log(name, null, namespace, tailLines);
    }

    /**
     * Fetch Log.
     *
     * @param name      the name
     * @param container the container
     * @param namespace the namespace
     * @return the list
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    public List<String> log(String name, String container, String namespace) throws ApiException, IOException {
        return log(name, container, namespace, 0);
    }

    /**
     * Fetch Log.
     *
     * @param name      the name
     * @param container the container
     * @param namespace the namespace
     * @param tailLines the tail lines
     * @return log list
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    public List<String> log(String name, String container, String namespace, int tailLines) throws ApiException, IOException {
        List<String> logResult = new CopyOnWriteArrayList<>();
        Closeable closeable = log(name, container, namespace, logResult::add, tailLines);
        try {
            // 等待日志收集
            Thread.sleep(2000);
            int length = 0;
            while (true) {
                if (logResult.size() == length) {
                    // 近似认为日志输出到末尾
                    // TODO 更优雅地判断日志是否到末尾
                    closeable.close();
                    return logResult;
                } else {
                    length = logResult.size();
                }
                Thread.sleep(1);
            }
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
            return logResult;
        }
    }

    /**
     * Watch Log.
     * <p>
     * 需要手工关闭
     *
     * @param name          the name
     * @param container     the container
     * @param namespace     the namespace
     * @param tailFollowFun the tail follow fun
     * @return the closeable
     * @throws ApiException the api exception
     */
    public Closeable log(String name, String container, String namespace, Consumer<String> tailFollowFun) throws ApiException {
        return log(name, container, namespace, tailFollowFun, 0);
    }

    /**
     * Watch Log.
     * <p>
     * 需要手工关闭
     *
     * @param name          the name
     * @param container     the container
     * @param namespace     the namespace
     * @param tailFollowFun the tail follow fun
     * @param tailLines     the tail lines
     * @return the closeable
     * @throws ApiException the api exception
     */
    public Closeable log(String name, String container, String namespace, Consumer<String> tailFollowFun, int tailLines) throws ApiException {
        if (container == null) {
            container = read(name, namespace, KubeRES.POD, V1Pod.class).getSpec().getContainers().get(0).getName();
        }
        String finalContainer = container;
        try {
            AtomicBoolean closed = new AtomicBoolean(false);
            InputStream is = podLogs.streamNamespacedPodLog(namespace, name, finalContainer, null, tailLines == 0 ? null : tailLines, false);
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            executorService.execute(() -> {
                try {
                    String msg;
                    while ((msg = r.readLine()) != null) {
                        tailFollowFun.accept(msg);
                    }
                } catch (IOException e) {
                    if (!closed.get()) {
                        log.error("Output log error", e);
                    }
                } finally {
                    try {
                        closed.set(true);
                        is.close();
                        r.close();
                    } catch (IOException e) {
                        log.error("Close log stream error", e);
                    }
                }
            });
            return () -> {
                closed.set(true);
                is.close();
                r.close();
            };
        } catch (IOException e) {
            log.error("Output log error", e);
        }
        return () -> {
        };
    }

    /**
     * Delete.
     *
     * @param name the name
     * @param res  the res
     * @throws ApiException the api exception
     */
    public void delete(String name, KubeRES res) throws ApiException {
        delete(name, "", res);
    }

    /**
     * Delete.
     *
     * @param name      the name
     * @param namespace the namespace
     * @param res       the res
     * @throws ApiException the api exception
     */
    public void delete(String name, String namespace, KubeRES res) throws ApiException {
        if (!exist(name, namespace, res)) {
            return;
        }
        V1DeleteOptions deleteOptions = new V1DeleteOptions();
        try {
            switch (res) {
                case NAME_SPACE:
                    coreApi.deleteNamespace(name, null, null, null, null, null, deleteOptions);
                    break;
                case INGRESS:
                    networkingV1Api.deleteNamespacedIngress(name, namespace, null, null, null, null, null, deleteOptions);
                    break;
                case SERVICE:
                    coreApi.deleteNamespacedService(name, namespace, null, null, null, null, null, deleteOptions);
                    break;
                case DEPLOYMENT:
                    // 后台级联删除（按照Deployment->ReplicaSet->Pod的顺序进行删除）
                    deleteOptions.setPropagationPolicy("Background");
                    appsApi.deleteNamespacedDeployment(name, namespace, null, null, null, null, null, deleteOptions);
                    break;
                case REPLICA_SET:
                    appsApi.deleteNamespacedReplicaSet(name, namespace, null, null, null, null, null, deleteOptions);
                    break;
                case POD:
                    coreApi.deleteNamespacedPod(name, namespace, null, null, null, null, null, deleteOptions);
                    break;
                case SECRET:
                    coreApi.deleteNamespacedSecret(name, namespace, null, null, null, null, null, deleteOptions);
                    break;
                case CONFIG_MAP:
                    coreApi.deleteNamespacedConfigMap(name, namespace, null, null, null, null, null, deleteOptions);
                    break;
                case SERVICE_ACCOUNT:
                    coreApi.deleteNamespacedServiceAccount(name, namespace, null, null, null, null, null, deleteOptions);
                    break;
                case DAEMON_SET:
                    appsApi.deleteNamespacedDaemonSet(name, namespace, null, null, null, null, null, deleteOptions);
                    break;
                case ROLE:
                    rbacAuthorizationApi.deleteNamespacedRole(name, namespace, null, null, null, null, null, deleteOptions);
                    break;
                case ROLE_BINDING:
                    rbacAuthorizationApi.deleteNamespacedRoleBinding(name, namespace, null, null, null, null, null, deleteOptions);
                    break;
                case CLUSTER_ROLE:
                    rbacAuthorizationApi.deleteClusterRole(name, null, null, null, null, null, deleteOptions);
                    break;
                case CLUSTER_ROLE_BINDING:
                    rbacAuthorizationApi.deleteClusterRoleBinding(name, null, null, null, null, null, deleteOptions);
                    break;
                case HORIZONTAL_POD_AUTOSCALER:
                    autoscalingApi.deleteNamespacedHorizontalPodAutoscaler(name, namespace, null, null, null, null, null, deleteOptions);
                    break;
                default:
                    throw new ApiException("The resource [" + res.getVal() + "] operation NOT implementation");
            }
        } catch (JsonSyntaxException e) {
            // Swagger Bug https://github.com/kubernetes-client/java/issues/86
            if (e.getCause() instanceof IllegalStateException) {
                IllegalStateException ise = (IllegalStateException) e.getCause();
                if (ise.getMessage() == null || !ise.getMessage().contains("Expected a string but was BEGIN_OBJECT")) {
                    throw e;
                }
            } else {
                throw e;
            }
        }
        boolean exist = exist(name, namespace, res);
        while (exist) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
            exist = exist(name, namespace, res);
        }
    }

    /**
     * Exec command.
     *
     * @param name      the name
     * @param container the container
     * @param namespace the namespace
     * @param cmd       the cmd
     * @return result list
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    public List<String> exec(String name, String container, String namespace, String[] cmd) throws ApiException, IOException {
        List<String> result = new CopyOnWriteArrayList<>();
        exec(name, container, namespace, cmd, result::add, true).close();
        return result;
    }

    /**
     * Exec command.
     * <p>
     * 需要手工关闭
     *
     * @param name      the name
     * @param container the container
     * @param namespace the namespace
     * @param cmd       command
     * @param outputFun output fun
     * @return the closeable
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    public Closeable exec(String name, String container, String namespace, String[] cmd, Consumer<String> outputFun) throws ApiException,
            IOException {
        return exec(name, container, namespace, cmd, outputFun, false);
    }

    /**
     * Exec command.
     *
     * @param name      the name
     * @param container the container
     * @param namespace the namespace
     * @param cmd       command
     * @param outputFun output fun
     * @param waiting   if <b>true</b> waiting unit execute finish
     * @return the closeable
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    private Closeable exec(String name, String container, String namespace, String[] cmd, Consumer<String> outputFun, boolean waiting) throws ApiException, IOException {
        if (container == null) {
            container = read(name, namespace, KubeRES.POD, V1Pod.class).getSpec().getContainers().get(0).getName();
        }
        String finalContainer = container;
        try {
            AtomicBoolean closed = new AtomicBoolean(false);
            CountDownLatch cdl = new CountDownLatch(1);
            final Process proc = new Exec().exec(namespace, name, cmd, finalContainer, true, true);
            executorService.execute(() -> {
                try {
                    ByteStreams.copy(System.in, proc.getOutputStream());
                } catch (IOException e) {
                    if (!closed.get()) {
                        log.error("Exec error", e);
                    }
                }
            });
            executorService.execute(() -> {
                try {
                    BufferedReader outputReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                    String outputLine;
                    while ((outputLine = outputReader.readLine()) != null) {
                        outputFun.accept(outputLine);
                    }
                } catch (IOException e) {
                    if (!closed.get()) {
                        log.error("Exec error", e);
                    }
                } finally {
                    cdl.countDown();
                }
            });
            if (waiting) {
                cdl.await();
            }
            return () -> {
                closed.set(true);
                proc.destroy();
            };
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Exec error", e);
        }
        return () -> {
        };
    }


    /**
     * Forward.
     *
     * @param name        the name
     * @param namespace   the namespace
     * @param innerPort   the inner port
     * @param forwardPort the forward port
     * @return the closeable
     * @throws IOException  the io exception
     * @throws ApiException the api exception
     */
    public Closeable forward(String name, String namespace, int innerPort, int forwardPort) throws IOException, ApiException {
        AtomicBoolean closed = new AtomicBoolean(false);
        PortForward.PortForwardResult result = new PortForward().forward(namespace, name, new ArrayList<>() {
            {
                add(forwardPort);
                add(innerPort);
            }
        });
        ServerSocket ss = new ServerSocket(forwardPort);
        AtomicReference<Socket> s = new AtomicReference<>();
        executorService.execute(() -> {
            try {
                while (!closed.get()) {
                    s.set(ss.accept());
                    ByteStreams.copy(s.get().getInputStream(), result.getOutboundStream(innerPort));
                }
            } catch (IOException e) {
                if (!closed.get()) {
                    log.error("Froward error", e);
                }
            }
        });
        executorService.execute(() -> {
            try {
                while (!closed.get()) {
                    if (s.get() != null) {
                        ByteStreams.copy(result.getInputStream(innerPort), s.get().getOutputStream());
                    }
                }
            } catch (IOException e) {
                if (!closed.get()) {
                    log.error("Froward error", e);
                }
            }
        });
        log.info("Connect address: <Current Host> <" + forwardPort + ">");
        return () -> {
            closed.set(true);
            s.get().close();
            ss.close();
        };
    }

}

