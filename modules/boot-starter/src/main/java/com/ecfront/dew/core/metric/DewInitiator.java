package com.ecfront.dew.core.metric;

import com.ecfront.dew.Dew;
import com.ecfront.dew.core.DewConfig;
import com.ecfront.dew.Dew;
import com.ecfront.dew.core.DewConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;

@Component
@ConditionalOnBean(DewFilter.class)
public class DewInitiator {

    @Autowired
    private DewConfig dewConfig;

    @Autowired
    private DewFilter dewFilter;

    @Bean
    public FilterRegistrationBean testFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(dewFilter);
        registration.addUrlPatterns("/*");
        registration.setName("dewFilter");
        registration.setOrder(1);
        return registration;
    }

    @PostConstruct
    public void init() {
        long standardTime = Instant.now().minusSeconds(dewConfig.getMetric().getPeriodSec()).toEpochMilli();
        Dew.Timer.periodic(60, () -> {
            for (Map<Long, Integer> map : DewFilter.RECORD_MAP.values()) {
                Iterator<Map.Entry<Long, Integer>> iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Long, Integer> entry = iterator.next();
                    if (entry.getKey() < standardTime) {
                        iterator.remove();
                    } else {
                        break;
                    }
                }
            }
        });
    }
}
