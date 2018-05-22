package com.tairanchina.csp.dew.auth

import com.ecfront.dew.common.Resp
import com.tairanchina.csp.dew.auth.service.BasicService
import io.swagger.annotations.Api
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation._

@RestController
@Api(value = "测试")
@RequestMapping(value = Array("/test"))
@Validated
class TestController @Autowired()(
                                   val basicService: BasicService
                                 ) {

  @GetMapping(Array(""))
  def test1(): Resp[Void] = {
    Resp.success(null)
  }

  @GetMapping(Array("/auth"))
  def test2(): Resp[Void] = {
    Resp.success(null)
  }

  @GetMapping(Array("/auth/t"))
  def test3(): Resp[Void] = {
    Resp.success(null)
  }

}
