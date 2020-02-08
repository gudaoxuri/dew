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

import com.ecfront.dew.common.$
import javax.persistence._

import scala.beans.BeanProperty

@Entity
@Table(name = "dew_auth_account", indexes = Array(
  new Index(name = "idx_account_tenantId", columnList = "tenantId"),
  new Index(name = "idx_account_enabled", columnList = "enabled")
))
class Account extends SafeStatusEntity {

  @Id
  @BeanProperty
  var id: String = _

  @Column(nullable = false)
  @BeanProperty
  var name: String = _

  @Column(nullable = false)
  @BeanProperty
  var password: String = _

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "dew_auth_rel_account_role",
    joinColumns = Array(new JoinColumn(name = "account_id", referencedColumnName = "id")),
    inverseJoinColumns = Array(new JoinColumn(name = "role_id", referencedColumnName = "id")))
  @BeanProperty
  var roles: java.util.Set[Role] = new util.HashSet[Role]()

  @Column(nullable = false)
  @BeanProperty
  var tenantId: String = _

}

object Account {

  def generatePassword(oriPassword: String, passwordSalt: String): String = {
    $.security.digest.digest(oriPassword + passwordSalt, "SHA-256")
  }
}
