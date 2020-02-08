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

package ms.dew.auth.controller

import com.ecfront.dew.common.{Page, Resp}
import io.swagger.annotations.Api
import ms.dew.auth.dto.management._
import ms.dew.auth.service.ManagementService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation._

/**
  * Management controller.
  *
  * @param managementService management service
  * @author gudaoxuri
  */
@RestController
@Api(value = "后台管理")
@RequestMapping(value = Array("/management"))
@Validated
class ManagementController @Autowired()(
                                         val managementService: ManagementService
                                       ) {

  @GetMapping(Array("/publish"))
  def publish(): Resp[Void] = {
    managementService.publish()
  }

  //================================= Tenant =================================
  @PostMapping(Array("/tenant"))
  def addTenant(@Validated @RequestBody req: TenantAddReq): Resp[TenantResp] = {
    managementService.addTenant(req)
  }

  @PutMapping(Array("/tenant/{id}"))
  def modifyTenant(@PathVariable("id") id: String, @Validated @RequestBody req: TenantModifyReq): Resp[Void] = {
    managementService.modifyTenant(id, req)
  }

  @GetMapping(Array("/tenant/{id}"))
  def getTenant(@PathVariable("id") id: String): Resp[TenantResp] = {
    managementService.getTenant(id)
  }

  @GetMapping(Array("/tenants/{pageNumber}"))
  def pagingTenants(@PathVariable("pageNumber") pageNumber: Int,
                    @RequestParam(value = "pageSize", required = false, defaultValue = "10") pageSize: Int): Resp[Page[TenantResp]] = {
    managementService.pagingTenants(pageNumber, pageSize)
  }

  @DeleteMapping(Array("/tenant/{id}"))
  def disableTenant(@PathVariable("id") id: String): Resp[Void] = {
    managementService.disableTenant(id)
  }

  @PutMapping(Array("/tenant/{id}/enable"))
  def enableTenant(@PathVariable("id") id: String): Resp[Void] = {
    managementService.enableTenant(id)
  }

  //================================= Resource =================================
  @PostMapping(Array("/resource"))
  def addResource(@Validated @RequestBody req: ResourceAddReq): Resp[ResourceResp] = {
    managementService.addResource(req)
  }

  @PutMapping(Array("/resource/{id}"))
  def modifyResource(@PathVariable("id") id: Int, @Validated @RequestBody req: ResourceModifyReq): Resp[Void] = {
    managementService.modifyResource(id, req)
  }

  @GetMapping(Array("/resource/{id}"))
  def getResource(@PathVariable("id") id: Int): Resp[ResourceResp] = {
    managementService.getResource(id)
  }

  @GetMapping(Array("/resources/{pageNumber}"))
  def pagingResources(@PathVariable("pageNumber") pageNumber: Int,
                      @RequestParam(value = "pageSize", required = false, defaultValue = "10") pageSize: Int): Resp[Page[ResourceResp]] = {
    managementService.pagingResources(pageNumber, pageSize)
  }

  //================================= Role =================================
  @PostMapping(Array("/role"))
  def addRole(@Validated @RequestBody req: RoleAddReq): Resp[RoleResp] = {
    managementService.addRole(req)
  }

  @PutMapping(Array("/role/{id}"))
  def modifyRole(@PathVariable("id") id: String, @Validated @RequestBody req: RoleModifyReq): Resp[Void] = {
    managementService.modifyRole(id, req)
  }

  @GetMapping(Array("/role/{id}"))
  def getRole(@PathVariable("id") id: String): Resp[RoleResp] = {
    managementService.getRole(id)
  }

  @GetMapping(Array("/roles/{pageNumber}"))
  def pagingRoles(@PathVariable("pageNumber") pageNumber: Int,
                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") pageSize: Int): Resp[Page[RoleResp]] = {
    managementService.pagingRoles(pageNumber, pageSize)
  }

  @DeleteMapping(Array("/role/{id}"))
  def disableRole(@PathVariable("id") id: String): Resp[Void] = {
    managementService.disableRole(id)
  }

  @PutMapping(Array("/role/{id}/enable"))
  def enableRole(@PathVariable("id") id: String): Resp[Void] = {
    managementService.enableRole(id)
  }

  //================================= Account =================================
  @PostMapping(Array("/account"))
  def addAccount(@Validated @RequestBody req: AccountAddReq): Resp[AccountResp] = {
    managementService.addAccount(req)
  }

  @PutMapping(Array("/account/{id}"))
  def modifyAccount(@PathVariable("id") id: String, @Validated @RequestBody req: AccountModifyReq): Resp[Void] = {
    managementService.modifyAccount(id, req)
  }

  @GetMapping(Array("/account/{id}"))
  def getAccount(@PathVariable("id") id: String): Resp[AccountResp] = {
    managementService.getAccount(id)
  }

  @GetMapping(Array("/accounts/{pageNumber}"))
  def pagingAccounts(@PathVariable("pageNumber") pageNumber: Int,
                     @RequestParam(value = "pageSize", required = false, defaultValue = "10") pageSize: Int): Resp[Page[AccountResp]] = {
    managementService.pagingAccounts(pageNumber, pageSize)
  }

  @DeleteMapping(Array("/account/{id}"))
  def disableAccount(@PathVariable("id") id: String): Resp[Void] = {
    managementService.disableAccount(id)
  }

  @PutMapping(Array("/account/{id}/enable"))
  def enableAccount(@PathVariable("id") id: String): Resp[Void] = {
    managementService.enableAccount(id)
  }

  //================================= Ident =================================
  @PostMapping(Array("/ident"))
  def addIdent(@Validated @RequestBody req: IdentAddReq): Resp[IdentResp] = {
    managementService.addIdent(req)
  }

  @PutMapping(Array("/ident/{id}"))
  def modifyIdent(@PathVariable("id") id: Int, @Validated @RequestBody req: IdentModifyReq): Resp[Void] = {
    managementService.modifyIdent(id, req)
  }

  @GetMapping(Array("/ident/{id}"))
  def getRole(@PathVariable("id") id: Int): Resp[IdentResp] = {
    managementService.getIdent(id)
  }

  @GetMapping(Array("/idents/{pageNumber}"))
  def pagingIdents(@PathVariable("pageNumber") pageNumber: Int,
                   @RequestParam(value = "pageSize", required = false, defaultValue = "10") pageSize: Int): Resp[Page[IdentResp]] = {
    managementService.pagingIdents(pageNumber, pageSize)
  }

  @DeleteMapping(Array("/ident/{id}"))
  def disableIdent(@PathVariable("id") id: Int): Resp[Void] = {
    managementService.disableIdent(id)
  }

  @PutMapping(Array("/ident/{id}/enable"))
  def enableIdent(@PathVariable("id") id: Int): Resp[Void] = {
    managementService.enableIdent(id)
  }

}
