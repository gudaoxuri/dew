package group.idealworld.dew.core.basic.utils.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ObjectUtils;

import java.time.Instant;

/**
 * Instant convert.
 *
 * @author gudaoxuri
 */
public class InstantConvert implements Converter<String, Instant> {
    @Override
    public Instant convert(String str) {
        if (ObjectUtils.isEmpty(str)) {
            return null;
        }
        return Instant.ofEpochMilli(Long.valueOf(str));
    }
}
