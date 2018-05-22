package com.tairanchina.csp.dew.auth.dto.basic

import scala.beans.BeanProperty

class AccessTokenResp {

  @BeanProperty
  var accessToken: String = _
  @BeanProperty
  var serverCurrentTimestamp: Long = _
  @BeanProperty
  var expireTimestamp: Long = _


}

object AccessTokenResp {

  def apply(accessToken: String, expireTimestamp: Long): AccessTokenResp = {
    val resp = new AccessTokenResp()
    resp.accessToken = accessToken
    resp.serverCurrentTimestamp = System.currentTimeMillis()
    resp.expireTimestamp = expireTimestamp
    resp
  }

}