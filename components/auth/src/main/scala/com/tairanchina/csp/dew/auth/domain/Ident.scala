package com.tairanchina.csp.dew.auth.domain

import javax.persistence._

import scala.beans.BeanProperty

object Ident {

  val IDENT_CATEGORY_USERNAME = "USERNAME"
  val IDENT_CATEGORY_PHONE = "PHONE"
  val IDENT_CATEGORY_EMAIL = "EMAIL"
  val IDENT_CATEGORY_WECHAT = "WECHAT"

}

@Entity
@Table(name = "dew_auth_ident", indexes = Array(
  new Index(name = "idx_ident_accountId", columnList = "accountId"),
  new Index(name = "idx_ident_enabled", columnList = "enabled"),
  new Index(name = "uni_ident", columnList = "category,key,tenantId", unique = true),
))
class Ident extends SafeStatusEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: Int = _

  @Column(nullable = false)
  @BeanProperty
  var category: String = _

  @Column(nullable = false)
  @BeanProperty
  var key: String = _

  @Column(nullable = false)
  @BeanProperty
  var secret: String = _

  @Column(nullable = false)
  @BeanProperty
  var accountId: String = _

  @Column(nullable = false)
  @BeanProperty
  var tenantId: String = _

}