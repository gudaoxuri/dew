package com.tairanchina.csp.dew.auth.dto.management

import com.ecfront.dew.common.Page
import com.tairanchina.csp.dew.auth.domain.Ident
import com.tairanchina.csp.dew.auth.dto.common.SafeStatusDTO
import com.tairanchina.csp.dew.auth.helper.CommonConverter

import scala.beans.BeanProperty


case class IdentResp() extends SafeStatusDTO {

  @BeanProperty
  var id: Int = _
  @BeanProperty
  var category: String = _
  @BeanProperty
  var key: String = _
  @BeanProperty
  var secret: String = _
  @BeanProperty
  var accountId: String = _
  @BeanProperty
  var tenantId: String = _

}

object IdentResp {

  implicit def convert(ori: Ident): IdentResp = {
    CommonConverter.convert[IdentResp](ori)
  }

  implicit def convertPage(ori: org.springframework.data.domain.Page[Ident]): Page[IdentResp] = {
    CommonConverter.convertPage[IdentResp, Ident](ori)
  }

}





