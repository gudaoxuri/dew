package com.tairanchina.csp.dew.auth.dto.basic

import scala.beans.BeanProperty

class AuthCacheData {

  // accessToken -> tenantId
  @BeanProperty
  var tenants: scala.collection.mutable.Map[String, String] = _

  // tenantId -> resourceUri -> roleIds
  @BeanProperty
  var resources: Map[String,Map[String, Set[String]]] = _

}