package com.tairanchina.csp.dew.auth.dto.user

import com.ecfront.dew.common.Page
import com.tairanchina.csp.dew.auth.domain.Account
import com.tairanchina.csp.dew.auth.dto.common.SafeStatusDTO

import scala.beans.BeanProperty
import scala.collection.JavaConverters._


case class UserResp() extends SafeStatusDTO {

  @BeanProperty
  var id: String = _

  @BeanProperty
  var name: String = _

  @BeanProperty
  var roles: java.util.Map[String, String] = new java.util.HashMap()

  @BeanProperty
  var tenantId: String = _

}

object UserResp {

  implicit def convert(ori: Account): UserResp = {
    val userResp = UserResp()
    userResp.id = ori.id
    userResp.name = ori.name
    userResp.roles = ori.roles.asScala.map(role => role.id -> role.name).toMap.asJava
    userResp
  }

  implicit def convertPage(ori: org.springframework.data.domain.Page[Account]): Page[UserResp] = {
    Page.build(ori.getNumber(), ori.getNumberOfElements(), ori.getTotalElements,
      ori.getContent.asScala.map(convert).asJava)
  }

}





