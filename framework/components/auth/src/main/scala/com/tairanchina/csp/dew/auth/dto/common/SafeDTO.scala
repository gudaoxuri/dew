package com.tairanchina.csp.dew.auth.dto.common

import java.util.Date

import scala.beans.BeanProperty

abstract class SafeDTO {

  @BeanProperty
  var createTime: Date = _
  @BeanProperty
  var updateTime: Date = _
  @BeanProperty
  var createUser: String = _
  @BeanProperty
  var updateUser: String = _

}