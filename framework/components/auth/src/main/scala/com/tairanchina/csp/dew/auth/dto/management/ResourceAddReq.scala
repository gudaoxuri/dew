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