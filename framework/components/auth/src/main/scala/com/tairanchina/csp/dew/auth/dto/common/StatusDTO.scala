package com.tairanchina.csp.dew.auth.dto.common

import javax.validation.constraints.NotNull

import scala.beans.BeanProperty

abstract class StatusDTO {

  @BeanProperty
  @NotNull()
  var enabled: Boolean = true

}