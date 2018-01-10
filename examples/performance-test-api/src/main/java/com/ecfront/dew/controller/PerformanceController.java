package com.ecfront.dew.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecfront.dew.feign.PerformanceServiceClient;

/**
 * PerformanceController
 *
 * @author hzzjb
 * @date 2017/9/27
 */
@RestController
public class PerformanceController {

    @Autowired
    private PerformanceServiceClient performanceServiceClient;

    @RequestMapping(value = "performance", method = RequestMethod.GET)
    public String getAtOnce() {
        return performanceServiceClient.getAtOnce();
    }

    @RequestMapping(value = "performance/delay/{time}", method = RequestMethod.GET)
    @ResponseBody
    public String getWithDelay(@PathVariable("time") String delayTime) {
        return performanceServiceClient.getWithDelay(delayTime);
    }

    @RequestMapping(value = "performance", method = RequestMethod.POST)
    @ResponseBody
    public String postAtOnce() {
        return performanceServiceClient.postAtOnce();
    }

    @RequestMapping(value = "performance/delay", method = RequestMethod.POST)
    @ResponseBody
    public String postWithDelay(String time) {
        return performanceServiceClient.postWithDelay(time);
    }

    @RequestMapping(value = "performance/file", method = RequestMethod.GET)
    public Integer doWithLargeTime() {
        return performanceServiceClient.doWithLargeTime();
    }

    @RequestMapping(value = "performance/mix", method = RequestMethod.GET)
    public Integer doMix() {
        return performanceServiceClient.doMix();
    }

}
