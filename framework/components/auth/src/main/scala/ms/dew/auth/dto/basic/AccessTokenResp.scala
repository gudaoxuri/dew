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

package ms.dew.auth.dto.basic

import scala.beans.BeanProperty

class AccessTokenResp {

  @BeanProperty
  var accessToken: String = _
  @BeanProperty
  var serverCurrentTimestamp: Long = _
  @BeanProperty
  var expireTimestamp: Long = _


}

object AccessTokenResp {

  def apply(accessToken: String, expireTimestamp: Long): AccessTokenResp = {
    val resp = new AccessTokenResp()
    resp.accessToken = accessToken
    resp.serverCurrentTimestamp = System.currentTimeMillis()
    resp.expireTimestamp = expireTimestamp
    resp
  }

}
