package com.wusi.reimbursement.mapper;

import com.wusi.reimbursement.base.dao.mybatis.BaseMapper;
import com.wusi.reimbursement.entity.SystemLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author admin
 * @date 2020-05-29 11:35:50
 **/
@Mapper
public interface SystemLogMapper extends BaseMapper<SystemLog,Long> {


}
