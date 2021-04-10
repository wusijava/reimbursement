package com.wusi.reimbursement.vo;

import lombok.Data;

/**
 * @ Description   :  三个数混合运算
 * @ Author        :  wusi
 * @ CreateDate    :  2021/1/15$ 10:05$
 */
@Data
public class Math {
    private Integer numOne;
    private String  symbolOne;
    private Integer numTwo;
    private String  symbolTwo;
    private Integer numThree;
    private Integer result;
    private String source;
    private Long rowId;
    /**
     * 今日剩余任务量
     */
    private String num;
}
