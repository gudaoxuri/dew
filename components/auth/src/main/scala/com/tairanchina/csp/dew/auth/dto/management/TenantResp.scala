package com.tairanchina.csp.dew.auth.dto.management

import com.ecfront.dew.common.Page
import com.tairanchina.csp.dew.auth.domain.Tenant
import com.tairanchina.csp.dew.auth.dto.common.SafeStatusDTO
import com.tairanchina.csp.dew.auth.helper.CommonConverter

import scala.beans.BeanProperty

case class TenantResp() extends SafeStatusDTO {

  @BeanProperty
  var id: String = _
  @BeanProperty
  var secret: String = _
  @BeanProperty
  var name: String = _

}

object TenantResp {

  implicit def convert(ori: Tenant): TenantResp = {
    CommonConverter.convert[TenantResp](ori)
  }

  implicit def convertPage(ori: org.springframework.data.domain.Page[Tenant]): Page[TenantResp] = {
    CommonConverter.convertPage[TenantResp, Tenant](ori)
  }

}