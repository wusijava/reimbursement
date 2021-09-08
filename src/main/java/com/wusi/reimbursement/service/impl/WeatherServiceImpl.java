package com.wusi.reimbursement.service.impl;

import com.wusi.reimbursement.base.dao.mybatis.BaseMapper;
import com.wusi.reimbursement.base.service.impl.BaseMybatisServiceImpl;
import com.wusi.reimbursement.entity.Weather;
import com.wusi.reimbursement.mapper.WeatherMapper;
import com.wusi.reimbursement.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author admin
 * @date 2021-09-08 11:36:15
 **/
@Service
public class WeatherServiceImpl extends BaseMybatisServiceImpl<Weather,Long> implements WeatherService {

    @Autowired
    private WeatherMapper weatherMapper;


    @Override
    protected BaseMapper<Weather, Long> getBaseMapper() {
        return weatherMapper;
    }
}
