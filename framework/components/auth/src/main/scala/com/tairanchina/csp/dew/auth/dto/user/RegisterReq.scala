package com.tairanchina.csp.dew.auth.dto.user

import javax.validation.constraints.NotNull

import scala.beans.BeanProperty


case class RegisterReq() {

  @BeanProperty
  var name: String = _

  @NotNull
  @BeanProperty
  var identCategory: String = _

  @NotNull
  @BeanProperty
  var identKey: String = _

  @BeanProperty
  var identSecret: String = _

}





