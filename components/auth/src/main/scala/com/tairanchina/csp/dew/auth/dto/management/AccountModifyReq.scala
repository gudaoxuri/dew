package com.tairanchina.csp.dew.auth.dto.management

import javax.validation.constraints.NotNull

import com.ecfront.dew.common.$
import com.tairanchina.csp.dew.auth.domain.{Account, Role}
import com.tairanchina.csp.dew.auth.dto.common.StatusDTO
import com.tairanchina.csp.dew.auth.helper.CommonConverter

import scala.beans.BeanProperty
import scala.collection.JavaConverters._


case class AccountModifyReq() extends StatusDTO {

  @NotNull()
  @BeanProperty
  var name: String = _

  @BeanProperty
  var password: String = _

  @BeanProperty
  var roleIds: java.util.Set[String] = new java.util.HashSet()

  @NotNull
  @BeanProperty
  var tenantId: String = _

}

object AccountModifyReq {

  def convert(ori: AccountModifyReq, dest: Account): Account = {
    val id = dest.id
    val tmp = CommonConverter.convert[Account](ori)
    $.bean.copyProperties(dest, tmp)
    dest.roles = ori.roleIds.asScala.map(Role(_)).asJava
    dest.id = id
    dest
  }

}



