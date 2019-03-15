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

package com.tairanchina.csp.dew.auth.dto.common

import java.util.Date

import scala.beans.BeanProperty

abstract class SafeDTO {

  @BeanProperty
  var createTime: Date = _
  @BeanProperty
  var updateTime: Date = _
  @BeanProperty
  var createUser: String = _
  @BeanProperty
  var updateUser: String = _

}