package com.tairanchina.csp.dew.auth.dto.management

import com.ecfront.dew.common.Page
import com.tairanchina.csp.dew.auth.domain.Role
import com.tairanchina.csp.dew.auth.dto.common.SafeStatusDTO
import com.tairanchina.csp.dew.auth.helper.CommonConverter

import scala.beans.BeanProperty
import scala.collection.JavaConverters._


case class RoleResp() extends SafeStatusDTO {

  @BeanProperty
  var id: String = _
  @BeanProperty
  var name: String = _
  @BeanProperty
  var tenantId: String = _
  @BeanProperty
  var resourceIds: java.util.Map[Int, String] = new java.util.HashMap()

}

object RoleResp {

  implicit def convert(ori: Role): RoleResp = {
    val roleResp = CommonConverter.convert[RoleResp](ori)
    roleResp.resourceIds = ori.resources.asScala.map(res => res.id -> res.name).toMap.asJava
    roleResp
  }

  implicit def convertPage(ori: org.springframework.data.domain.Page[Role]): Page[RoleResp] = {
    Page.build(ori.getNumber(), ori.getNumberOfElements(), ori.getTotalElements,
      ori.getContent.asScala.map(convert).asJava)
  }

}
