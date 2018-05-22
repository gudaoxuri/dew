package com.tairanchina.csp.dew.auth.dto.management

import javax.validation.constraints.NotNull

import com.ecfront.dew.common.$
import com.tairanchina.csp.dew.auth.domain.{Resource, Role}
import com.tairanchina.csp.dew.auth.dto.common.StatusDTO
import com.tairanchina.csp.dew.auth.helper.CommonConverter

import scala.beans.BeanProperty
import scala.collection.JavaConverters._


case class RoleModifyReq() extends StatusDTO {

  @NotNull
  @BeanProperty
  var name: String = _
  @BeanProperty
  var tenantId: String = ""
  @BeanProperty
  var resourceIds: java.util.Set[Int] = new java.util.HashSet()

}

object RoleModifyReq {

  def convert(ori: RoleModifyReq, dest: Role): Role = {
    val id = dest.id
    val tmp = CommonConverter.convert[Role](ori)
    $.bean.copyProperties(dest, tmp)
    dest.resources = ori.resourceIds.asScala.map(Resource(_)).asJava
    dest.id = id
    dest
  }

}