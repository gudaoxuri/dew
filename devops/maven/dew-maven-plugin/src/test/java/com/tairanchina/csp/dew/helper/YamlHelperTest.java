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

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class YamlHelperTest {

    @Before
    public void before() {
        YamlHelper.init(new SystemStreamLog());
    }

    @Test
    public void testAll() {
        TestYaml o = YamlHelper.toObject(TestYaml.class, "# common.yaml\n" +
                "a: vala\n" +
                "b: valb\n" +
                "c: \n" +
                "  c1: valc1 \n" +
                "\n" +
                "# spec.yaml\n" +
                "c: \n" +
                "  c1: valc1_new\n" +
                "  c2: valc2\n" +
                "d: vald");
        Assert.assertEquals("valc1_new", o.getC().getC1());
        Assert.assertEquals("valc2", o.getC().getC2());
        Assert.assertEquals("vala", o.getA());
        Assert.assertEquals("valb", o.getB());
    }

}

