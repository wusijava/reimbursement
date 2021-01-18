package com.wusi.reimbursement.entity;

import com.wusi.reimbursement.common.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * @ Description   :  数学题记录
 * @ Author        :  wusi
 * @ CreateDate    :  2021/1/15$ 11:53$
 */
@Data
public class Math implements Identifiable<Long> {
    private Long id;
    private String content;
    private Date createTime;
    private String result;
    /**
     * 正确答案
     */
    private Integer rightResult;
    /**
     * 时间
     */
    private String time;

}