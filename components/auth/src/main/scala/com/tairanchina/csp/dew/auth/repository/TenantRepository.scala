package com.tairanchina.csp.dew.auth.repository

import javax.annotation.Resource

import com.tairanchina.csp.dew.auth.domain.Tenant
import org.springframework.data.jpa.repository.{JpaRepository, Modifying, Query}

import scala.language.implicitConversions

@Resource
trait TenantRepository extends JpaRepository[Tenant, String] {

  @Modifying
  @Query(value = "UPDATE Tenant SET enabled = false WHERE id = ?1")
  def disable(id: String): Unit

  @Modifying
  @Query(value = "UPDATE Tenant SET enabled = true WHERE id = ?1")
  def enable(id: String): Unit

  @Modifying
  @Query(value = "UPDATE Tenant SET accessToken = ?2 , accessTokenExpireMS = ?3 WHERE id = ?1")
  def updateAccessToken(id: String,accessToken:String,accessTokenExpireMS:Long): Unit

  @Query(value = "FROM Tenant WHERE enabled = true")
  def findByEnabled():java.util.Set[Tenant]

}
