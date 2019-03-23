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

package com.tairanchina.csp.dew.helper;

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
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Kubernetes操作函数类
 *
 * @link https://github.com/kubernetes-client/java
 */
public class KubeHelper {

    private static final ConcurrentHashMap<String, String> EXISTS = new ConcurrentHashMap<>();
    private static final Map<String, KubeHelper.Instance> INSTANCES = new HashMap<>();

    private static class Instance {

        public Instance(Log log, ApiClient client, CoreV1Api coreApi, ExtensionsV1beta1Api extensionsApi, RbacAuthorizationV1Api rbacAuthorizationApi, PodLogs podLogs) {
            this.log = log;
            this.client = client;
            this.coreApi = coreApi;
            this.extensionsApi = extensionsApi;
            this.rbacAuthorizationApi = rbacAuthorizationApi;
            this.podLogs = podLogs;
        }

        private Log log;
        private ApiClient client;
        private CoreV1Api coreApi;
        private ExtensionsV1beta1Api extensionsApi;
        private RbacAuthorizationV1Api rbacAuthorizationApi;
        private PodLogs podLogs;

    }

    private static final Map<String, Watch> WATCH_LIST = new ConcurrentHashMap<>();
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    public static void init(String instanceId, Log log, String base64KubeConfig) {
        try {
            String hash = $.security.digest.digest(base64KubeConfig, "MD5");
            if (EXISTS.containsKey(hash)) {
                INSTANCES.put(instanceId, INSTANCES.get(EXISTS.get(hash)));
                return;
            }
            EXISTS.put(hash, instanceId);
        } catch (NoSuchAlgorithmException ignore) {
        }
        YamlHelper.init(log);
        ApiClient client = null;
        CoreV1Api coreApi;
        ExtensionsV1beta1Api extensionsApi;
        RbacAuthorizationV1Api rbacAuthorizationApi;
        PodLogs podLogs;
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
        podLogs = new PodLogs();
        INSTANCES.put(instanceId, new Instance(log, client, coreApi, extensionsApi, rbacAuthorizationApi, podLogs));
    }

    public static <T> String watch(WatchCall call, Consumer<Watch.Response<T>> callback, Class<T> clazz, String instanceId) throws ApiException {
        KubeHelper.Instance instance = INSTANCES.get(instanceId);
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
        Watch<T> watch = Watch.createWatch(instance.client, call.call(instance.coreApi, instance.extensionsApi, instance.rbacAuthorizationApi), typeToken.getType());
        WATCH_LIST.put(watchId, watch);
        EXECUTOR_SERVICE.execute(() -> {
            try {
                watch.forEach(callback);
            } catch (RuntimeException e) {
                if (!WATCH_LIST.containsKey(watchId) && e.getMessage().equals("IO Exception during hasNext method.")) {
                    // https://github.com/kubernetes-client/java/issues/259
                } else {
                    throw e;
                }
            }
        });
        return watchId;
    }

    public static void stopWatch(String watchId, String instanceId) throws IOException {
        Watch watch = WATCH_LIST.get(watchId);
        WATCH_LIST.remove(watchId);
        watch.close();
    }


    public static void create(Object body, String instanceId) throws ApiException {
        create(Yaml.dump(body), instanceId);
    }

    public static void create(String body, String instanceId) throws ApiException {
        KubeHelper.Instance instance = INSTANCES.get(instanceId);
        KubeBasicRes basicRes = YamlHelper.toObject(KubeBasicRes.class, body);
        try {
            switch (RES.parse(basicRes.getKind())) {
                case NAME_SPACE:
                    V1Namespace namespaceObj = Yaml.loadAs(body, V1Namespace.class);
                    instance.coreApi.createNamespace(namespaceObj, false, "true", null);
                    break;
                case INGRESS:
                    V1beta1Ingress ingressObj = Yaml.loadAs(body, V1beta1Ingress.class);
                    instance.extensionsApi.createNamespacedIngress(ingressObj.getMetadata().getNamespace(), ingressObj, false, "true", null);
                    break;
                case SERVICE:
                    V1Service serviceObj = Yaml.loadAs(body, V1Service.class);
                    instance.coreApi.createNamespacedService(serviceObj.getMetadata().getNamespace(), serviceObj, false, "true", null);
                    break;
                case DEPLOYMENT:
                    ExtensionsV1beta1Deployment deploymentObj = Yaml.loadAs(body, ExtensionsV1beta1Deployment.class);
                    instance.extensionsApi.createNamespacedDeployment(deploymentObj.getMetadata().getNamespace(), deploymentObj, false, "true", null);
                    break;
                case POD:
                    V1Pod podObj = Yaml.loadAs(body, V1Pod.class);
                    instance.coreApi.createNamespacedPod(podObj.getMetadata().getNamespace(), podObj, false, "true", null);
                    break;
                case SECRET:
                    V1Secret secretObj = Yaml.loadAs(body, V1Secret.class);
                    instance.coreApi.createNamespacedSecret(secretObj.getMetadata().getNamespace(), secretObj, false, "true", null);
                    break;
                case CONFIG_MAP:
                    V1ConfigMap configMapObj = Yaml.loadAs(body, V1ConfigMap.class);
                    instance.coreApi.createNamespacedConfigMap(configMapObj.getMetadata().getNamespace(), configMapObj, false, "true", null);
                    break;
                case SERVICE_ACCOUNT:
                    V1ServiceAccount serviceAccountObj = Yaml.loadAs(body, V1ServiceAccount.class);
                    instance.coreApi.createNamespacedServiceAccount(serviceAccountObj.getMetadata().getNamespace(), serviceAccountObj, false, "true", null);
                    break;
                case DAEMON_SET:
                    V1beta1DaemonSet daemonSetObj = Yaml.loadAs(body, V1beta1DaemonSet.class);
                    instance.extensionsApi.createNamespacedDaemonSet(daemonSetObj.getMetadata().getNamespace(), daemonSetObj, false, "true", null);
                    break;
                case ROLE:
                    V1Role roleObj = Yaml.loadAs(body, V1Role.class);
                    instance.rbacAuthorizationApi.createNamespacedRole(roleObj.getMetadata().getNamespace(), roleObj, false, "true", null);
                    break;
                case RULE_BINDING:
                    V1RoleBinding roleBindingObj = Yaml.loadAs(body, V1RoleBinding.class);
                    instance.rbacAuthorizationApi.createNamespacedRoleBinding(roleBindingObj.getMetadata().getNamespace(), roleBindingObj, false, "true", null);
                    break;
                case CLUSTER_ROLE:
                    V1ClusterRole clusterRoleObj = Yaml.loadAs(body, V1ClusterRole.class);
                    instance.rbacAuthorizationApi.createClusterRole(clusterRoleObj, false, "true", null);
                    break;
                case CLUSTER_RULE_BINDING:
                    V1ClusterRoleBinding clusterRoleBindingObj = Yaml.loadAs(body, V1ClusterRoleBinding.class);
                    instance.rbacAuthorizationApi.createClusterRoleBinding(clusterRoleBindingObj, false, "true", null);
                    break;
            }
        } catch (ApiException e) {
            instance.log.error("Create error for \r\n" + Yaml.dump(body), e);
            throw e;
        }
    }

    public static boolean exist(String name, RES res, String instanceId) throws ApiException {
        return exist(name, "", res, instanceId);
    }

    public static boolean exist(String name, String namespace, RES res, String instanceId) throws ApiException {
        return read(name, namespace, res, String.class, instanceId) != null;
    }

    public static <T> List<T> list(RES res, Class<T> clazz, String instanceId) throws ApiException {
        return list("", "", res, clazz, instanceId);
    }

    public static <T> List<T> list(String labelSelector, String namespace, RES res, Class<T> clazz, String instanceId) throws ApiException {
        KubeHelper.Instance instance = INSTANCES.get(instanceId);
        Object resource = null;
        switch (res) {
            case NAME_SPACE:
                resource = instance.coreApi.listNamespace(true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case INGRESS:
                resource = instance.extensionsApi.listNamespacedIngress(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case SERVICE:
                resource = instance.coreApi.listNamespacedService(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case DEPLOYMENT:
                resource = instance.extensionsApi.listNamespacedDeployment(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case POD:
                resource = instance.coreApi.listNamespacedPod(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case SECRET:
                resource = instance.coreApi.listNamespacedSecret(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case CONFIG_MAP:
                resource = instance.coreApi.listNamespacedConfigMap(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case SERVICE_ACCOUNT:
                resource = instance.coreApi.listNamespacedServiceAccount(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case DAEMON_SET:
                resource = instance.extensionsApi.listNamespacedDaemonSet(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case ROLE:
                resource = instance.rbacAuthorizationApi.listNamespacedRole(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case RULE_BINDING:
                resource = instance.rbacAuthorizationApi.listNamespacedRoleBinding(namespace, true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case CLUSTER_ROLE:
                resource = instance.rbacAuthorizationApi.listClusterRole(true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
            case CLUSTER_RULE_BINDING:
                resource = instance.rbacAuthorizationApi.listClusterRoleBinding(true, "true", null, null, labelSelector, Integer.MAX_VALUE, null, null, null).getItems();
                break;
        }
        if (clazz == String.class) {
            return (List<T>) ((List) resource).stream().map(item -> Yaml.dump(item)).collect(Collectors.toList());
        } else {
            return (List<T>) resource;
        }
    }

    public static <T> T read(String name, RES res, Class<T> clazz, String instanceId) throws ApiException {
        return read(name, "", res, clazz, instanceId);
    }

    public static <T> T read(String name, String namespace, RES res, Class<T> clazz, String instanceId) throws ApiException {
        KubeHelper.Instance instance = INSTANCES.get(instanceId);
        Object resource = null;
        try {
            switch (res) {
                case NAME_SPACE:
                    resource = instance.coreApi.readNamespace(name, "true", false, false);
                    break;
                case INGRESS:
                    resource = instance.extensionsApi.readNamespacedIngress(name, namespace, "true", false, false);
                    break;
                case SERVICE:
                    resource = instance.coreApi.readNamespacedService(name, namespace, "true", false, false);
                    break;
                case DEPLOYMENT:
                    resource = instance.extensionsApi.readNamespacedDeployment(name, namespace, "true", false, false);
                    break;
                case POD:
                    resource = instance.coreApi.readNamespacedPod(name, namespace, "true", false, false);
                    break;
                case SECRET:
                    resource = instance.coreApi.readNamespacedSecret(name, namespace, "true", false, false);
                    break;
                case CONFIG_MAP:
                    resource = instance.coreApi.readNamespacedConfigMap(name, namespace, "true", false, false);
                    break;
                case SERVICE_ACCOUNT:
                    resource = instance.coreApi.readNamespacedServiceAccount(name, namespace, "true", false, false);
                    break;
                case DAEMON_SET:
                    resource = instance.extensionsApi.readNamespacedDaemonSet(name, namespace, "true", false, false);
                    break;
                case ROLE:
                    resource = instance.rbacAuthorizationApi.readNamespacedRole(name, namespace, "true");
                    break;
                case RULE_BINDING:
                    resource = instance.rbacAuthorizationApi.readNamespacedRoleBinding(name, namespace, "true");
                    break;
                case CLUSTER_ROLE:
                    resource = instance.rbacAuthorizationApi.readClusterRole(name, "true");
                    break;
                case CLUSTER_RULE_BINDING:
                    resource = instance.rbacAuthorizationApi.readClusterRoleBinding(name, "true");
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

    public static void replace(Object body, String instanceId) throws ApiException {
        replace(Yaml.dump(body), instanceId);
    }

    public static void replace(String body, String instanceId) throws ApiException {
        KubeHelper.Instance instance = INSTANCES.get(instanceId);
        KubeBasicRes basicRes = YamlHelper.toObject(KubeBasicRes.class, body);
        try {
            switch (RES.parse(basicRes.getKind())) {
                case NAME_SPACE:
                    V1Namespace namespaceObj = Yaml.loadAs(body, V1Namespace.class);
                    instance.coreApi.replaceNamespace(namespaceObj.getMetadata().getName(), namespaceObj, "true", null);
                    break;
                case INGRESS:
                    V1beta1Ingress ingressObj = Yaml.loadAs(body, V1beta1Ingress.class);
                    instance.extensionsApi.replaceNamespacedIngress(ingressObj.getMetadata().getName(), ingressObj.getMetadata().getNamespace(), ingressObj, "true", null);
                    break;
                case SERVICE:
                    V1Service serviceObj = Yaml.loadAs(body, V1Service.class);
                    instance.coreApi.replaceNamespacedService(serviceObj.getMetadata().getName(), serviceObj.getMetadata().getNamespace(), serviceObj, "true", null);
                    break;
                case DEPLOYMENT:
                    ExtensionsV1beta1Deployment deploymentObj = Yaml.loadAs(body, ExtensionsV1beta1Deployment.class);
                    instance.extensionsApi.replaceNamespacedDeployment(deploymentObj.getMetadata().getName(), deploymentObj.getMetadata().getNamespace(), deploymentObj, "true", null);
                    break;
                case POD:
                    V1Pod podObj = Yaml.loadAs(body, V1Pod.class);
                    instance.coreApi.replaceNamespacedPod(podObj.getMetadata().getName(), podObj.getMetadata().getNamespace(), podObj, "true", null);
                    break;
                case SECRET:
                    V1Secret secretObj = Yaml.loadAs(body, V1Secret.class);
                    instance.coreApi.replaceNamespacedSecret(secretObj.getMetadata().getName(), secretObj.getMetadata().getNamespace(), secretObj, "true", null);
                    break;
                case CONFIG_MAP:
                    V1ConfigMap configMapObj = Yaml.loadAs(body, V1ConfigMap.class);
                    instance.coreApi.replaceNamespacedConfigMap(configMapObj.getMetadata().getName(), configMapObj.getMetadata().getNamespace(), configMapObj, "true", null);
                    break;
                case SERVICE_ACCOUNT:
                    V1ServiceAccount serviceAccountObj = Yaml.loadAs(body, V1ServiceAccount.class);
                    instance.coreApi.replaceNamespacedServiceAccount(serviceAccountObj.getMetadata().getName(), serviceAccountObj.getMetadata().getNamespace(), serviceAccountObj, "true", null);
                    break;
                case DAEMON_SET:
                    V1beta1DaemonSet daemonSetObj = Yaml.loadAs(body, V1beta1DaemonSet.class);
                    instance.extensionsApi.replaceNamespacedDaemonSet(daemonSetObj.getMetadata().getName(), daemonSetObj.getMetadata().getNamespace(), daemonSetObj, "true", null);
                    break;
                case ROLE:
                    V1Role roleObj = Yaml.loadAs(body, V1Role.class);
                    instance.rbacAuthorizationApi.replaceNamespacedRole(roleObj.getMetadata().getName(), roleObj.getMetadata().getNamespace(), roleObj, "true", null);
                    break;
                case RULE_BINDING:
                    V1RoleBinding roleBindingObj = Yaml.loadAs(body, V1RoleBinding.class);
                    instance.rbacAuthorizationApi.replaceNamespacedRoleBinding(roleBindingObj.getMetadata().getName(), roleBindingObj.getMetadata().getNamespace(), roleBindingObj, "true", null);
                    break;
                case CLUSTER_ROLE:
                    V1ClusterRole clusterRoleObj = Yaml.loadAs(body, V1ClusterRole.class);
                    instance.rbacAuthorizationApi.replaceClusterRole(clusterRoleObj.getMetadata().getName(), clusterRoleObj, "true", null);
                    break;
                case CLUSTER_RULE_BINDING:
                    V1ClusterRoleBinding clusterRoleBindingObj = Yaml.loadAs(body, V1ClusterRoleBinding.class);
                    instance.rbacAuthorizationApi.replaceClusterRoleBinding(clusterRoleBindingObj.getMetadata().getName(), clusterRoleBindingObj, "true", null);
                    break;
            }
        } catch (ApiException e) {
            instance.log.error("Replace error for \r\n" + Yaml.dump(body), e);
            throw e;
        }
    }

    public static void patch(String name, List<String> patchers, RES res, String instanceId) throws ApiException {
        patch(name, patchers, "", res, instanceId);
    }

    /**
     * @param name
     * @param patchers
     * @param namespace
     * @param res
     * @throws ApiException
     * @link http://jsonpatch.com/
     */
    public static void patch(String name, List<String> patchers, String namespace, RES res, String instanceId) throws ApiException {
        KubeHelper.Instance instance = INSTANCES.get(instanceId);
        List<JsonObject> jsonPatchers = patchers.stream()
                .map(patcher -> (new Gson()).fromJson(patcher, JsonElement.class).getAsJsonObject())
                .collect(Collectors.toList());
        try {
            switch (res) {
                case NAME_SPACE:
                    instance.coreApi.patchNamespace(name, jsonPatchers, "true", null);
                    break;
                case INGRESS:
                    instance.extensionsApi.patchNamespacedIngress(name, namespace, jsonPatchers, "true", null);
                    break;
                case SERVICE:
                    instance.coreApi.patchNamespacedService(name, namespace, jsonPatchers, "true", null);
                    break;
                case DEPLOYMENT:
                    instance.extensionsApi.patchNamespacedDeployment(name, namespace, jsonPatchers, "true", null);
                    break;
                case POD:
                    instance.coreApi.patchNamespacedPod(name, namespace, jsonPatchers, "true", null);
                    break;
                case SECRET:
                    instance.coreApi.patchNamespacedSecret(name, namespace, jsonPatchers, "true", null);
                    break;
                case CONFIG_MAP:
                    instance.coreApi.patchNamespacedConfigMap(name, namespace, jsonPatchers, "true", null);
                    break;
                case SERVICE_ACCOUNT:
                    instance.coreApi.patchNamespacedServiceAccount(name, namespace, jsonPatchers, "true", null);
                    break;
                case DAEMON_SET:
                    instance.extensionsApi.patchNamespacedDaemonSet(name, namespace, jsonPatchers, "true", null);
                    break;
                case ROLE:
                    instance.rbacAuthorizationApi.patchNamespacedRole(name, namespace, jsonPatchers, "true", null);
                    break;
                case RULE_BINDING:
                    instance.rbacAuthorizationApi.patchNamespacedRoleBinding(name, namespace, jsonPatchers, "true", null);
                    break;
                case CLUSTER_ROLE:
                    instance.rbacAuthorizationApi.patchClusterRole(name, jsonPatchers, "true", null);
                    break;
                case CLUSTER_RULE_BINDING:
                    instance.rbacAuthorizationApi.patchClusterRoleBinding(name, jsonPatchers, "true", null);
                    break;
            }
        } catch (ApiException e) {
            instance.log.error("Patch error for \r\n" + $.json.toJsonString(patchers), e);
            throw e;
        }
    }

    public static void apply(Object body, String instanceId) throws ApiException {
        apply(Yaml.dump(body), instanceId);
    }

    public static void apply(String body, String instanceId) throws ApiException {
        KubeBasicRes basicRes = YamlHelper.toObject(KubeBasicRes.class, body);
        if (exist(basicRes.getMetadata().getName(), basicRes.getMetadata().getNamespace(), RES.parse(basicRes.getKind()), instanceId)) {
            replace(body, instanceId);
        } else {
            create(body, instanceId);
        }
    }

    public static List<String> log(String name, String namespace, int waitSec, String instanceId) throws ApiException, IOException {
        return log(name, null, namespace, waitSec, instanceId);
    }

    public static List<String> log(String name, String container, String namespace, int waitSec, String instanceId) throws ApiException, IOException {
        KubeHelper.Instance instance = INSTANCES.get(instanceId);
        List<String> logResult = new ArrayList<>();
        Closeable closeable = log(name, container, namespace, logResult::add, instanceId);
        try {
            Thread.sleep(waitSec * 1000);
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
        closeable.close();
        return logResult;
    }

    public static Closeable log(String name, String container, String namespace, Consumer<String> tailFollowFun, String instanceId) throws ApiException {
        KubeHelper.Instance instance = INSTANCES.get(instanceId);
        if (container == null) {
            container = read(name, namespace, RES.POD, V1Pod.class, instanceId).getSpec().getContainers().get(0).getName();
        }
        String finalContainer = container;
        try {
            InputStream is = instance.podLogs.streamNamespacedPodLog(namespace, name, finalContainer);
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            EXECUTOR_SERVICE.execute(() -> {
                try {
                    while (r.readLine() != null) {
                        tailFollowFun.accept(r.readLine());
                    }
                } catch (IOException e) {
                    instance.log.error("Output log error.", e);
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        instance.log.error("Close log stream error.", e);
                    }
                }
            });
            return () -> {
                r.close();
                is.close();
            };
        } catch (ApiException | IOException e) {
            instance.log.error("Output log error.", e);
        }
        return () -> {
        };
    }

    public static void delete(String name, RES res, String instanceId) throws ApiException {
        delete(name, "", res, instanceId);
    }

    public static void delete(String name, String namespace, RES res, String instanceId) throws ApiException {
        KubeHelper.Instance instance = INSTANCES.get(instanceId);
        V1DeleteOptions deleteOptions = new V1DeleteOptions();
        try {
            switch (res) {
                case NAME_SPACE:
                    instance.coreApi.deleteNamespace(name, deleteOptions, null, null, null, null, null);
                    break;
                case INGRESS:
                    instance.extensionsApi.deleteNamespacedIngress(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case SERVICE:
                    instance.coreApi.deleteNamespacedService(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case DEPLOYMENT:
                    instance.extensionsApi.deleteNamespacedDeployment(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case POD:
                    instance.coreApi.deleteNamespacedPod(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case SECRET:
                    instance.coreApi.deleteNamespacedSecret(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case CONFIG_MAP:
                    instance.coreApi.deleteNamespacedConfigMap(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case SERVICE_ACCOUNT:
                    instance.coreApi.deleteNamespacedServiceAccount(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case DAEMON_SET:
                    instance.extensionsApi.deleteNamespacedDaemonSet(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case ROLE:
                    instance.rbacAuthorizationApi.deleteNamespacedRole(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case RULE_BINDING:
                    instance.rbacAuthorizationApi.deleteNamespacedRoleBinding(name, namespace, deleteOptions, "true", null, null, null, null);
                    break;
                case CLUSTER_ROLE:
                    instance.rbacAuthorizationApi.deleteClusterRole(name, deleteOptions, "true", null, null, null, null);
                    break;
                case CLUSTER_RULE_BINDING:
                    instance.rbacAuthorizationApi.deleteClusterRoleBinding(name, deleteOptions, "true", null, null, null, null);
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
        boolean exist = exist(name, namespace, res, instanceId);
        while (exist) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
            exist = exist(name, namespace, res, instanceId);
        }
    }

    public enum RES {
        NAME_SPACE("Namespace"),
        INGRESS("Ingress"),
        SERVICE("Service"),
        SERVICE_ACCOUNT("ServiceAccount"),
        DEPLOYMENT("Deployment"),
        DAEMON_SET("DaemonSet"),
        CONFIG_MAP("ConfigMap"),
        SECRET("Secret"),
        POD("Pod"),
        ROLE("Role"),
        RULE_BINDING("RoleBinding"),
        CLUSTER_ROLE("ClusterRole"),
        CLUSTER_RULE_BINDING("ClusterRoleBinding");

        String val;

        RES(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }

        public static RES parse(String val) {
            for (RES res : RES.values()) {
                if (res.val.equalsIgnoreCase(val)) {
                    return res;
                }
            }
            return null;
        }
    }

    @FunctionalInterface
    public interface WatchCall {

        Call call(CoreV1Api coreApi, ExtensionsV1beta1Api extensionsApi, RbacAuthorizationV1Api rbacAuthorizationApi) throws ApiException;

    }

}

