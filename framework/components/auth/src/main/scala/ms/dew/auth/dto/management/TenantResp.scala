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

package ms.dew.auth.dto.management

import com.ecfront.dew.common.Page
import ms.dew.auth.domain.Tenant
import ms.dew.auth.helper.CommonConverter
import ms.dew.auth.domain.Tenant
import ms.dew.auth.dto.common.SafeStatusDTO
import ms.dew.auth.helper.CommonConverter

import scala.beans.BeanProperty

case class TenantResp() extends SafeStatusDTO {

  @BeanProperty
  var id: String = _
  @BeanProperty
  var secret: String = _
  @BeanProperty
  var name: String = _

}

object TenantResp {

  implicit def convert(ori: Tenant): TenantResp = {
    CommonConverter.convert[TenantResp](ori)
  }

  implicit def convertPage(ori: org.springframework.data.domain.Page[Tenant]): Page[TenantResp] = {
    CommonConverter.convertPage[TenantResp, Tenant](ori)
  }

}