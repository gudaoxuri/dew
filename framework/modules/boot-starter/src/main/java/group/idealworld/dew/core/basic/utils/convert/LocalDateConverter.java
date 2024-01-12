package group.idealworld.dew.core.basic.utils.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Local date converter.
 *
 * @author gudaoxuri
 */
public class LocalDateConverter implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(String str) {
        if (ObjectUtils.isEmpty(str)) {
            return null;
        }
        return LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}
