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

package group.idealworld.dew.devops.maven.mojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import static org.apache.maven.plugins.annotations.LifecyclePhase.VALIDATE;

/**
 * Init mojo.
 * <p>
 * NOTE: 此mojo不能单独调用，仅与 release 配合使用
 * <p>
 * 默认绑定到 validate phase，
 * 是为抢先初始化配置及执行 skip 操作（避免不必要的compile/jar/install/deploy等）
 *
 * @author gudaoxuri
 */
@Mojo(name = "init", defaultPhase = VALIDATE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class InitMojo extends BasicMojo {

    @Override
    protected boolean executeInternal() {
        return true;
    }

}
