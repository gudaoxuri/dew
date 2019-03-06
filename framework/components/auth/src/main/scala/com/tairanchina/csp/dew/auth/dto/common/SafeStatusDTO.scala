package com.tairanchina.csp.dew.auth.dto.common

import scala.beans.BeanProperty

abstract class SafeStatusDTO extends SafeDTO {

  @BeanProperty
  var enabled: Boolean = _

}