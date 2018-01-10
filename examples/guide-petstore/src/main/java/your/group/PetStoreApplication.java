package your.group;

import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 工程启动类
 */
@DewBootApplication(scanBasePackageClasses = {PetStoreApplication.class,Dew.class})
public class PetStoreApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(PetStoreApplication.class).run(args);
    }

}
