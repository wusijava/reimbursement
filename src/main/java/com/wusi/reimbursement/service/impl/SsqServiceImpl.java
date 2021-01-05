package com.wusi.reimbursement.service.impl;

import com.wusi.reimbursement.base.dao.mybatis.BaseMapper;
import com.wusi.reimbursement.base.service.impl.BaseMybatisServiceImpl;
import com.wusi.reimbursement.entity.Ssq;
import com.wusi.reimbursement.mapper.SsqMapper;
import com.wusi.reimbursement.service.SsqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author admin
 * @date 2021-01-05 11:42:29
 **/
@Service
public class SsqServiceImpl extends BaseMybatisServiceImpl<Ssq,Long> implements SsqService {

    @Autowired
    private SsqMapper ssqMapper;


    @Override
    protected BaseMapper<Ssq, Long> getBaseMapper() {
        return ssqMapper;
    }
}
