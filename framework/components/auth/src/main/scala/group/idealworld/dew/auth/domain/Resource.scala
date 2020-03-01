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

@Entity
@Table(name = "dew_auth_resource", indexes = Array(
  new Index(name = "idx_resource_category", columnList = "category")
))
class Resource {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: Int = _

  @Column(nullable = false, unique = true)
  @BeanProperty
  var uri: String = _

  @Column(nullable = false)
  @BeanProperty
  var name: String = _

  @Column(nullable = false)
  @BeanProperty
  var category: String = _

  @Column(nullable = false)
  @BeanProperty
  var icon: String = _

  @Column(nullable = false)
  @BeanProperty
  var parentId: Int = _

  @Column(nullable = false)
  @BeanProperty
  var sort: Int = 0

  @Column(nullable = false)
  @BeanProperty
  var tenantId: String = _

}

object Resource {

  val CATEGORY_API = "API"
  val CATEGORY_MENU = "MENU"

  def apply(id: Int): Resource = {
    val res = new Resource()
    res.id = id
    res
  }
}
