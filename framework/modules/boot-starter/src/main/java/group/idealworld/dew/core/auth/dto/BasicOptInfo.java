package group.idealworld.dew.core.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 基础操作用户信息.
 *
 * @author gudaoxuri
 */
@Schema(title = "操作用户信息")
public class BasicOptInfo extends OptInfo {

    /**
     * The Mobile.
     */
    @Schema(title = "手机号", required = true)
    protected String mobile;
    /**
     * The Email.
     */
    @Schema(title = "邮箱", required = true)
    protected String email;
    /**
     * The Name.
     */
    @Schema(title = "姓名", required = true)
    protected String name;

    /**
     * The Last login time.
     */
    @Schema(title = "最后一次登录时间", required = true)
    protected LocalDateTime lastLoginTime;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}
