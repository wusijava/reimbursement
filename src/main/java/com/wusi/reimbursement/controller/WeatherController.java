package com.wusi.reimbursement.controller;

import com.alibaba.fastjson.JSONObject;
import com.wusi.reimbursement.utils.WeatherUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ Description   :  天气提醒
 * @ Author        :  wusi
 * @ CreateDate    :  2021/1/5$ 17:50$
 */
@RestController
@Slf4j
@RequestMapping(value = "api/open/")
public class WeatherController {
    @Scheduled(cron = "0 45 7 * * ?")
    @RequestMapping(value = "getWeather")
    public void sendWeatherSMS() throws Exception {
        String weather = WeatherUtils.getWeather();
        log.error("三日内天气,{}", JSONObject.toJSONString(weather));
    }
}
