/*
 * Copyright 2020. the original author or authors.
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

package ms.dew.auth.sdk;

import ms.dew.Dew;
import ms.dew.core.auth.AuthAdapter;
import ms.dew.core.auth.dto.OptInfo;

import java.util.Optional;

/**
 * Auth sdk adapter.
 *
 * @author gudaoxuri
 */
public class AuthSDKAdapter implements AuthAdapter {

    @Override
    public <E extends OptInfo> Optional<E> getOptInfo(String token) {
        return Dew.context().optInfo();
    }

    @Override
    public void removeOptInfo(String token) {
        Dew.context().setInnerOptInfo(Optional.empty());
    }

    @Override
    public <E extends OptInfo> void setOptInfo(E optInfo) {
        Dew.context().setInnerOptInfo(Optional.of(optInfo));
    }
}
