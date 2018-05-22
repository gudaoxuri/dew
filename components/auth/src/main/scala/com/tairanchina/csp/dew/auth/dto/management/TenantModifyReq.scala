package com.tairanchina.csp.dew.auth.dto.management

import javax.validation.constraints.NotNull

import com.ecfront.dew.common.$
import com.tairanchina.csp.dew.auth.domain.Tenant
import com.tairanchina.csp.dew.auth.dto.common.StatusDTO
import com.tairanchina.csp.dew.auth.helper.CommonConverter

import scala.beans.BeanProperty

class TenantModifyReq extends StatusDTO {

  @BeanProperty
  @NotNull()
  var name: String = _

}

object TenantModifyReq {

  def convert(ori: TenantModifyReq, dest: Tenant): Tenant = {
    val id = dest.id
    val tmp = CommonConverter.convert[Tenant](ori)
    $.bean.copyProperties(dest, tmp)
    dest.id = id
    dest
  }

}