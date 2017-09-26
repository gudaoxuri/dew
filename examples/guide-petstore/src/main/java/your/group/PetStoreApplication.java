package your.group;

import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

/**
 * 工程启动类
 * 重写父类的ComponentScan，先扫描Dew下的包，再是项目的根包
 */
@ComponentScan(basePackageClasses = {Dew.class, PetStoreApplication.class})
public class PetStoreApplication extends DewBootApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(PetStoreApplication.class).run(args);
    }

}
