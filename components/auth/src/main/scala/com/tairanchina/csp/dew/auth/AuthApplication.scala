package com.tairanchina.csp.dew.auth

import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cloud.client.SpringCloudApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringCloudApplication
@EnableTransactionManagement
class AuthApplication {

}

object AuthApplication extends App {

  new SpringApplicationBuilder(classOf[AuthApplication]).web(true).run(args: _*)

}
