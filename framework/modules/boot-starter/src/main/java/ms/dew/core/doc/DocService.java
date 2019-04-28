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

package ms.dew.core.doc;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.HttpHelper;
import com.ecfront.dew.common.Resp;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Doc service.
 * <p>
 * 用于生成离线Asciidoc文档
 *
 * @author gudaoxuri
 */
@Service
public class DocService {

    private static final Logger logger = LoggerFactory.getLogger(DocService.class);

    /**
     * Generate offline doc.
     *
     * @param docName         the doc name
     * @param docDesc         the doc desc
     * @param visitUrls       the visit urls
     * @param swaggerJsonUrls the swagger json urls
     * @return the resp
     * @throws IOException the io exception
     */
    public Resp<String> generateOfflineDoc(String docName, String docDesc, Map<String, String> visitUrls, List<String> swaggerJsonUrls)
            throws IOException {
        List<String> swaggerJsons = new ArrayList<>();
        for (String url : swaggerJsonUrls) {
            try {
                HttpHelper.ResponseWrap result = $.http.getWrap(url);
                if (result.statusCode == 200) {
                    swaggerJsons.add(result.result);
                } else {
                    logger.warn("Fetch swagger url [" + url + "] error, code:" + result.statusCode);
                }
            } catch (IOException e) {
                logger.error("Fetch swagger url [" + url + "] error.", e);
                throw e;
            }
        }
        return doGenerateOfflineDoc(docName, docDesc, visitUrls, swaggerJsons);
    }

    /**
     * Do generate offline doc.
     *
     * @param docName      the doc name
     * @param docDesc      the doc desc
     * @param visitUrls    the visit urls
     * @param swaggerJsons the swagger jsons
     * @return the resp
     */
    public Resp<String> doGenerateOfflineDoc(String docName, String docDesc, Map<String, String> visitUrls, List<String> swaggerJsons) {
        List<JsonNode> swaggers = swaggerJsons.stream().map(json -> $.json.toJson(json)).collect(Collectors.toList());
        // 文档概要
        StringBuilder asciidocContent = new StringBuilder(""
                + "= " + docName + "\n"
                + "" + $.time().yyyy_MM_dd_HH_mm_ss.format(new Date()) + "\n"
                + ":doctype: book\n"
                + ":encoding: utf-8\n"
                + ":lang: zh-CN\n"
                + ":toc: left\n"
                + ":toclevels: 4\n"
                + ":numbered:\n\n");
        asciidocContent.append("=====\n" + "[%hardbreaks]\n")
                .append(docDesc)
                .append("\n")
                .append("=====\n")
                .append("\n")
                .append("[NOTE]\n")
                .append(".接入地址\n")
                .append("====\n")
                .append("[%hardbreaks]\n")
                .append(visitUrls.entrySet().stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue())
                        .collect(Collectors.joining("\n")))
                .append("\n")
                .append("====\n")
                .append("\n\n")
                .append("本系统包含如下服务：\n")
                .append("\n")
                .append(swaggers.stream()
                        .map(swagger ->
                                ". <<"
                                        + encode(getTag(DocAutoConfiguration.FLAG_APPLICATION_NAME, swagger))
                                        + "," + swagger.path("info").path("title").asText("")
                                        + ">>" + " 接口数:"
                                        + stream(swagger.path("paths")).mapToLong(JsonNode::size).sum()
                        ).collect(Collectors.joining("\n"))).append("\n\n");
        // 文档包含服务列表
        swaggers.forEach(swagger -> {
            // Basic info for per swagger
            asciidocContent
                    .append("<<<\n\n" + "[[")
                    .append(encode(getTag(DocAutoConfiguration.FLAG_APPLICATION_NAME, swagger)))
                    .append("]]\n").append("== ")
                    .append(swagger.path("info").path("title").asText(""))
                    .append("\n")
                    .append("\n")
                    .append("[%hardbreaks]\n")
                    .append("**服务简介**: ")
                    .append(swagger.path("info").path("description").asText(""))
                    .append("\n").append("**当前版本**: ")
                    .append(swagger.path("info").path("version").asText(""))
                    .append("\n");
            if (swagger.path("info").has("contact")) {
                asciidocContent.append("**对接信息**: ")
                        .append(swagger.path("info").path("contact").path("name").asText(""))
                        .append(" ")
                        .append("[")
                        .append(swagger.path("info").path("contact").path("email").asText(""))
                        .append("]\n");
            }
            asciidocContent.append("**接口数量**: ")
                    .append(stream(swagger.path("paths")).mapToLong(JsonNode::size).sum() + "\n");
            asciidocContent.append("**服务URI**: /")
                    .append(getTag(DocAutoConfiguration.FLAG_APPLICATION_NAME, swagger))
                    .append(swagger.path("basePath").asText(""))
                    .append("\n");

            // 每个服务的API详细说明
            stream(swagger.path("tags"))
                    .filter(tag -> !tag.path("name").asText().equals(DocAutoConfiguration.FLAG_APPLICATION_NAME))
                    .forEach(tag -> {
                        // 以Controller分组显示
                        String tagName = tag.path("name").asText();
                        asciidocContent.append("\n")
                                .append("[[" + encode(tagName) + "]]\n")
                                .append("=== " + tag.path("description").asText() + "\n")
                                .append("'''\n");
                        // 每个Controller下的API详细说明
                        stream(swagger.path("paths").fields())
                                .forEach(path -> {
                                    String uri = path.getKey();
                                    stream(path.getValue().fields()).forEach(apiInfo -> {
                                        // 命中分组（Controller）
                                        if (array(apiInfo.getValue().path("tags")).contains(tagName)) {
                                            String httpMethod = apiInfo.getKey();
                                            JsonNode api = apiInfo.getValue();
                                            asciidocContent.append("\n")
                                                    .append("[[" + encode(httpMethod + " " + uri) + "]]\n")
                                                    .append("==== ")
                                                    .append((api.has("deprecated")
                                                            && api.path("deprecated").asBoolean()
                                                            ? "[.line-through]#" + api.path("summary").asText() + "#" : api.path("summary").asText()))
                                                    .append("\n")
                                                    .append("'''\n")
                                                    .append("\n")
                                                    .append("----\n")
                                                    .append("" + api.path("description").asText(""))
                                                    .append("\n")
                                                    .append("----\n")
                                                    .append("\n")
                                                    .append("请求URI: ``" + httpMethod.toUpperCase() + " " + uri)
                                                    .append("``\n")
                                                    .append("[%hardbreaks]\n");
                                            if (api.has("consumes")) {
                                                asciidocContent.append("consumes: ``" + String.join(";", array(api.path("consumes"))) + "``\n");
                                            }
                                            if (api.has("produces")) {
                                                asciidocContent.append("produces: ``" + String.join(";", array(api.path("produces"))) + "``\n");
                                            }
                                            if (api.has("parameters")) {
                                                asciidocContent.append("\n")
                                                        .append("请求参数: \n")
                                                        .append("[options=\"header\", cols=\".^2a,.^10a,.^4a,.^1a,.^10a,.^5a\"]\n")
                                                        .append("|===\n")
                                                        .append("|位置 |名称 |类型 |必填 |说明 |示例 \n ");
                                                stream(api.path("parameters"))
                                                        .filter(p -> !p.path("in").asText().equals("body"))
                                                        .sorted(Comparator.comparing(p -> p.path("in").asText()))
                                                        .forEach(parameter -> {
                                                            String typeStr = "";
                                                            switch (parameter.path("type").asText("")) {
                                                                case "":
                                                                    // 不存在
                                                                    if (parameter.has("items")) {
                                                                        // MAP类型
                                                                        String valueType =
                                                                                parameter.path("items").path("type").path("additionalProperties")
                                                                                        .asText("Object");
                                                                        // TODO 引用类型
                                                                        //"additionalProperties": {
                                                                        //  "$ref": "#/components/schemas/ComplexModel"
                                                                        //}
                                                                        typeStr = "map<String," + valueType + ">";
                                                                    } else if (parameter.has("schema")) {
                                                                        // TODO 引用类型
                                                                        //"schema": {
                                                                        //  "$ref": "#/definitions/DeepRequest"
                                                                        //}
                                                                        typeStr = parameter.path("schema").path("type").asText();
                                                                    }
                                                                    break;
                                                                case "array":
                                                                    if (parameter.has("items")) {
                                                                        // ARRAY类型
                                                                        String valueType = parameter.path("items").path("type").asText();
                                                                        // TODO 引用类型
                                                                        //"additionalProperties": {
                                                                        //  "$ref": "#/components/schemas/ComplexModel"
                                                                        //}
                                                                        typeStr = "array<" + valueType + ">";
                                                                    } else if (parameter.has("schema")) {
                                                                        // TODO 引用类型
                                                                        //"schema": {
                                                                        //  "$ref": "#/definitions/DeepRequest"
                                                                        //}
                                                                        typeStr = "array<" + parameter.path("schema").path("type").asText() + ">";
                                                                    }
                                                                    break;
                                                                default:
                                                                    typeStr = parameter.path("type").asText();
                                                                    break;
                                                            }
                                                            if (parameter.has("enum")) {
                                                                // 枚举类型
                                                                typeStr = stream(parameter.path("enum")).map(JsonNode::asText)
                                                                        .collect(Collectors.joining("/", "enum:", ""));
                                                            }
                                                            if (typeStr.isEmpty()) {
                                                                logger.warn("Parameter TYPE parse error in " + $.json.toJsonString(parameter));
                                                            }
                                                            asciidocContent.append("\n")
                                                                    .append("|" + parameter.path("in").asText() + "\n")
                                                                    .append("|" + parameter.path("name").asText() + "\n")
                                                                    .append("|" + typeStr + "\n")
                                                                    .append("|" + (parameter.path("required").asBoolean() ? "Y" : "") + "\n")
                                                                    .append("|" + parameter.path("description").asText("") + "\n")
                                                                    .append("|" + parameter.path("x-example").asText("") + "\n")
                                                                    .append("\n");
                                                        });
                                                List<JsonNode> bodyParameters = stream(api.path("parameters"))
                                                        .filter(p -> p.path("in").asText().equals("body"))
                                                        .collect(Collectors.toList());
                                                AtomicLong bodyParameterSize = new AtomicLong(bodyParameters.size());
                                                if (bodyParameterSize.get() != 0) {
                                                    // 存在Body
                                                    String bodyJson = bodyParameters.stream().map(parameter -> {
                                                        boolean isArray = false;
                                                        String bodyTypeOrStruct = "";
                                                        String type = parameter.path("schema").path("type").asText("");
                                                        String ref = parameter.path("schema").path("$ref").asText("");
                                                        if (!type.isEmpty()) {
                                                            if ("array".equals(type)) {
                                                                isArray = true;
                                                                type = parameter.path("schema").path("items").path("type").asText("");
                                                                if (!type.isEmpty()) {
                                                                    bodyTypeOrStruct = "<" + type + ">";
                                                                } else {
                                                                    ref = parameter.path("schema").path("items").path("$ref").asText("");
                                                                }
                                                            } else {
                                                                bodyTypeOrStruct = "<" + type + ">";
                                                            }
                                                        }
                                                        if (bodyTypeOrStruct.isEmpty() && !ref.isEmpty()) {
                                                            // 引用类型
                                                            ref = ref.substring("#/definitions/".length());
                                                            bodyTypeOrStruct = fillRelModel(swagger.path("definitions"), ref, 2, new HashSet<>());
                                                        }
                                                        if (bodyTypeOrStruct.isEmpty()) {
                                                            logger.warn("Parameter TYPE parse error in " + $.json.toJsonString(parameter));
                                                        }
                                                        if (isArray) {
                                                            bodyTypeOrStruct = "[" + bodyTypeOrStruct + level(1) + ",...]";
                                                        }
                                                        return level(1)
                                                                + "// " + (parameter.path("required").asBoolean() ? "[必填]" : "")
                                                                + parameter.path("description").asText("")
                                                                + (parameter.has("x-example") ? " e.g. "
                                                                + parameter.path("x-example").asText() : "")
                                                                + "\n"
                                                                + level(1)
                                                                + "\"" + parameter.path("name").asText()
                                                                + "\":" + bodyTypeOrStruct
                                                                + (bodyParameterSize.decrementAndGet() == 0 ? "" : ",")
                                                                + "\n";
                                                    })
                                                            .collect(Collectors.joining("", "{\n", "}\n"));
                                                    asciidocContent.append("\n")
                                                            .append("|body\n")
                                                            .append("5+|\n")
                                                            .append("[source]\n")
                                                            .append("----\n")
                                                            .append(bodyJson)
                                                            .append("----\n")
                                                            .append("\n");
                                                }
                                                asciidocContent.append("\n|===\n");
                                            }
                                            if (api.has("responses")) {
                                                asciidocContent.append("\n")
                                                        .append("响应结果: \n");
                                                String ref = api.get("responses").path("200").path("schema").path("$ref").asText("");
                                                if (ref.contains("#/definitions/Resp")) {
                                                    // Resp类型
                                                    asciidocContent.append("\n")
                                                            .append("[source]\n")
                                                            .append("----\n")
                                                            .append(parseTypeOrStructBySchema(swagger.path("definitions"),
                                                                    api.get("responses").path("200").path("schema"), 0) + "\n")
                                                            .append("----\n")
                                                            .append("\n");
                                                } else {
                                                    // Http state code 类型
                                                    asciidocContent.append("[options=\"header\", cols=\".^2a,.^10a,.^20a,.^10a\"]\n")
                                                            .append("|===\n")
                                                            .append("|状态码 |说明 |类型 |示例 \n ");
                                                    stream(api.get("responses").fields()).forEach(response -> {
                                                        String typeOrStruct = parseTypeOrStructBySchema(swagger.path("definitions"),
                                                                response.getValue().get("schema"), 0);
                                                        if (!typeOrStruct.trim().isEmpty()) {
                                                            typeOrStruct = "[source]\n----\n" + typeOrStruct + "\n----\n";
                                                        }
                                                        asciidocContent.append("\n")
                                                                .append("|" + response.getKey() + "\n")
                                                                .append("|" + response.getValue().path("description").asText("") + "\n")
                                                                .append("|\n")
                                                                .append(typeOrStruct)
                                                                .append("|" + response.getValue().path("x-example").asText("") + "\n")
                                                                .append("\n");
                                                    });
                                                    asciidocContent.append("\n|===\n");
                                                }
                                            }

                                        }
                                    });
                                });
                    });
        });
        return Resp.success(asciidocContent.toString());
    }

    private String parseTypeOrStructBySchema(JsonNode definitions, JsonNode schema, int level) {
        try {
            if (schema == null) {
                return "";
            }
            if (schema.has("type")) {
                if (schema.get("type").asText().equals("array")) {
                    // Array
                    if (schema.get("items").has("type")) {
                        return "["
                                + schema.get("items").get("type").asText()
                                + "]";
                    } else {
                        return "["
                                + fillRelModel(definitions,
                                schema.get("items").get("$ref").asText().substring("#/definitions/".length()),
                                level + 1,
                                new HashSet<>())
                                + "]";
                    }
                } else if (schema.get("type").asText().equals("object") && schema.has("additionalProperties")) {
                    // Map
                    return "{\n" + level(level + 1) + "\"<some keys>\":"
                            + parseTypeOrStructBySchema(definitions, schema.get("additionalProperties"), level + 2)
                            + "\n" + level(level) + "}";
                } else {
                    return schema.get("type").asText();
                }
            } else {
                return fillRelModel(definitions, schema.get("$ref").asText().substring("#/definitions/".length()), level + 1, new HashSet<>());
            }
        } catch (Exception e) {
            logger.warn("Parse schema [" + $.json.toJsonString(schema) + "] error", e);
            return "";
        }
    }

    private String fillRelModel(JsonNode definitions, String modelName, int level, Set<String> filledModels) {
        try {
            if (filledModels.contains(modelName)) {
                // 防止无限循环，如多级菜单
                return "<见 " + modelName + ">";
            }
            filledModels.add(modelName);
            if (!definitions.has(modelName)) {
                // e.g. MultipartFile, 在 definitions中没有定义
                return "<" + modelName + ">";
            }
            if (modelName.startsWith("Map«")) {
                return "\"<some keys>\":" + parseTypeOrStructBySchema(definitions, definitions.get(modelName).get("additionalProperties"), level + 1);
            }
            List<Map.Entry<String, JsonNode>> properties =
                    stream(definitions.get(modelName).path("properties").fields()).collect(Collectors.toList());
            AtomicLong parameterSize = new AtomicLong(properties.size());
            return properties.stream().map(prop -> {
                boolean isArray = false;
                String bodySubJsonOrSimpleType = "";
                String type = prop.getValue().path("type").asText("");
                String ref = prop.getValue().path("$ref").asText("");
                if (!type.isEmpty()) {
                    if ("array".equals(type)) {
                        isArray = true;
                        type = prop.getValue().path("items").path("type").asText("");
                        if (!type.isEmpty()) {
                            bodySubJsonOrSimpleType = "<" + type + ">";
                        } else {
                            ref = prop.getValue().path("items").path("$ref").asText("");
                        }
                    } else {
                        bodySubJsonOrSimpleType = "<" + type + ">";
                    }
                }
                if (bodySubJsonOrSimpleType.isEmpty() && !ref.isEmpty()) {
                    // 引用类型
                    ref = ref.substring("#/definitions/".length());
                    bodySubJsonOrSimpleType = fillRelModel(definitions, ref, level + 1, filledModels);
                }
                if (bodySubJsonOrSimpleType.isEmpty()) {
                    logger.warn("Parameter TYPE parse error in " + $.json.toJsonString(prop));
                }
                if (isArray) {
                    bodySubJsonOrSimpleType = "[" + bodySubJsonOrSimpleType + ",...]";
                }
                String desc = (ref.isEmpty() ? "" : "[类型: " + ref + "] ") + "" + prop.getValue().path("description").asText("");

                return (desc.trim().isEmpty() ? "" : level(level) + "// " + desc + "\n")
                        + level(level)
                        + "\"" + prop.getKey()
                        + "\":" + bodySubJsonOrSimpleType
                        + (parameterSize.decrementAndGet() == 0 ? "" : ",")
                        + "\n";
            }).collect(Collectors.joining("", "{\n", level(level - 1) + "}"));
        } catch (Exception e) {
            logger.warn("Fill rel model [" + modelName + "] error.", e);
            return "";
        }
    }

    private String encode(String name) {
        try {
            return "_" + $.security.digest.digest(name, "MD5");
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    private String getTag(String tagName, JsonNode json) {
        Iterator<JsonNode> it = json.path("tags").elements();
        while (it.hasNext()) {
            JsonNode curr = it.next();
            if (curr.path("name").asText().equals(tagName)) {
                return curr.path("description").asText();
            }
        }
        return "";
    }

    private Stream<JsonNode> stream(JsonNode json) {
        return stream(json.elements());
    }

    private <E> Stream<E> stream(Iterator<E> it) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED),
                false);
    }

    private List<String> array(JsonNode json) {
        return stream(json.elements()).map(JsonNode::asText).collect(Collectors.toList());
    }

    private String repeat(String s, int repeat) {
        return new String(new char[repeat]).replace("\0", s);
    }

    private String level(int l) {
        return repeat(" ", l * 2);
    }

}
