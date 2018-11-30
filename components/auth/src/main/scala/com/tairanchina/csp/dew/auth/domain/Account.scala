package com.tairanchina.csp.dew.auth.domain

import java.util

import com.ecfront.dew.common.$
import javax.persistence._

import scala.beans.BeanProperty

@Entity
@Table(name = "dew_auth_account", indexes = Array(
  new Index(name = "idx_account_tenantId", columnList = "tenantId"),
  new Index(name = "idx_account_enabled", columnList = "enabled")
))
class Account extends SafeStatusEntity {

  @Id
  @BeanProperty
  var id: String = _

  @Column(nullable = false)
  @BeanProperty
  var name: String = _

  @Column(nullable = false)
  @BeanProperty
  var password: String = _

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "dew_auth_rel_account_role",
    joinColumns = Array(new JoinColumn(name = "account_id", referencedColumnName = "id")),
    inverseJoinColumns = Array(new JoinColumn(name = "role_id", referencedColumnName = "id")))
  @BeanProperty
  var roles: java.util.Set[Role] = new util.HashSet[Role]()

  @Column(nullable = false)
  @BeanProperty
  var tenantId: String = _

}

object Account {

  def generatePassword(oriPassword: String, passwordSalt: String): String = {
    $.security.digest.digest(oriPassword + passwordSalt, "SHA-256")
  }
}