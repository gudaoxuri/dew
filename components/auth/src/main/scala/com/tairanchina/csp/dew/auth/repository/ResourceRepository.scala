package com.tairanchina.csp.dew.auth.repository


import javax.annotation.Resource

import org.springframework.data.jpa.repository.JpaRepository

import scala.language.implicitConversions

@Resource
trait ResourceRepository extends JpaRepository[com.tairanchina.csp.dew.auth.domain.Resource, Integer] {


}
