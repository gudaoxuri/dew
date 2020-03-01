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

package group.idealworld.dew.auth.dto.management

import com.ecfront.dew.common.$
import group.idealworld.dew.auth.domain.Ident
import group.idealworld.dew.auth.dto.common.StatusDTO
import group.idealworld.dew.auth.helper.CommonConverter
import javax.validation.constraints.NotNull
import group.idealworld.dew.auth.domain.Ident
import group.idealworld.dew.auth.dto.common.StatusDTO
import group.idealworld.dew.auth.helper.CommonConverter

import scala.beans.BeanProperty


case class IdentModifyReq() extends StatusDTO {

  @NotNull
  @BeanProperty
  var category: String = _

  @NotNull
  @BeanProperty
  var key: String = _

  @NotNull
  @BeanProperty
  var secret: String = ""

  @NotNull
  @BeanProperty
  var accountId: String = _

  @NotNull
  @BeanProperty
  var tenantId: String = _

}


object IdentModifyReq {

  def convert(ori: IdentModifyReq, dest: Ident): Ident = {
    val id = dest.id
    val tmp = CommonConverter.convert[Ident](ori)
    $.bean.copyProperties(dest, tmp)
    dest.id = id
    dest
  }

}





