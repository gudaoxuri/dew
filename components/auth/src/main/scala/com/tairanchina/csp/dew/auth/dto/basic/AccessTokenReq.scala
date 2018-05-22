package com.tairanchina.csp.dew.auth.dto.basic

import javax.validation.constraints.NotNull

import scala.beans.BeanProperty

class AccessTokenReq {

  @NotNull
  @BeanProperty
  var appId: String = _

  @NotNull
  @BeanProperty
  var secret: String = _

}