package com.tairanchina.csp.dew.auth.domain

import java.util.Date
import javax.persistence._

import org.hibernate.annotations.{CreationTimestamp, UpdateTimestamp}

import scala.beans.BeanProperty

@MappedSuperclass
abstract class SafeEntity {

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column
  @BeanProperty
  var createTime: Date = _

  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column
  @BeanProperty
  var updateTime: Date = _

  @Column(nullable = false)
  @BeanProperty
  var createUser: String = ""

  @Column(nullable = false)
  @BeanProperty
  var updateUser: String = ""

}