package com.ecfront.dew.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecfront.dew.feign.fallback.PerformanceServiceFallback;

/**
 * PerformanceServiceClient
 *
 * @author hzzjb
 * @date 2017/9/27
 */
@FeignClient(value = "performance-service", fallback = PerformanceServiceFallback.class)
public interface PerformanceServiceClient {

    @RequestMapping(value = "performance", method = RequestMethod.GET)
    String getAtOnce();

    @RequestMapping(value = "performance/delay/{time}", method = RequestMethod.GET)
    @ResponseBody
    String getWithDelay(@PathVariable("time") String delayTime);

    @RequestMapping(value = "performance", method = RequestMethod.POST)
    @ResponseBody
    String postAtOnce();

    @RequestMapping(value = "performance/delay", method = RequestMethod.POST)
    @ResponseBody
    String postWithDelay(String time);

    @RequestMapping(value = "performance/file", method = RequestMethod.GET)
    Integer doWithLargeTime();

    @RequestMapping(value = "performance/mix", method = RequestMethod.GET)
    Integer doMix();
}
