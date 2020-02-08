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

package ms.dew.auth.domain

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
