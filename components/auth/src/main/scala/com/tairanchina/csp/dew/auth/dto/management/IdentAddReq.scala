package com.tairanchina.csp.dew.auth.dto.management

import javax.validation.constraints.NotNull

import com.tairanchina.csp.dew.auth.domain.Ident
import com.tairanchina.csp.dew.auth.dto.common.StatusDTO
import com.tairanchina.csp.dew.auth.helper.CommonConverter

import scala.beans.BeanProperty


case class IdentAddReq() extends StatusDTO {

  @NotNull
  @BeanProperty
  var category: String = _

  @NotNull
  @BeanProperty
  var key: String = _

  @NotNull
  @BeanProperty
  var secret: String = ""

  @NotNull
  @BeanProperty
  var accountId: String = _

  @NotNull
  @BeanProperty
  var tenantId: String = _

}

object IdentAddReq {

  implicit def convert(ori: IdentAddReq): Ident = {
    CommonConverter.convert[Ident](ori)
  }

}





