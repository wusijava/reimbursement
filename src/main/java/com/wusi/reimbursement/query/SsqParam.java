package com.wusi.reimbursement.query;

import com.wusi.reimbursement.entity.Ssq;
import com.wusi.reimbursement.vo.SsqVo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SsqParam {
    private List<SsqVo> ssqList;
    private String term;
    private long totalPage;
}