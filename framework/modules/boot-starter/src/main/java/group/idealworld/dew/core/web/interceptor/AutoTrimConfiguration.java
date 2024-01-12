package group.idealworld.dew.core.web.interceptor;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

import java.io.IOException;

/**
 * 自动去掉请求中字符串类型的前后空格.
 *
 * @author gudaoxuri
 */
@Configuration
@ConditionalOnProperty(prefix = "dew.basic.format", name = "auto-trim-from-req", havingValue = "true")
public class AutoTrimConfiguration extends SimpleModule {

    /**
     * Instantiates a new Auto trim configuration.
     */
    public AutoTrimConfiguration() {
        addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
            @Override
            public String deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
                String value = jsonParser.getValueAsString();
                if (ObjectUtils.isEmpty(value)) {
                    return value;
                }
                return value.trim();
            }
        });
    }

}
