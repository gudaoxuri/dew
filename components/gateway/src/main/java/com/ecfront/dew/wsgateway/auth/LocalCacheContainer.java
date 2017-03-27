package com.ecfront.dew.wsgateway.auth;

import com.ecfront.dew.common.Resp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LocalCacheContainer {

    private static final Logger logger = LoggerFactory.getLogger(LocalCacheContainer.class);

    private static final Set<String> organizations = new HashSet<String>() {{
        add("");
    }};

    private static final Map<String, Map<String, Set<String>>> resources = new HashMap<String, Map<String, Set<String>>>() {{
        put("GET", new HashMap<>());
        put("POST", new HashMap<>());
        put("PUT", new HashMap<>());
        put("DELETE", new HashMap<>());
        put("WS", new HashMap<>());
    }};

    private static final Map<String, List<RouterRContent>> resourcesR = new HashMap<String, List<RouterRContent>>() {{
        put("GET", new ArrayList<>());
        put("POST", new ArrayList<>());
        put("PUT", new ArrayList<>());
        put("DELETE", new ArrayList<>());
        put("WS", new ArrayList<>());
    }};

    private static final Pattern matchRegex = Pattern.compile(":\\w+");

    static void addOrganization(String code) {
        organizations.add(code);
    }

    static void removeOrganization(String code) {
        organizations.remove(code);
    }

    static boolean existOrganization(String code) {
        return organizations.contains(code);
    }

    private static String formatResourcePath(String path) {
        if (!path.endsWith("*") && !path.endsWith("/")) {
            return path + "/";
        } else {
            return path;
        }
    }

    private static void doAddResourceR(String method, String path) {
        String formatPath;
        if(path.endsWith("*")){
            formatPath=path.substring(0,path.length()-1);
        }else{
            formatPath=path;
        }
        if (resourcesR.get(method).stream().noneMatch(i -> Objects.equals(i.getOriginalPath(),formatPath))) {
            // regular
            Object[] r = getRegex(path);
            resourcesR.get(method).add(new RouterRContent(formatPath, (Pattern) r[0], (List<String>) r[1]));
            resourcesR.get(method).sort(Comparator.comparingInt(o -> (o.getOriginalPath()).split("/").length * -1));
        }
    }

    private static void doAddResource(String method, String path) {
        if (!resources.get(method).containsKey(path)) {
            resources.get(method).put(path, new HashSet<>());
        }
    }

    static void addResource(String method, String _path) {
        String path = formatResourcePath(_path);
        if (path.endsWith("*") || path.contains(":")) {
            if (Objects.equals(method, "*")) {
                doAddResourceR("GET", path);
                doAddResourceR("POST", path);
                doAddResourceR("PUT", path);
                doAddResourceR("DELETE", path);
                doAddResourceR("WS", path);
            } else {
                doAddResourceR(method, path);
            }
        } else {
            if (Objects.equals(method, "*")) {
                doAddResource("GET", path);
                doAddResource("POST", path);
                doAddResource("PUT", path);
                doAddResource("DELETE", path);
                doAddResource("WS", path);
            } else {
                doAddResource(method, path);
            }
        }
    }

    private static void doRemoveResourceR(String method, String path) {
        String formatPath;
        if(path.endsWith("*")){
            formatPath=path.substring(0,path.length()-1);
        }else{
            formatPath=path;
        }
        Optional<RouterRContent> resR = resourcesR.get(method).stream().filter(i -> Objects.equals(i.getOriginalPath(), formatPath)).findFirst();
        resR.ifPresent(objects -> resourcesR.get(method).remove(objects));
    }

    static void removeResource(String method, String _path) {
        String path = formatResourcePath(_path);
        if (path.endsWith("*") || path.contains(":")) {
            if (Objects.equals(method, "*")) {
                doRemoveResourceR("GET", path);
                doRemoveResourceR("POST", path);
                doRemoveResourceR("PUT", path);
                doRemoveResourceR("DELETE", path);
                doRemoveResourceR("WS", path);
            } else {
                doRemoveResourceR(method, path);
            }
        } else {
            if (resources.get(method).containsKey(path)) {
                resources.get(method).remove(path);
            }
        }
    }

    private static void doAddRoleToResourceR(String code, String method, String path) {
        String formatPath;
        if(path.endsWith("*")){
            formatPath=path.substring(0,path.length()-1);
        }else{
            formatPath=path;
        }
        Optional<RouterRContent> resR = resourcesR.get(method).stream().filter(i -> Objects.equals(i.getOriginalPath(), formatPath)).findFirst();
        resR.ifPresent(routerRContent -> routerRContent.getRoleCode().add(code));
    }

    private static void doAddRoleToResource(String code, String method, String path) {
        if (resources.get(method).containsKey(path)) {
            resources.get(method).get(path).add(code);
        }
    }

    static void addRole(String code, Set<String> resourceCodes) {
        resourceCodes.forEach(
                _resCode ->
                {
                    String resCode = formatResourcePath(_resCode);
                    String[] item = resCode.split("@");
                    String method = item[0];
                    String path = item[1];
                    if (path.endsWith("*") || path.contains(":")) {
                        if (Objects.equals(method, "*")) {
                            doAddRoleToResourceR(code, "GET", path);
                            doAddRoleToResourceR(code, "POST", path);
                            doAddRoleToResourceR(code, "PUT", path);
                            doAddRoleToResourceR(code, "DELETE", path);
                            doAddRoleToResourceR(code, "WS", path);
                        } else {
                            doAddRoleToResourceR(code, method, path);
                        }
                    } else {
                        if (Objects.equals(method, "*")) {
                            doAddRoleToResource(code, "GET", path);
                            doAddRoleToResource(code, "POST", path);
                            doAddRoleToResource(code, "PUT", path);
                            doAddRoleToResource(code, "DELETE", path);
                            doAddRoleToResource(code, "WS", path);
                        } else {
                            doAddRoleToResource(code, method, path);
                        }
                    }
                }
        );
    }

    static void removeRole(String code) {
        resourcesR.values().forEach(
                i -> i.forEach(ii -> ii.getRoleCode().remove(code))
        );
        resources.values().forEach(i -> i.remove(code));
    }

    static void flushAuth() {
        resourcesR.values().forEach(List::clear);
        resources.values().forEach(Map::clear);
        organizations.clear();
    }


    static Resp<MatchInfo> getRouter(String method, String path, String ip) {
        // 格式化path
        String formatPath = formatResourcePath(path);
        if (resources.containsKey(method.toUpperCase())) {
            if (resources.get(method.toUpperCase()).containsKey(formatPath)) {
                return Resp.success(new MatchInfo(formatPath, resources.get(method.toUpperCase()).get(formatPath)));
            } else {
                // 使用正则路由
                List<RouterRContent> contents = resourcesR.get(method.toUpperCase());
                for (RouterRContent content : contents) {
                    Matcher matcher = content.getPattern().matcher(formatPath);
                    if (matcher.matches()) {
                        // 匹配到正则路由
                        // 获取原始（注册时的）Path
                        return Resp.success(new MatchInfo(content.getOriginalPath(), content.getRoleCode()));
                    }
                }
            }
            return Resp.success(null);
        } else {
            // 没有匹配的方法
            logger.warn("[" + method + "]  " + formatPath + " not implemented from " + ip);
            return Resp.notImplemented("[" + method + "] " + formatPath + " not implemented from " + ip);
        }
    }

    /**
     * 将非规范正则转成规范正则
     * <p>
     * 如 输入 /index/:id/  输出 （^/index/(?<id>[^/]+)/$ 的正则对象，Seq("id") ）
     *
     * @param path 非规范正则，用 :x 表示一个变量
     * @return （规范正则，变更列表）
     */
    private static Object[] getRegex(String path) {
        String pathR;
        if(path.endsWith("*")){
            pathR=path.substring(0,path.length()-1);
        }else{
            pathR=path;
        }
        List<String> named = new ArrayList<>();
        Matcher matcher = matchRegex.matcher(pathR);
        while (matcher.find()){
            String name = matcher.group();
            pathR = pathR.replaceAll(name, "(?<" + name.substring(1) + ">[^/]+)");
            named.add(name.substring(1));
        }
        if (path.endsWith("*")) {
            pathR += ".+";
        }
        return new Object[]{Pattern.compile("^" + pathR + "$"), named};
    }

    static class RouterRContent {
        private String originalPath;
        private Pattern pattern;
        private List<String> param;
        private Set<String> roleCode;

        public RouterRContent(String originalPath, Pattern pattern, List<String> param) {
            this.originalPath = originalPath;
            this.pattern = pattern;
            this.param = param;
            this.roleCode = new HashSet<>();
        }

        public String getOriginalPath() {
            return originalPath;
        }

        public void setOriginalPath(String originalPath) {
            this.originalPath = originalPath;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public void setPattern(Pattern pattern) {
            this.pattern = pattern;
        }

        public List<String> getParam() {
            return param;
        }

        public void setParam(List<String> param) {
            this.param = param;
        }

        public Set<String> getRoleCode() {
            return roleCode;
        }

        public void setRoleCode(Set<String> roleCode) {
            this.roleCode = roleCode;
        }
    }

    static class MatchInfo {

        private String originalPath;
        private Set<String> roleCode;

        public MatchInfo(String originalPath, Set<String> roleCode) {
            this.originalPath = originalPath;
            this.roleCode = roleCode;
        }

        public String getOriginalPath() {
            return originalPath;
        }

        public void setOriginalPath(String originalPath) {
            this.originalPath = originalPath;
        }

        public Set<String> getRoleCode() {
            return roleCode;
        }

        public void setRoleCode(Set<String> roleCode) {
            this.roleCode = roleCode;
        }
    }

}
