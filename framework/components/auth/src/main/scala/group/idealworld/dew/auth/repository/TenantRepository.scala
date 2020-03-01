/*
 * Copyright 2020. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.idealworld.dew.auth.repository

import group.idealworld.dew.auth.domain.Tenant
import javax.annotation.Resource
import group.idealworld.dew.auth.domain.Tenant
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
  def updateAccessToken(id: String, accessToken: String, accessTokenExpireMS: Long): Unit

  @Query(value = "FROM Tenant WHERE enabled = true")
  def findByEnabled(): java.util.Set[Tenant]

}
