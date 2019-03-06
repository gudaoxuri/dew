package com.tairanchina.csp.dew.auth.domain

import javax.persistence._

import scala.beans.BeanProperty

@Entity
@Table(name = "dew_auth_resource", indexes = Array(
  new Index(name = "idx_resource_category", columnList = "category")
))
class Resource {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: Int = _

  @Column(nullable = false, unique = true)
  @BeanProperty
  var uri: String = _

  @Column(nullable = false)
  @BeanProperty
  var name: String = _

  @Column(nullable = false)
  @BeanProperty
  var category: String = _

  @Column(nullable = false)
  @BeanProperty
  var icon: String = _

  @Column(nullable = false)
  @BeanProperty
  var parentId: Int = _

  @Column(nullable = false)
  @BeanProperty
  var sort: Int = 0

  @Column(nullable = false)
  @BeanProperty
  var tenantId: String = _

}

object Resource {

  val CATEGORY_API = "API"
  val CATEGORY_MENU = "MENU"

  def apply(id: Int): Resource = {
    val res = new Resource()
    res.id = id
    res
  }
}