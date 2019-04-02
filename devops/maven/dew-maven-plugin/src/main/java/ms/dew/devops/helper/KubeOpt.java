/*
 * Copyright 2019. the original author or authors.
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

package ms.dew.devops.helper;

import com.ecfront.dew.common.$;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Call;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.apis.AutoscalingV2beta2Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.apis.RbacAuthorizationV1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import io.kubernetes.client.util.Watch;
import io.kubernetes.client.util.Yaml;
import org.apache.maven.plugin.logging.Log;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Kubernetes操作函数类
 *
 * @link https://github.com/kubernetes-client/java
 */
public class KubeOpt {

    protected final Map<String, Watch> WATCH_LIST = new ConcurrentHashMap<>();
    protected final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    protected Log log;
    protected ApiClient client;
    protected CoreV1Api coreApi;
    protected ExtensionsV1beta1Api extensionsApi;
    protected RbacAuthorizationV1Api rbacAuthorizationApi;
    protected AutoscalingV2beta2Api autoscalingApi;
    protected PodLogs podLogs;

    protected KubeOpt(Log log, String base64KubeConfig) {
        this.log = log;
        YamlHelper.init(log);
        try {
            client = Config.fromConfig(
                    KubeConfig.loadKubeConfig(
                            new StringReader(
                                    $.security.decodeBase64ToString(base64KubeConfig, "UTF-8")
                            )
                    )
            );
        } catch (IOException ignore) {
        }
        Configuration.setDefaultApiClient(client);
        client.getHttpClient().setReadTimeout(0, TimeUnit.MILLISECONDS);
        coreApi = new CoreV1Api(client);
        extensionsApi = new ExtensionsV1beta1Api(client);
        rbacAuthorizationApi = new RbacAuthorizationV1Api(client);
        autoscalingApi = new AutoscalingV2beta2Api(client);
        podLogs = new PodLogs(client);
    }

    public <T> String watch(WatchCall call, Consumer<Watch.Response<T>> callback, Class<T> clazz) throws ApiException {
        String watchId = $.field.createShortUUID();
        TypeToken typeToken = null;
        if (clazz == V1Namespace.class) {
            typeToken = new TypeToken<Watch.Response<V1Namespace>>() {
            };
        } else if (clazz == V1beta1Ingress.class) {
            typeToken = new TypeToken<Watch.Response<V1beta1Ingress>>() {
            };
        } else if (clazz == V1Service.class) {
            typeToken = new TypeToken<Watch.Response<V1Service>>() {
            };
        } else if (clazz == V1ServiceAccount.class) {
            typeToken = new TypeToken<Watch.Response<V1ServiceAccount>>() {
            };
        } else if (clazz == ExtensionsV1beta1Deployment.class) {
            typeToken = new TypeToken<Watch.Response<ExtensionsV1beta1Deployment>>() {
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
        } else if (clazz == V1beta1DaemonSet.class) {
            typeToken = new TypeToken<Watch.Response<V1beta1DaemonSet>>() {
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
        Watch<T> watch = Watch.createWatch(client, call.call(coreApi, extensionsApi,
                rbacAuthorizationApi, autoscalingApi), typeToken.getType());
        WATCH_LIST.put(watchId, watch);
        EXECUTOR_SERVICE.execute(() -> {
            try {
                watch.forEach(callback);
            } catch (RuntimeException e) {
                if (!WATCH_LIST.containsKey(watchId)) {
                    if (e instanceof IllegalStateException
                            && e.getMessage() != null
                            && e.getMessage().equalsIgnoreCase("closed")
                            || e.getMessage() != null
                            && e.getMessage().equals("IO Exception during hasNext method.")) {
                        // https://github.com/kubernetes-client/java/issues/259
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

    public void stopWatch(String watchId) throws IOException {
        Watch watch = WATCH_LIST.get(watchId);
        WATCH_LIST.remove(watchId);
        watch.close();
    }

    public <T> T toResource(String body, Class<T> clazz) {
        return Yaml.loadAs(body, clazz);
    }

    public String toString(Object body) {
        return Yaml.dump(body);
    }

    public void create(Object body) throws ApiException {
        create(Yaml.dump(body));
    }

    public void create(String body) throws ApiException {
        KubeBasicRes basicRes = YamlHelper.toObject(KubeBasicRes.class, body);
        try {
            switch (RES.parse(basicRes.getKind())) {
                case NAME_SPACE:
                    V1Namespace namespaceObj = Yaml.loadAs(body, V1Namespace.class);
                    coreApi.createNamespace(namespaceObj, false, "true", null);
                    break;
                case INGRESS:
                    V1beta1Ingress ingressObj = Yaml.loadAs(body, V1beta1Ingress.class);
                    extensionsApi.createNamespacedIngress(ingressObj.getMetadata().getNamespace(), ingressObj, false, "true", null);
                    break;
                case SERVICE:
                    V1Service serviceObj = Yaml.loadAs(body, V1Service.class);
                    coreApi.createNamespacedService(serviceObj.getMetadata().getNamespace(), serviceObj, false, "true", null);
                    break;
                case DEPLOYMENT:
                    ExtensionsV1beta1Deployment deploymentObj = Yaml.loadAs(body, ExtensionsV1beta1Deployment.class);
                    extensionsApi.createNamespacedDeployment(deploymentObj.getMetadata().getNamespace(), deploymentObj, false, "true", null);
                    break;
                case POD:
                    V1Pod podObj = Yaml.loadAs(body, V1Pod.class);
                    coreApi.createNamespacedPod(podObj.getMetadata().getNamespace(), podObj, false, "true", null);
                    break;
                case SECRET:
                    V1Secret secretObj = Yaml.loadAs(body, V1Secret.class);
                    coreApi.createNamespacedSecret(secretObj.getMetadata().getNamespace(), secretObj, false, "true", null);
                    break;
                case CONFIG_MAP:
                    V1ConfigMap configMapObj = Yaml.loadAs(body, V1ConfigMap.class);
                    coreApi.createNamespacedConfigMap(configMapObj.getMetadata().getNamespace(), configMapObj, false, "true", null);
                    break;
                case SERVICE_ACCOUNT:
                    V1ServiceAccount serviceAccountObj = Yaml.loadAs(body, V1ServiceAccount.class);
                    coreApi.createNamespacedServiceAccount(serviceAccountObj.getMetadata().getNamespace(), serviceAccountObj, false, "true", null);
                    break;
                case DAEMON_SET:
                    V1beta1DaemonSet daemonSetObj = Yaml.loadAs(body, V1beta1DaemonSet.class);
                    extensionsApi.createNamespacedDaemonSet(daemonSetObj.getMetadata().getNamespace(), daemonSetObj, false, "true", null);
                    break;
                case ROLE:
                    V1Role roleObj = Yaml.loadAs(body, V1Role.class);
                    rbacAuthorizationApi.createNamespacedRole(roleObj.getMetadata().getNamespace(), roleObj, false, "true", null);
                    break;
                case RULE_BINDING:
                    V1RoleBinding roleBindingObj = Yaml.loadAs(body, V1RoleBinding.class);
                    rbacAuthorizationApi.createNamespacedRoleBinding(roleBindingObj.getMetadata().getNamespace(), roleBindingObj, false, "true", null);
                    break;
                case CLUSTER_ROLE:
                    V1ClusterRole clusterRoleObj = Yaml.loadAs(body, V1ClusterRole.class);
                    rbacAuthorizationApi.createClusterRole(clusterRoleObj, false, "true", null);
                    break;
                case CLUSTER_RULE_BINDING:
                    V1ClusterRoleBinding clusterRoleBindingObj = Yaml.loadAs(body, V1ClusterRoleBinding.class);
                    rbacAuthorizationApi.createClusterRoleBinding(clusterRoleBindingObj, false, "true", null);
                    break;
                case HORIZONTAL_POD_AUTOSCALER:
                    V2beta2HorizontalPodAutoscaler hpaObj = Yaml.loadAs(body, V2beta2HorizontalPodAutoscaler.class);
                    autoscalingApi.createNamespacedHorizontalPodAutoscaler(hpaObj.getMetadata().getNamespace(), hpaObj, false, "true", null);
                    break;
            }
        } catch (ApiException e) {
            log.error("Create error for \r\n" + Yaml.dump(body), e);
            throw e;
        }
    }

    public boolean exist(String name, RES res) throws ApiException {
        return exist(name, "", res);
    }

    public boolean exist(String name, String namespace, RES res) throws ApiException {
        return read(name, namespace, res, String.class) != null;
    }

    public <T> List<T> list(RES res, Class<T> clazz) throws ApiException {
        return list("", "", res, clazz);
    }

    public <T> List<T> list(String labelSelector, String namespace, RES res, Class<T> clazz) throws ApiException {
        Object resource = null;
        switch (res) {
            case NAME_SPACE:
                resource = coreApi.listNamespace(true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case INGRESS:
                resource = extensionsApi.listNamespacedIngress(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case SERVICE:
                resource = coreApi.listNamespacedService(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case DEPLOYMENT:
                resource = extensionsApi.listNamespacedDeployment(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case REPLICA_SET:
                resource = extensionsApi.listNamespacedReplicaSet(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case POD:
                resource = coreApi.listNamespacedPod(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case SECRET:
                resource = coreApi.listNamespacedSecret(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case CONFIG_MAP:
                resource = coreApi.listNamespacedConfigMap(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case SERVICE_ACCOUNT:
                resource = coreApi.listNamespacedServiceAccount(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case DAEMON_SET:
                resource = extensionsApi.listNamespacedDaemonSet(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case ROLE:
                resource = rbacAuthorizationApi.listNamespacedRole(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case RULE_BINDING:
                resource = rbacAuthorizationApi.listNamespacedRoleBinding(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case CLUSTER_ROLE:
                resource = rbacAuthorizationApi.listClusterRole(true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case CLUSTER_RULE_BINDING:
                resource = rbacAuthorizationApi.listClusterRoleBinding(true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case HORIZONTAL_POD_AUTOSCALER:
                resource = autoscalingApi.listNamespacedHorizontalPodAutoscaler(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
        }
        if (clazz == String.class) {
            return (List<T>) ((List) resource).stream().map(item -> Yaml.dump(item)).collect(Collectors.toList());
        } else {
            return (List<T>) resource;
        }
    }

    public <T> T read(String name, RES res, Class<T> clazz) throws ApiException {
        return read(name, "", res, clazz);
    }

    public <T> T read(String name, String namespace, RES res, Class<T> clazz) throws ApiException {
        Object resource = null;
        try {
            switch (res) {
                case NAME_SPACE:
                    resource = coreApi.readNamespace(name, "true", false, false);
                    break;
                case INGRESS:
                    resource = extensionsApi.readNamespacedIngress(name, namespace, "true", false, false);
                    break;
                case SERVICE:
                    resource = coreApi.readNamespacedService(name, namespace, "true", false, false);
                    break;
                case DEPLOYMENT:
                    resource = extensionsApi.readNamespacedDeployment(name, namespace, "true", false, false);
                    break;
                case REPLICA_SET:
                    resource = extensionsApi.readNamespacedReplicaSet(name, namespace, "true", false, false);
                    break;
                case POD:
                    resource = coreApi.readNamespacedPod(name, namespace, "true", false, false);
                    break;
                case SECRET:
                    resource = coreApi.readNamespacedSecret(name, namespace, "true", false, false);
                    break;
                case CONFIG_MAP:
                    resource = coreApi.readNamespacedConfigMap(name, namespace, "true", false, false);
                    break;
                case SERVICE_ACCOUNT:
                    resource = coreApi.readNamespacedServiceAccount(name, namespace, "true", false, false);
                    break;
                case DAEMON_SET:
                    resource = extensionsApi.readNamespacedDaemonSet(name, namespace, "true", false, false);
                    break;
                case ROLE:
                    resource = rbacAuthorizationApi.readNamespacedRole(name, namespace, "true");
                    break;
                case RULE_BINDING:
                    resource = rbacAuthorizationApi.readNamespacedRoleBinding(name, namespace, "true");
                    break;
                case CLUSTER_ROLE:
                    resource = rbacAuthorizationApi.readClusterRole(name, "true");
                    break;
                case CLUSTER_RULE_BINDING:
                    resource = rbacAuthorizationApi.readClusterRoleBinding(name, "true");
                    break;
                case HORIZONTAL_POD_AUTOSCALER:
                    resource = autoscalingApi.readNamespacedHorizontalPodAutoscaler(name, namespace, "true", false, false);
                    break;
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

    public void replace(Object body) throws ApiException {
        replace(Yaml.dump(body));
    }

    public void replace(String body) throws ApiException {
        KubeBasicRes basicRes = YamlHelper.toObject(KubeBasicRes.class, body);
        try {
            switch (RES.parse(basicRes.getKind())) {
                case NAME_SPACE:
                    V1Namespace namespaceObj = Yaml.loadAs(body, V1Namespace.class);
                    coreApi.replaceNamespace(namespaceObj.getMetadata().getName(), namespaceObj, "true", null);
                    break;
                case INGRESS:
                    V1beta1Ingress ingressObj = Yaml.loadAs(body, V1beta1Ingress.class);
                    extensionsApi.replaceNamespacedIngress(ingressObj.getMetadata().getName(), ingressObj.getMetadata().getNamespace(), ingressObj, "true", null);
                    break;
                case SERVICE:
                    V1Service serviceObj = Yaml.loadAs(body, V1Service.class);
                    coreApi.replaceNamespacedService(serviceObj.getMetadata().getName(), serviceObj.getMetadata().getNamespace(), serviceObj, "true", null);
                    break;
                case DEPLOYMENT:
                    ExtensionsV1beta1Deployment deploymentObj = Yaml.loadAs(body, ExtensionsV1beta1Deployment.class);
                    extensionsApi.replaceNamespacedDeployment(deploymentObj.getMetadata().getName(), deploymentObj.getMetadata().getNamespace(), deploymentObj, "true", null);
                    break;
                case POD:
                    V1Pod podObj = Yaml.loadAs(body, V1Pod.class);
                    coreApi.replaceNamespacedPod(podObj.getMetadata().getName(), podObj.getMetadata().getNamespace(), podObj, "true", null);
                    break;
                case SECRET:
                    V1Secret secretObj = Yaml.loadAs(body, V1Secret.class);
                    coreApi.replaceNamespacedSecret(secretObj.getMetadata().getName(), secretObj.getMetadata().getNamespace(), secretObj, "true", null);
                    break;
                case CONFIG_MAP:
                    V1ConfigMap configMapObj = Yaml.loadAs(body, V1ConfigMap.class);
                    coreApi.replaceNamespacedConfigMap(configMapObj.getMetadata().getName(), configMapObj.getMetadata().getNamespace(), configMapObj, "true", null);
                    break;
                case SERVICE_ACCOUNT:
                    V1ServiceAccount serviceAccountObj = Yaml.loadAs(body, V1ServiceAccount.class);
                    coreApi.replaceNamespacedServiceAccount(serviceAccountObj.getMetadata().getName(), serviceAccountObj.getMetadata().getNamespace(), serviceAccountObj, "true", null);
                    break;
                case DAEMON_SET:
                    V1beta1DaemonSet daemonSetObj = Yaml.loadAs(body, V1beta1DaemonSet.class);
                    extensionsApi.replaceNamespacedDaemonSet(daemonSetObj.getMetadata().getName(), daemonSetObj.getMetadata().getNamespace(), daemonSetObj, "true", null);
                    break;
                case ROLE:
                    V1Role roleObj = Yaml.loadAs(body, V1Role.class);
                    rbacAuthorizationApi.replaceNamespacedRole(roleObj.getMetadata().getName(), roleObj.getMetadata().getNamespace(), roleObj, "true", null);
                    break;
                case RULE_BINDING:
                    V1RoleBinding roleBindingObj = Yaml.loadAs(body, V1RoleBinding.class);
                    rbacAuthorizationApi.replaceNamespacedRoleBinding(roleBindingObj.getMetadata().getName(), roleBindingObj.getMetadata().getNamespace(), roleBindingObj, "true", null);
                    break;
                case CLUSTER_ROLE:
                    V1ClusterRole clusterRoleObj = Yaml.loadAs(body, V1ClusterRole.class);
                    rbacAuthorizationApi.replaceClusterRole(clusterRoleObj.getMetadata().getName(), clusterRoleObj, "true", null);
                    break;
                case CLUSTER_RULE_BINDING:
                    V1ClusterRoleBinding clusterRoleBindingObj = Yaml.loadAs(body, V1ClusterRoleBinding.class);
                    rbacAuthorizationApi.replaceClusterRoleBinding(clusterRoleBindingObj.getMetadata().getName(), clusterRoleBindingObj, "true", null);
                    break;
                case HORIZONTAL_POD_AUTOSCALER:
                    V2beta2HorizontalPodAutoscaler hpaObj = Yaml.loadAs(body, V2beta2HorizontalPodAutoscaler.class);
                    autoscalingApi.replaceNamespacedHorizontalPodAutoscaler(hpaObj.getMetadata().getName(), hpaObj.getMetadata().getNamespace(), hpaObj, "true", null);
                    break;
            }
        } catch (ApiException e) {
            log.error("Replace error for \r\n" + Yaml.dump(body), e);
            throw e;
        }
    }

    public void patch(String name, List<String> patchers, RES res) throws ApiException {
        patch(name, patchers, "", res);
    }

    /**
     * @param name
     * @param patchers
     * @param namespace
     * @param res
     * @throws ApiException
     * @link http://jsonpatch.com/
     */
    public void patch(String name, List<String> patchers, String namespace, RES res) throws ApiException {
        List<JsonObject> jsonPatchers = patchers.stream()
                .map(patcher -> (new Gson()).fromJson(patcher, JsonElement.class).getAsJsonObject())
                .collect(Collectors.toList());
        try {
            switch (res) {
                case NAME_SPACE:
                    coreApi.patchNamespace(name, jsonPatchers, "true", null);
                    break;
                case INGRESS:
                    extensionsApi.patchNamespacedIngress(name, namespace, jsonPatchers, "true", null);
                    break;
                case SERVICE:
                    coreApi.patchNamespacedService(name, namespace, jsonPatchers, "true", null);
                    break;
                case DEPLOYMENT:
                    extensionsApi.patchNamespacedDeployment(name, namespace, jsonPatchers, "true", null);
                    break;
                case POD:
                    coreApi.patchNamespacedPod(name, namespace, jsonPatchers, "true", null);
                    break;
                case SECRET:
                    coreApi.patchNamespacedSecret(name, namespace, jsonPatchers, "true", null);
                    break;
                case CONFIG_MAP:
                    coreApi.patchNamespacedConfigMap(name, namespace, jsonPatchers, "true", null);
                    break;
                case SERVICE_ACCOUNT:
                    coreApi.patchNamespacedServiceAccount(name, namespace, jsonPatchers, "true", null);
                    break;
                case DAEMON_SET:
                    extensionsApi.patchNamespacedDaemonSet(name, namespace, jsonPatchers, "true", null);
                    break;
                case ROLE:
                    rbacAuthorizationApi.patchNamespacedRole(name, namespace, jsonPatchers, "true", null);
                    break;
                case RULE_BINDING:
                    rbacAuthorizationApi.patchNamespacedRoleBinding(name, namespace, jsonPatchers, "true", null);
                    break;
                case CLUSTER_ROLE:
                    rbacAuthorizationApi.patchClusterRole(name, jsonPatchers, "true", null);
                    break;
                case CLUSTER_RULE_BINDING:
                    rbacAuthorizationApi.patchClusterRoleBinding(name, jsonPatchers, "true", null);
                    break;
                case HORIZONTAL_POD_AUTOSCALER:
                    autoscalingApi.patchNamespacedHorizontalPodAutoscaler(name, namespace, jsonPatchers, "true", null);
                    break;
            }
        } catch (ApiException e) {
            log.error("Patch error for \r\n" + $.json.toJsonString(patchers), e);
            throw e;
        }
    }

    public void apply(Object body) throws ApiException {
        apply(Yaml.dump(body));
    }

    public void apply(String body) throws ApiException {
        KubeBasicRes basicRes = YamlHelper.toObject(KubeBasicRes.class, body);
        if (exist(basicRes.getMetadata().getName(), basicRes.getMetadata().getNamespace(), RES.parse(basicRes.getKind()))) {
            replace(body);
        } else {
            create(body);
        }
    }

    public List<String> log(String name, String namespace) throws ApiException, IOException {
        return log(name, null, namespace, 0);
    }

    public List<String> log(String name, String namespace, int tailLines) throws ApiException, IOException {
        return log(name, null, namespace, tailLines);
    }

    public List<String> log(String name, String container, String namespace) throws ApiException, IOException {
        return log(name, container, namespace, 0);
    }

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

    public Closeable log(String name, String container, String namespace,
                         Consumer<String> tailFollowFun) throws ApiException {
        return log(name, container, namespace, tailFollowFun, 0);
    }

    public Closeable log(String name, String container, String namespace,
                         Consumer<String> tailFollowFun, int tailLines) throws ApiException {
        if (container == null) {
            container = read(name, namespace, RES.POD, V1Pod.class).getSpec().getContainers().get(0).getName();
        }
        String finalContainer = container;
        try {
            AtomicBoolean closed = new AtomicBoolean(false);
            InputStream is = podLogs.streamNamespacedPodLog(namespace, name, finalContainer, null, tailLines == 0 ? null : tailLines, false);
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            EXECUTOR_SERVICE.execute(() -> {
                try {
                    String msg;
                    while ((msg = r.readLine()) != null) {
                        tailFollowFun.accept(msg);
                    }
                } catch (IOException e) {
                    if (closed.get()) {
                        // 正常关闭
                    } else {
                        log.error("Output log error", e);
                    }
                } finally {
                    try {
                        is.close();
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
        } catch (ApiException | IOException e) {
            log.error("Output log error", e);
        }
        return () -> {
        };
    }

    public void delete(String name, RES res) throws ApiException {
        delete(name, "", res);
    }

    public void delete(String name, String namespace, RES res) throws ApiException {
        if (!exist(name, namespace, res)) {
            return;
        }
        V1DeleteOptions deleteOptions = new V1DeleteOptions();
        try {
            switch (res) {
                case NAME_SPACE:
                    coreApi.deleteNamespace(name, deleteOptions, null, null, null, null, null);
                    break;
                case INGRESS:
                    extensionsApi.deleteNamespacedIngress(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case SERVICE:
                    coreApi.deleteNamespacedService(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case DEPLOYMENT:
                    extensionsApi.deleteNamespacedDeployment(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case REPLICA_SET:
                    extensionsApi.deleteNamespacedReplicaSet(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case POD:
                    coreApi.deleteNamespacedPod(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case SECRET:
                    coreApi.deleteNamespacedSecret(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case CONFIG_MAP:
                    coreApi.deleteNamespacedConfigMap(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case SERVICE_ACCOUNT:
                    coreApi.deleteNamespacedServiceAccount(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case DAEMON_SET:
                    extensionsApi.deleteNamespacedDaemonSet(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case ROLE:
                    rbacAuthorizationApi.deleteNamespacedRole(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case RULE_BINDING:
                    rbacAuthorizationApi.deleteNamespacedRoleBinding(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case CLUSTER_ROLE:
                    rbacAuthorizationApi.deleteClusterRole(name, deleteOptions, "true", null, null, null, null);
                    break;
                case CLUSTER_RULE_BINDING:
                    rbacAuthorizationApi.deleteClusterRoleBinding(name, deleteOptions, "true", null, null, null, null);
                    break;
                case HORIZONTAL_POD_AUTOSCALER:
                    autoscalingApi.deleteNamespacedHorizontalPodAutoscaler(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
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

    public static enum RES {
        NAME_SPACE("Namespace"),
        INGRESS("Ingress"),
        SERVICE("Service"),
        SERVICE_ACCOUNT("ServiceAccount"),
        DEPLOYMENT("Deployment"),
        DAEMON_SET("DaemonSet"),
        REPLICA_SET("ReplicaSet"),
        CONFIG_MAP("ConfigMap"),
        SECRET("Secret"),
        POD("Pod"),
        ROLE("Role"),
        RULE_BINDING("RoleBinding"),
        CLUSTER_ROLE("ClusterRole"),
        CLUSTER_RULE_BINDING("ClusterRoleBinding"),
        HORIZONTAL_POD_AUTOSCALER("HorizontalPodAutoscaler");

        String val;

        RES(String val) {
            this.val = val;
        }

        public static RES parse(String val) {
            for (RES res : RES.values()) {
                if (res.val.equalsIgnoreCase(val)) {
                    return res;
                }
            }
            return null;
        }

        public String getVal() {
            return val;
        }
    }

    @FunctionalInterface
    public interface WatchCall {

        Call call(CoreV1Api coreApi, ExtensionsV1beta1Api extensionsApi,
                  RbacAuthorizationV1Api rbacAuthorizationApi, AutoscalingV2beta2Api autoscalingApi) throws ApiException;

    }

}

