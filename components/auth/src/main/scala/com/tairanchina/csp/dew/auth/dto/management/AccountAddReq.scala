package com.tairanchina.csp.dew.auth.dto.management

import com.tairanchina.csp.dew.auth.domain.{Account, Role}
import com.tairanchina.csp.dew.auth.dto.common.StatusDTO
import com.tairanchina.csp.dew.auth.helper.CommonConverter
import javax.validation.constraints.NotNull

import scala.beans.BeanProperty
import scala.collection.JavaConverters._


case class AccountAddReq() extends StatusDTO {

  @NotNull
  @BeanProperty
  var name: String = _

  @NotNull
  @BeanProperty
  var password: String = _

  @BeanProperty
  var roleIds: java.util.Set[String] = new java.util.HashSet()

  @NotNull
  @BeanProperty
  var tenantId: String = _

}

object AccountAddReq {

  implicit def convert(ori: AccountAddReq): Account = {
    val account = CommonConverter.convert[Account](ori)
    account.roles = ori.roleIds.asScala.map(Role(_)).asJava
    account
  }

}



