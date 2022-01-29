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

package group.idealworld.dew.sdkgen.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Name helper.
 *
 * @author gudaoxuri
 */
public class NameHelper {

    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("[^a-zA-Z0-9]+(\\w{1})");

    /**
     * Format package.
     *
     * @param pkg the pkg
     * @return the result
     */
    public static String formatPackage(String pkg) {
        return pkg.toLowerCase().replaceAll("[^a-zA-Z0-9.]+", "");
    }

    /**
     * Format class name.
     *
     * @param name the name
     * @return the result
     */
    public static String formatClassName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length() - 4) + "SDK";
        Matcher matcher = CLASS_NAME_PATTERN.matcher(name);
        return matcher.replaceAll(s -> s.group(1).toUpperCase());
    }
}
