package com.tairanchina.csp.dew.auth.repository

import com.tairanchina.csp.dew.auth.domain.Account
import javax.annotation.Resource
import org.springframework.data.jpa.repository.{JpaRepository, Modifying, Query}

import scala.language.implicitConversions

@Resource
trait AccountRepository extends JpaRepository[Account, String] {

  @Modifying
  @Query(value = "UPDATE Account SET enabled = false WHERE id = ?1")
  def disable(id: String): Unit

  @Modifying
  @Query(value = "UPDATE Account SET enabled = true WHERE id = ?1")
  def enable(id: String): Unit

}
