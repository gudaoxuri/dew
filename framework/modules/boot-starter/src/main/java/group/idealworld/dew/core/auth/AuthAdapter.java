package group.idealworld.dew.core.auth;

import group.idealworld.dew.Dew;
import group.idealworld.dew.core.auth.dto.OptInfo;

import java.util.Optional;

/**
 * 登录鉴权适配器.
 *
 * @author gudaoxuri
 */
public interface AuthAdapter {

    /**
     * 获取当前登录的操作用户信息.
     *
     * @param <E> 扩展操作用户信息类型
     * @return 操作用户信息
     */
    default <E extends OptInfo> Optional<E> getOptInfo() {
        return Dew.context().optInfo();
    }

    /**
     * 根据Token获取操作用户信息.
     *
     * @param <E>   扩展操作用户信息类型
     * @param token 登录Token
     * @return 操作用户信息
     */
    <E extends OptInfo> Optional<E> getOptInfo(String token);

    /**
     * 设置操作用户信息类型.
     *
     * @param <E>     扩展操作用户信息类型
     * @param optInfo 扩展操作用户信息
     */
    <E extends OptInfo> void setOptInfo(E optInfo);

    /**
     * 删除当前登录的操作用户信息.
     * <p>
     * 对指注销登录
     */
    default void removeOptInfo() {
        getOptInfo().ifPresent(optInfo -> removeOptInfo(optInfo.getToken()));
    }

    /**
     * 根据Token删除操作用户信息.
     *
     * @param token 登录Token
     */
    void removeOptInfo(String token);
}
