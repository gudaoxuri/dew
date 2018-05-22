package com.tairanchina.csp.dew.auth.dto.management

import javax.validation.constraints.NotNull

import com.tairanchina.csp.dew.auth.domain.{Resource, Role}
import com.tairanchina.csp.dew.auth.dto.common.StatusDTO
import com.tairanchina.csp.dew.auth.helper.CommonConverter
import scala.collection.JavaConverters._
import scala.beans.BeanProperty


case class RoleAddReq() extends StatusDTO {

  @NotNull
  @BeanProperty
  var name: String = _
  @BeanProperty
  var tenantId: String = ""
  @BeanProperty
  var resourceIds: java.util.Set[Int] = new java.util.HashSet()

}

object RoleAddReq {

  implicit def convert(ori: RoleAddReq): Role = {
    val role = CommonConverter.convert[Role](ori)
    role.resources = ori.resourceIds.asScala.map(Resource(_)).asJava
    role
  }

}

