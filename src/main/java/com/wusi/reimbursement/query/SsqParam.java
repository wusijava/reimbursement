package com.wusi.reimbursement.query;

import com.wusi.reimbursement.entity.Ssq;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SsqParam {
    private List<Ssq> ssqList;
    private String term;
    private long totalPage;
}
