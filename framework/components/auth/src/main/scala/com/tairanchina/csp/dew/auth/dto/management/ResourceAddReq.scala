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

package com.tairanchina.csp.dew.auth.dto.management

import com.tairanchina.csp.dew.auth.domain.Resource
import com.tairanchina.csp.dew.auth.helper.CommonConverter
import javax.validation.constraints.NotNull

import scala.beans.BeanProperty


case class ResourceAddReq() {

  @NotNull()
  @BeanProperty
  var uri: String = _
  @NotNull()
  @BeanProperty
  var name: String = _
  @NotNull()
  @BeanProperty
  var category: String = _
  @BeanProperty
  var icon: String = ""
  @BeanProperty
  var parentId: Int = 0
  @BeanProperty
  var sort: Int = 0
  @BeanProperty
  var tenantId: String = ""

}

object ResourceAddReq {

  implicit def convert(ori: ResourceAddReq): Resource = {
    CommonConverter.convert[Resource](ori)
  }

}