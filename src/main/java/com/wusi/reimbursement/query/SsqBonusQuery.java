package com.wusi.reimbursement.query;

import com.wusi.reimbursement.entity.SsqBonus;
import lombok.Data;

/**
 * @ Description   :  开奖查询类
 * @ Author        :  wusi
 * @ CreateDate    :  2020/1/16$ 17:14$
 */
@Data
public class SsqBonusQuery extends SsqBonus {
    private Integer page;

    private Integer limit;

    private Integer offset;

    private String startTime;

    private String endTime;
    private Integer type;
}
