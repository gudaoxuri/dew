package com.ecfront.dew.core.entity;

import com.ecfront.dew.common.BeanHelper;
import com.ecfront.dew.common.ClassScanHelper;
import com.ecfront.dew.core.DewConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
@ConditionalOnClass({Entity.class})
public class EntityContainer {

    private static final Map<String, Optional<EntityClassInfo>> CODE_FIELD_NAMES = new ConcurrentHashMap<>();

    @Autowired
    private DewConfig dewConfig;

    @PostConstruct
    public void init() {
        if (dewConfig.getBasic().getEntity().getBasePackages().isEmpty()) {
            return;
        }
        dewConfig.getBasic().getEntity().getBasePackages().stream().parallel().forEach(s -> {
            try {
                ClassScanHelper.scan(s, new HashSet<Class<? extends Annotation>>() {{
                    add(Entity.class);
                }}, null).stream().forEach(clazz -> {
                    Map<String, BeanHelper.FieldInfo> codeFieldInfo = BeanHelper.findFieldsInfo(
                            clazz, null, null, null, new HashSet<Class<? extends Annotation>>() {{
                                add(Code.class);
                            }});
                    if (!codeFieldInfo.isEmpty()) {
                        BeanHelper.FieldInfo info = codeFieldInfo.values().toArray(new BeanHelper.FieldInfo[0])[0];
                        EntityClassInfo entityClassInfo = new EntityClassInfo();
                        entityClassInfo.codeFieldName = info.getName();
                        entityClassInfo.codeFieldUUID = ((Code) info.getAnnotations().stream().filter(i -> i.annotationType() == Code.class).findAny().get()).uuid();
                        CODE_FIELD_NAMES.put(clazz.getName(), Optional.of(entityClassInfo));
                    } else {
                        CODE_FIELD_NAMES.put(clazz.getName(), Optional.empty());
                    }
                });
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public static Optional<EntityClassInfo> getCodeFieldNameByClazz(Class<?> clazz) {
        return CODE_FIELD_NAMES.get(clazz.getName());
    }

    public static class EntityClassInfo {
        public String codeFieldName;
        public boolean codeFieldUUID;
    }

}
