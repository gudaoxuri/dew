package com.tairanchina.csp.dew.auth.dto.user

import javax.validation.constraints.NotNull

import scala.beans.BeanProperty


case class UserModifyReq() {

  @NotNull()
  @BeanProperty
  var name: String = _

  @BeanProperty
  var password: String = _

}





