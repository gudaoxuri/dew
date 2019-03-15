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

package com.tairanchina.csp.dew.auth.helper

import com.ecfront.dew.common.{$, Page}

import scala.collection.JavaConverters._

object CommonConverter {

  def convert[E](ori: AnyRef)(implicit m: Manifest[E]): E = {
    val dest = m.runtimeClass.newInstance().asInstanceOf[E]
    $.bean.copyProperties(dest, ori)
    dest
  }

  def convertPage[E, O <: AnyRef](ori: org.springframework.data.domain.Page[O])(implicit m: Manifest[E]): Page[E] = {
    if (ori.getContent.size() > 0) {
      Page.build(ori.getNumber(), ori.getSize(), ori.getTotalElements, ori.getContent.asScala.map(convert[E](_)).asJava)
    } else {
      val dto = new Page[E]
      dto.setPageNumber(ori.getNumber())
      dto.setPageSize(ori.getSize())
      dto.setRecordTotal(ori.getTotalElements)
      dto.setPageTotal(ori.getTotalPages)
      dto.setObjects(null)
      dto
    }
  }
}
