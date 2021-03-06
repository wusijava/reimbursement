package com.wusi.reimbursement.query;

import com.wusi.reimbursement.entity.Homework;
import com.wusi.reimbursement.entity.SellLog;
import lombok.Data;

/**
 * @ Description   :  作业query
 * @ Author        :  wusi
 * @ CreateDate    :  2020/1/16$ 17:14$
 */
@Data
public class HomeworkQuery extends Homework {
    private Integer page;

    private Integer limit;

    private Integer offset;

    private String startTime;

    private String endTime;
}
