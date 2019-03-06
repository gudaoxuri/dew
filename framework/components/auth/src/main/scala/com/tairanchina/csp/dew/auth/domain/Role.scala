package com.tairanchina.csp.dew.auth.domain

import java.util

import javax.persistence._

import scala.beans.BeanProperty

@Entity
@Table(name = "dew_auth_role", indexes = Array(
  new Index(name = "idx_role_enabled", columnList = "enabled")
))
class Role extends SafeStatusEntity {

  @Id
  @BeanProperty
  var id: String = _

  @Column(nullable = false, unique = true)
  @BeanProperty
  var name: String = _

  @Column(nullable = false)
  @BeanProperty
  var tenantId: String = _

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "dew_auth_rel_role_resource",
    joinColumns = Array(new JoinColumn(name = "role_id", referencedColumnName = "id")),
    inverseJoinColumns = Array(new JoinColumn(name = "resource_id", referencedColumnName = "id")))
  @BeanProperty
  var resources: java.util.Set[Resource] = new util.HashSet[Resource]()

}

object Role {

  def apply(id: String): Role = {
    val role = new Role()
    role.id = id
    role
  }

}