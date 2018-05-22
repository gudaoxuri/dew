package com.tairanchina.csp.dew.auth.repository

import javax.annotation.Resource

import com.tairanchina.csp.dew.auth.domain.Ident
import org.springframework.data.jpa.repository.{JpaRepository, Modifying, Query}

import scala.language.implicitConversions

@Resource
trait IdentRepository extends JpaRepository[Ident, Integer] {

  @Modifying
  @Query(value = "UPDATE Ident SET enabled = false WHERE id = ?1")
  def disable(id: Int): Unit

  @Modifying
  @Query(value = "UPDATE Ident SET enabled = true WHERE id = ?1")
  def enable(id: Int): Unit

  def getByCategoryAndKeyAndTenantId(category: String, key: String, tenantId: String): Ident

}
