package group.idealworld.dew.devops.maven.mojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import static org.apache.maven.plugins.annotations.LifecyclePhase.VALIDATE;

/**
 * Init mojo.
 * <p>
 * NOTE: 此mojo不能单独调用，仅与 release 配合使用
 * <p>
 * 默认绑定到 validate phase，
 * 是为抢先初始化配置及执行 skip 操作（避免不必要的compile/jar/install/deploy等）
 *
 * @author gudaoxuri
 */
@Mojo(name = "init", defaultPhase = VALIDATE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class InitMojo extends BasicMojo {

    @Override
    protected boolean executeInternal() {
        return true;
    }

}
