package com.tairanchina.csp.dew.auth.dto.management

import com.tairanchina.csp.dew.auth.domain.Tenant
import com.tairanchina.csp.dew.auth.dto.common.StatusDTO
import com.tairanchina.csp.dew.auth.helper.CommonConverter
import javax.validation.constraints.NotNull

import scala.beans.BeanProperty

class TenantAddReq extends StatusDTO {

  @BeanProperty
  @NotNull()
  var name: String = _

}

object TenantAddReq {

  implicit def convert(ori: TenantAddReq): Tenant = {
    CommonConverter.convert[Tenant](ori)
  }

}