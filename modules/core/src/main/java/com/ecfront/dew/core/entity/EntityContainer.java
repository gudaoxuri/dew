package com.ecfront.dew.core.entity;

import com.ecfront.dew.common.BeanHelper;
import com.ecfront.dew.common.ClassScanHelper;
import com.ecfront.dew.common.FieldInfo;
import com.ecfront.dew.core.config.DewConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EntityContainer {

    private static final Map<String, String> CODE_FIELD_NAMES = new ConcurrentHashMap<>();

    @Autowired
    private DewConfig dewConfig;

    @PostConstruct
    private void init() {
        dewConfig.getEntity().getBasePackages().stream().parallel().forEach(s -> {
            try {
                ClassScanHelper.scan(s, new HashSet<Class<? extends Annotation>>() {{
                    add(Entity.class);
                }}, null).stream().forEach(clazz -> {
                    Map<String, FieldInfo> codeFieldInfo = BeanHelper.findFieldsInfo(
                            clazz, null, null, null, new HashSet<Class<? extends Annotation>>() {{
                                add(Code.class);
                            }});
                    if (!codeFieldInfo.isEmpty()) {
                        CODE_FIELD_NAMES.put(clazz.getName(), codeFieldInfo.keySet().toArray(new String[0])[0]);
                    } else {
                        CODE_FIELD_NAMES.put(clazz.getName(), null);
                    }
                });
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public static Optional<String> getCodeFieldNameByClazz(Class<?> clazz) {
        return Optional.ofNullable(CODE_FIELD_NAMES.get(clazz.getName()));
    }


}
