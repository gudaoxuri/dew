package com.tairanchina.csp.dew.auth

import java.util

import com.ecfront.dew.common.{Page, Resp}
import com.tairanchina.csp.dew.auth.sdk.AuthSDKConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.{HttpEntity, HttpHeaders, HttpMethod}


abstract class BasicTest {

  @Autowired
  private var testRestTemplate: TestRestTemplate = _

  protected var accessToken: String = ""
  protected var token: String = ""

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

  private def exchange[E](method: HttpMethod, uri: String, body: Any): Resp[_] = {
    val requestHeaders = new HttpHeaders
    requestHeaders.add(AuthSDKConfig.HTTP_ACCESS_TOKEN, accessToken)
    requestHeaders.add(AuthSDKConfig.HTTP_USER_TOKEN, token)
    val requestEntity = new HttpEntity[Any](body, requestHeaders)
    testRestTemplate.exchange(uri, method, requestEntity, classOf[Resp[_]]).getBody
  }

}
