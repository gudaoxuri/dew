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

import org.apache.maven.plugin.logging.Log;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

public class YamlHelper {

    private static Log log;
    private static Yaml yaml;

    public static void init(Log log) {
        if (yaml == null) {
            YamlHelper.log = log;
            Representer representer = new io.kubernetes.client.util.Yaml.CustomRepresenter();
            representer.getPropertyUtils().setSkipMissingProperties(true);
            yaml = new Yaml(new io.kubernetes.client.util.Yaml.CustomConstructor(), representer);
        }
    }

    public static <T> T toObject(String content) {
        return yaml.load(content);
    }

    public static <T> T toObject(Class<T> clazz, String content) {
        return yaml.loadAs(content, clazz);
    }

    public static <T> T toObject(Class<T> clazz, String... contents) {
        String mergedContent = String.join("\r\n", contents);
        return yaml.loadAs(mergedContent, clazz);
    }

    public static String toString(Object content) {
        String str = yaml.dump(content);
        if (str.startsWith("!!")) {
            return str.substring(str.indexOf('\n') + 1);
        }
        return str;
    }

}
