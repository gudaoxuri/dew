package com.tairanchina.csp.dew.auth.domain

import javax.persistence.{Column, MappedSuperclass}

import scala.beans.BeanProperty

@MappedSuperclass
abstract class StatusEntity {

  @Column(nullable = false)
  @BeanProperty
  var enabled: Boolean = true

}