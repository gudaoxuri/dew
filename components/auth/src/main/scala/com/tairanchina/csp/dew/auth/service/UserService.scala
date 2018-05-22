package com.tairanchina.csp.dew.auth.service

import com.ecfront.dew.common.{$, Resp}
import com.tairanchina.csp.dew.Dew
import com.tairanchina.csp.dew.auth.AuthConfig
import com.tairanchina.csp.dew.auth.domain.{Account, Ident}
import com.tairanchina.csp.dew.auth.dto.user._
import com.tairanchina.csp.dew.auth.repository._
import com.tairanchina.csp.dew.auth.sdk.TokenInfo
import com.typesafe.scalalogging.LazyLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.{JavaMailSender, MimeMessageHelper}
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._


@Service
class UserService @Autowired()(
                                val identRepository: IdentRepository,
                                val accountRepository: AccountRepository,
                                val authConfig: AuthConfig,
                                var mailSender: JavaMailSender
                              ) extends LazyLogging {


  @Transactional(readOnly = true)
  def sendRegisterVC(req: SendVCReq): Resp[Void] = {
    if (identRepository.getByCategoryAndKeyAndTenantId(req.identCategory, req.identKey,
      Dew.auth.getOptInfo.get().asInstanceOf[TokenInfo].getTenantId) != null) {
      Resp.conflict(req.identKey + " 已存在")
    } else if (req.identCategory == Ident.IDENT_CATEGORY_EMAIL) {
      val mimeMessage = mailSender.createMimeMessage
      val message = new MimeMessageHelper(mimeMessage, true)
      message.setFrom(authConfig.emailAccount)
      message.setTo(req.identKey)
      message.setSubject(Dew.dewConfig.getBasic.getName + " 注册验证")
      message.setText("请输入验证码 <b>" + generateVC(AuthConfig.CACHE_REG_VC_EMAIL + req.identKey) + "</b> , 10分钟内有效")
      mailSender.send(mimeMessage)
      Resp.success(null)
    } else {
      Resp.badRequest("请求凭证类型错误")
    }
  }

  @Transactional(readOnly = true)
  def sendLoginVC(req: SendVCReq): Resp[Void] = {
    if (req.identCategory == Ident.IDENT_CATEGORY_EMAIL) {
      val mimeMessage = mailSender.createMimeMessage
      val message = new MimeMessageHelper(mimeMessage, true)
      message.setFrom(authConfig.emailAccount)
      message.setTo(req.identKey)
      message.setSubject(Dew.dewConfig.getBasic.getName + " 登录验证")
      message.setText("请输入验证码 <b>" + generateVC(AuthConfig.CACHE_LOGIN_VC + req.identKey) + "</b> , 10分钟内有效")
      mailSender.send(mimeMessage)
      Resp.success(null)
    } else {
      Resp.badRequest("请求凭证类型错误")
    }
  }

  @Transactional
  def register(req: RegisterReq): Resp[TokenInfo] = {
    val tenantId = Dew.auth.getOptInfo.get().asInstanceOf[TokenInfo].getTenantId
    if (req.identCategory == Ident.IDENT_CATEGORY_EMAIL) {
      if (Dew.cluster.cache.get(AuthConfig.CACHE_REG_VC_EMAIL + req.identKey) == req.identSecret) {
        doRegister(req.name, null, Ident.IDENT_CATEGORY_EMAIL, req.identKey, tenantId)
      } else {
        Resp.unAuthorized("验证码错误")
      }
    } else if (req.identCategory == Ident.IDENT_CATEGORY_USERNAME) {
      doRegister(req.name, req.identSecret, Ident.IDENT_CATEGORY_USERNAME, req.identKey, tenantId)
    } else {
      Resp.badRequest("注册类型[" + req.identCategory + "]不支持")
    }
  }

  @Transactional
  def login(req: LoginReq): Resp[TokenInfo] = {
    val tenantId = Dew.auth.getOptInfo.get().asInstanceOf[TokenInfo].getTenantId
    val loginKey = (req.identCategory + req.identKey + tenantId).hashCode + ""
    val errorTimes = Dew.cluster.cache.get(AuthConfig.CACHE_LOGIN_ERROR + loginKey)
    if (errorTimes != null && errorTimes.toInt > authConfig.maxLoginErrorTimes) {
      return Resp.locked("多次登录错误，请稍后重试")
    }
    val ident = identRepository.getByCategoryAndKeyAndTenantId(req.identCategory, req.identKey, tenantId)
    if (ident == null || !ident.enabled) {
      logger.warn(tenantId + " - " + req.identKey + " NOT exist OR disabled")
      loginError(loginKey)
    } else {
      val account = accountRepository.findOne(ident.accountId)
      if (account == null || !account.enabled) {
        logger.warn(tenantId + " - " + req.identKey + " related account NOT exist OR disabled")
        loginError(loginKey)
      } else {
        if (req.byVC) {
          if (req.identSecret != Dew.cluster.cache.get(AuthConfig.CACHE_LOGIN_VC + req.identKey)) {
            logger.warn(tenantId + " - " + req.identKey + " vc error")
            loginError(loginKey)
          } else {
            cacheLogin(loginKey, account)
          }
        } else {
          if (account.password != Account.generatePassword(req.identSecret, authConfig.passwordSalt)) {
            logger.warn(tenantId + " - " + req.identKey + " password error")
            loginError(loginKey)
          } else {
            cacheLogin(loginKey, account)
          }
        }
      }
    }
  }

  @Transactional
  def logout(): Resp[Void] = {
    Dew.cluster.cache.del(AuthConfig.CACHE_TOKEN + Dew.auth.getOptInfo.get().asInstanceOf[TokenInfo].getToken)
    Resp.success(null)
  }

  @Transactional(readOnly = true)
  def getCurrentUser: Resp[UserResp] = {
    val accountId = Dew.auth.getOptInfo.get().asInstanceOf[TokenInfo].getAccountCode.asInstanceOf[String]
    Resp.success(accountRepository.findOne(accountId))
  }

  @Transactional
  def modifyCurrentUser(req: UserModifyReq): Resp[Void] = {
    val accountId = Dew.auth.getOptInfo.get().asInstanceOf[TokenInfo].getAccountCode.asInstanceOf[String]
    val account = accountRepository.findOne(accountId)
    account.name = req.name
    if (req.password != null && req.password.nonEmpty) {
      account.password = Account.generatePassword(req.password, authConfig.passwordSalt)
    }
    accountRepository.save(account)
    logout()
    Resp.success(null)
  }

  private def doRegister(name: String, password: String, identCategory: String, identKey: String, tenantId: String): Resp[TokenInfo] = {
    if (identRepository.getByCategoryAndKeyAndTenantId(identCategory, identKey, tenantId) != null) {
      Resp.conflict(identKey + " 已存在")
    } else {
      var account = new Account
      account.id = $.field.createShortUUID()
      account.name = if (name != null) name else ""
      account.password = Account.generatePassword(
        if (password != null) password else $.field.createShortUUID(), authConfig.passwordSalt)
      account.tenantId = tenantId
      account.enabled = true
      account = accountRepository.save(account)
      val ident = new Ident
      ident.category = Ident.IDENT_CATEGORY_USERNAME
      ident.key = identKey
      ident.secret = ""
      ident.accountId = account.id
      ident.tenantId = tenantId
      ident.enabled = true
      identRepository.save(ident)
      cacheLogin(identKey, account)
    }
  }

  private def loginError(loginKey: String): Resp[TokenInfo] = {
    Dew.cluster.cache.incrBy(AuthConfig.CACHE_LOGIN_ERROR + loginKey, 1)
    Dew.cluster.cache.expire(AuthConfig.CACHE_LOGIN_ERROR + loginKey, authConfig.cleanErrorTimeMin * 60)
    Resp.unAuthorized("认证错误")
  }

  private def cacheLogin(loginKey: String, account: Account): Resp[TokenInfo] = {
    val tokenInfo = new TokenInfo
    tokenInfo.setAccountCode(account.id)
    tokenInfo.setName(account.name)
    tokenInfo.setTenantId(account.tenantId)
    tokenInfo.setRoles(account.roles.asScala.map(role => role.id -> role.name).toMap.asJava)
    tokenInfo.setToken($.field.createUUID())
    Dew.cluster.cache.del(AuthConfig.CACHE_LOGIN_ERROR + loginKey)
    Dew.cluster.cache.setex(AuthConfig.CACHE_TOKEN + tokenInfo.getToken, $.json.toJsonString(tokenInfo), authConfig.tokenExpireSeconds)
    Resp.success(tokenInfo)
  }

  private def generateVC(cacheKey: String): String = {
    val vc = ((Math.random() * 9 + 1) * 1000).toInt + ""
    Dew.cluster.cache.setex(cacheKey, vc, 600)
    vc
  }

}
