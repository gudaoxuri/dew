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

import javax.persistence._

import scala.beans.BeanProperty

@Entity
@Table(name = "dew_auth_tenant", indexes = Array(
  new Index(name = "idx_tenant_enabled", columnList = "enabled")
))
class Tenant extends SafeStatusEntity {

  @Id
  @BeanProperty
  var id: String = _

  @Column(nullable = false, unique = true)
  @BeanProperty
  var secret: String = _

  @Column(nullable = false, unique = true)
  @BeanProperty
  var name: String = _

  @Column(nullable = false)
  @BeanProperty
  var effectiveTimeMS: Long = 1000L * 60 * 60 * 24 * 7

  @Column(nullable = false)
  @BeanProperty
  var accessToken: String = ""

  @Column(nullable = false)
  @BeanProperty
  var accessTokenExpireMS: Long = 0

}
