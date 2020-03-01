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

package group.idealworld.dew.auth

import java.util

import com.ecfront.dew.common.{Page, Resp}
import com.typesafe.scalalogging.LazyLogging
import group.idealworld.dew.auth.sdk.AuthSDKConfig
import group.idealworld.dew.auth.sdk.AuthSDKConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.{HttpEntity, HttpHeaders, HttpMethod}

/**
  * Basic test.
  *
  * @author gudaoxuri
  */
abstract class BasicTest extends LazyLogging {

  protected var accessToken: String = ""
  protected var token: String = ""
  @Autowired
  private var testRestTemplate: TestRestTemplate = _

  protected def post[E](uri: String, body: Any, clazz: Class[E]): Resp[E] = {
    Resp.generic(exchange(HttpMethod.POST, uri, body), clazz)
  }

  protected def postList[E](uri: String, body: Any, clazz: Class[E]): Resp[util.List[E]] = {
    Resp.genericList(exchange(HttpMethod.POST, uri, body), clazz)
  }

  protected def postPage[E](uri: String, body: Any, clazz: Class[E]): Resp[Page[E]] = {
    Resp.genericPage(exchange(HttpMethod.POST, uri, body), clazz)
  }

  protected def put[E](uri: String, body: Any, clazz: Class[E]): Resp[E] = {
    Resp.generic(exchange(HttpMethod.PUT, uri, body), clazz)
  }

  private def exchange[E](method: HttpMethod, uri: String, body: Any): Resp[_] = {
    val requestHeaders = new HttpHeaders
    requestHeaders.add(AuthSDKConfig.HTTP_ACCESS_TOKEN, accessToken)
    requestHeaders.add(AuthSDKConfig.HTTP_USER_TOKEN, token)
    val requestEntity = new HttpEntity[Any](body, requestHeaders)
    testRestTemplate.exchange(uri, method, requestEntity, classOf[Resp[_]]).getBody
  }

  protected def putList[E](uri: String, body: Any, clazz: Class[E]): Resp[util.List[E]] = {
    Resp.genericList(exchange(HttpMethod.PUT, uri, body), clazz)
  }

  protected def putPage[E](uri: String, body: Any, clazz: Class[E]): Resp[Page[E]] = {
    Resp.genericPage(exchange(HttpMethod.PUT, uri, body), clazz)
  }

  protected def get[E](uri: String, clazz: Class[E]): Resp[E] = {
    Resp.generic(exchange(HttpMethod.GET, uri, null), clazz)
  }

  protected def getList[E](uri: String, clazz: Class[E]): Resp[util.List[E]] = {
    Resp.genericList(exchange(HttpMethod.GET, uri, null), clazz)

  }

  protected def getPage[E](uri: String, clazz: Class[E]): Resp[Page[E]] = {
    Resp.genericPage(exchange(HttpMethod.GET, uri, null), clazz)
  }

  protected def delete(uri: String): Unit = {
    exchange(HttpMethod.DELETE, uri, null)
  }

}
