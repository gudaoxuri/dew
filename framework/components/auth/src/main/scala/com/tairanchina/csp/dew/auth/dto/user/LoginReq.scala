package com.tairanchina.csp.dew.auth.dto.user

import javax.validation.constraints.NotNull

import scala.beans.BeanProperty


case class LoginReq() {

  @NotNull
  @BeanProperty
  var identCategory: String = _

  @NotNull
  @BeanProperty
  var identKey: String = _

  @NotNull
  @BeanProperty
  var identSecret: String = _

  @NotNull
  @BeanProperty
  var byVC: Boolean = _

}





