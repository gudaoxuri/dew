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

import group.idealworld.dew.devops.kernel.util.DewLog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Yaml helper test.
 *
 * @author gudaoxuri
 */
public class YamlHelperTest {

    /**
     * Before.
     */
    @BeforeEach
    public void before() {
        YamlHelper.init(DewLog.build(this.getClass()));
    }

    /**
     * Test all.
     */
    @Test
    public void testAll() {
        TestYaml o = YamlHelper.toObject(TestYaml.class,
                "# common.yaml\n"
                        + "fa: vala\n"
                        + "fb: valb\n"
                        + "fc: \n"
                        + "  c1: valc1 \n"
                        + "\n"
                        + "# spec.yaml\n"
                        + "fc: \n"
                        + "  c1: valc1_new\n"
                        + "  c2: valc2\n"
                        + "fd: vald");
        Assertions.assertEquals("valc1_new", o.getFc().getC1());
        Assertions.assertEquals("valc2", o.getFc().getC2());
        Assertions.assertEquals("vala", o.getFa());
        Assertions.assertEquals("valb", o.getFb());
        Assertions.assertNull(o.getFd());
        o.setFd(false);
        String yaml = YamlHelper.toString(o);
        o = YamlHelper.toObject(TestYaml.class, yaml);
        Assertions.assertEquals("valc1_new", o.getFc().getC1());
        Assertions.assertEquals("valc2", o.getFc().getC2());
        Assertions.assertEquals("vala", o.getFa());
        Assertions.assertEquals("valb", o.getFb());
        Assertions.assertFalse(o.getFd());
    }

}

