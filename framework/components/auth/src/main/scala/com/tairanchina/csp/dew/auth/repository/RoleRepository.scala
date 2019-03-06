package com.tairanchina.csp.dew.auth.repository

import com.tairanchina.csp.dew.auth.domain.Role
import javax.annotation.Resource
import org.springframework.data.jpa.repository.{JpaRepository, Modifying, Query}

import scala.language.implicitConversions

@Resource
trait RoleRepository extends JpaRepository[Role, String] {

  @Modifying
  @Query(value = "UPDATE Role SET enabled = false WHERE id = ?1")
  def disable(id: String): Unit

  @Modifying
  @Query(value = "UPDATE Role SET enabled = true WHERE id = ?1")
  def enable(id: String): Unit

  @Query(value = "FROM Role WHERE enabled = true")
  def findByEnabled(): java.util.Set[Role]

}
