package com.tairanchina.csp.dew.auth.domain

import javax.persistence._

import scala.beans.BeanProperty

@Entity
@Table(name = "dew_auth_tenant", indexes = Array(
  new Index(name = "idx_tenant_enabled", columnList = "enabled")
))
class Tenant extends SafeStatusEntity {

  @Id
  @BeanProperty
  var id: String = _

  @Column(nullable = false, unique = true)
  @BeanProperty
  var secret: String = _

  @Column(nullable = false, unique = true)
  @BeanProperty
  var name: String = _

  @Column(nullable = false)
  @BeanProperty
  var effectiveTimeMS: Long = 1000L * 60 * 60 * 24 * 7

  @Column(nullable = false)
  @BeanProperty
  var accessToken: String = ""

  @Column(nullable = false)
  @BeanProperty
  var accessTokenExpireMS: Long = 0

}
