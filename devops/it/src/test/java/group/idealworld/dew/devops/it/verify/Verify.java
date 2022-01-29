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

package group.idealworld.dew.devops.it.verify;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.validation.json.JsonMessageValidationContext;
import com.consol.citrus.validation.json.JsonTextMessageValidator;
import com.consol.citrus.validation.matcher.ValidationMatcherConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.io.File;
import java.io.IOException;


/**
 * The interface Verify.
 *
 * @author gudaoxuri
 */
public interface Verify {

    /**
     * Verify.
     *
     * @param basedir the basedir
     * @throws Exception the exception
     */
    default void verify(File basedir) throws Exception {
        String expectedResPath = new File(basedir, File.separator + "expected").getPath() + File.separator;
        String buildPath = new File(basedir, File.separator + "target").getPath() + File.separator;
        doVerify(buildPath, expectedResPath);
    }

    /**
     * Do verify.
     *
     * @param buildPath       the build path
     * @param expectedResPath the expected res path
     * @throws Exception the exception
     */
    void doVerify(String buildPath, String expectedResPath) throws Exception;

    /**
     * Verify resource descriptors.
     *
     * @param message      the message
     * @param expectedText the expected text
     * @param actualText   the actual text
     * @throws IOException    the io exception
     * @throws ParseException the parse exception
     */
    default void verifyResourceDescriptors(String message, String expectedText, String actualText) throws IOException, ParseException {
        JsonTextMessageValidator validator = new JsonTextMessageValidator();
        validator.setStrict(false);

        TestContext context = new TestContext();
        context.getValidationMatcherRegistry()
                .getValidationMatcherLibraries()
                .add(new ValidationMatcherConfig().getValidationMatcherLibrary());

        validator.validateJson(message,
                (JSONObject) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(toJson(actualText)),
                (JSONObject) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(toJson(expectedText)),
                new JsonMessageValidationContext(),
                context,
                JsonPath.parse(actualText));
    }

    /**
     * To json string.
     *
     * @param yaml the yaml
     * @return the string
     * @throws IOException the io exception
     */
    default String toJson(String yaml) throws IOException {
        Object obj = new ObjectMapper(new YAMLFactory()).readValue(yaml, Object.class);
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

}
