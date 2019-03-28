/*
 * Copyright 2019. the original author or authors.
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

package ms.dew.auth.repository

import javax.annotation.Resource
import ms.dew.auth.domain.Role
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
