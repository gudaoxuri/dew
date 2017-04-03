package com.ecfront.dew.gateway.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LocalCacheContainer {

    private static final Logger logger = LoggerFactory.getLogger(LocalCacheContainer.class);

    private static final Map<String, String> RESOURCES = new ConcurrentHashMap<>();
    private static final Map<String, String> RESOURCE_MAPPING = new ConcurrentHashMap<>();

    private static final Map<String, Set<String>> ROLES = new ConcurrentHashMap<>();

    private static final PathMatcher pathMatcher = new AntPathMatcher();
    private static final String SPLIT = "@";

    public static void addResource(String code, String method, String path) {
        RESOURCES.put(code, method + SPLIT + path);
        RESOURCE_MAPPING.put(method + SPLIT + path, code);
    }

    public static void removeResource(String code) {
        RESOURCE_MAPPING.remove(RESOURCES.get(code));
        RESOURCES.remove(code);
    }

    public static void addRole(String code, Set<String> resourceCodes) {
        ROLES.put(code, resourceCodes);
    }

    public static void removeRole(String code) {
        ROLES.remove(code);
    }

    public static void flushAuth() {
        RESOURCES.clear();
        ROLES.clear();
    }

    public static boolean auth(Set<String> roleCodes, String bestMathResourceCode) {
        return ROLES.entrySet().stream()
                .filter(role -> roleCodes.contains(role.getKey()))
                .anyMatch(role -> role.getValue().stream().anyMatch(resCode -> resCode.equals(bestMathResourceCode)));
    }

    public static Optional<String> getBestMathResourceCode(String method, String requestPath) {
        boolean isMatch;
        // Direct match?
        isMatch = RESOURCE_MAPPING.containsKey(method + SPLIT + requestPath);
        if (isMatch) {
            return Optional.of(RESOURCE_MAPPING.get(method + SPLIT + requestPath));
        }
        // Pattern match?
        List<String> matchingPatterns =
                RESOURCES.values().stream()
                        .filter(res -> pathMatcher.match(res, method + SPLIT + requestPath)).collect(Collectors.toList());
        if (!matchingPatterns.isEmpty()) {
            Comparator<String> patternComparator = pathMatcher.getPatternComparator(requestPath);
            matchingPatterns.sort(patternComparator);
            return Optional.of(RESOURCE_MAPPING.get(matchingPatterns.get(0)));
        }
        return Optional.empty();
    }


}
