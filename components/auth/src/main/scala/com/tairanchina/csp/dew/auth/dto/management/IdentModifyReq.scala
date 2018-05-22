package com.tairanchina.csp.dew.auth.dto.management

import javax.validation.constraints.NotNull

import com.ecfront.dew.common.$
import com.tairanchina.csp.dew.auth.domain.Ident
import com.tairanchina.csp.dew.auth.dto.common.StatusDTO
import com.tairanchina.csp.dew.auth.helper.CommonConverter

import scala.beans.BeanProperty


case class IdentModifyReq() extends StatusDTO {

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


object IdentModifyReq {

  def convert(ori: IdentModifyReq, dest: Ident): Ident = {
    val id = dest.id
    val tmp = CommonConverter.convert[Ident](ori)
    $.bean.copyProperties(dest, tmp)
    dest.id = id
    dest
  }

}





