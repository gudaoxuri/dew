package group.idealworld.dew.core.basic.utils.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ObjectUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Local time converter.
 *
 * @author gudaoxuri
 */
public class LocalTimeConverter implements Converter<String, LocalTime> {

    @Override
    public LocalTime convert(String str) {
        if (ObjectUtils.isEmpty(str)) {
            return null;
        }
        return LocalTime.parse(str, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

}
