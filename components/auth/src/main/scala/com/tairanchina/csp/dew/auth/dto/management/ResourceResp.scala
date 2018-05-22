package com.tairanchina.csp.dew.auth.dto.management

import com.ecfront.dew.common.Page
import com.tairanchina.csp.dew.auth.domain.Resource
import com.tairanchina.csp.dew.auth.helper.CommonConverter

import scala.beans.BeanProperty


case class ResourceResp() {

  @BeanProperty
  var id: Int = _
  @BeanProperty
  var uri: String = _
  @BeanProperty
  var name: String = _
  @BeanProperty
  var category: String = _
  @BeanProperty
  var icon: String = _
  @BeanProperty
  var parentId: Int = _
  @BeanProperty
  var sort: Int = _
  @BeanProperty
  var tenantId: String = _

}

object ResourceResp {

  implicit def convert(ori: Resource): ResourceResp = {
    CommonConverter.convert[ResourceResp](ori)
  }

  implicit def convertPage(ori: org.springframework.data.domain.Page[Resource]): Page[ResourceResp] = {
    CommonConverter.convertPage[ResourceResp, Resource](ori)
  }

}