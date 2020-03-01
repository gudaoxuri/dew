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

package group.idealworld.dew.auth.domain

import javax.persistence._

import scala.beans.BeanProperty

object Ident {

  val IDENT_CATEGORY_USERNAME = "USERNAME"
  val IDENT_CATEGORY_PHONE = "PHONE"
  val IDENT_CATEGORY_EMAIL = "EMAIL"
  val IDENT_CATEGORY_WECHAT = "WECHAT"

}

@Entity
@Table(name = "dew_auth_ident", indexes = Array(
  new Index(name = "idx_ident_accountId", columnList = "accountId"),
  new Index(name = "idx_ident_enabled", columnList = "enabled"),
  new Index(name = "uni_ident", columnList = "category,key,tenantId", unique = true),
))
class Ident extends SafeStatusEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: Int = _

  @Column(nullable = false)
  @BeanProperty
  var category: String = _

  @Column(nullable = false)
  @BeanProperty
  var key: String = _

  @Column(nullable = false)
  @BeanProperty
  var secret: String = _

  @Column(nullable = false)
  @BeanProperty
  var accountId: String = _

  @Column(nullable = false)
  @BeanProperty
  var tenantId: String = _

}
