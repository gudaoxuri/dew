package com.tairanchina.csp.dew.auth.dto.management

import com.ecfront.dew.common.Page
import com.tairanchina.csp.dew.auth.domain.Account
import com.tairanchina.csp.dew.auth.dto.common.SafeStatusDTO
import com.tairanchina.csp.dew.auth.helper.CommonConverter

import scala.beans.BeanProperty
import scala.collection.JavaConverters._


case class AccountResp() extends SafeStatusDTO {

  @BeanProperty
  var id: String = _

  @BeanProperty
  var name: String = _

  @BeanProperty
  var roleIds: java.util.Map[String, String] = new java.util.HashMap()

  @BeanProperty
  var tenantId: String = _

}

object AccountResp {

  implicit def convert(ori: Account): AccountResp = {
    val accountResp = CommonConverter.convert[AccountResp](ori)
    accountResp.roleIds = ori.roles.asScala.filter(_.enabled).map(role => role.id -> role.name).toMap.asJava
    accountResp
  }

  implicit def convertPage(ori: org.springframework.data.domain.Page[Account]): Page[AccountResp] = {
    Page.build(ori.getNumber(), ori.getNumberOfElements(), ori.getTotalElements,
      ori.getContent.asScala.map(convert).asJava)
  }

}



