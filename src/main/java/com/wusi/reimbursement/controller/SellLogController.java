package com.wusi.reimbursement.controller;

import com.wusi.reimbursement.common.Response;
import com.wusi.reimbursement.entity.Reimbursement;
import com.wusi.reimbursement.entity.SellLog;
import com.wusi.reimbursement.query.SellLogQuery;
import com.wusi.reimbursement.service.SellLogService;
import com.wusi.reimbursement.utils.DataUtil;
import com.wusi.reimbursement.utils.DateUtil;
import com.wusi.reimbursement.vo.ReimbursementList;
import com.wusi.reimbursement.vo.SellLogList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @ Description   :  淘宝记账controller
 * @ Author        :  wusi
 * @ CreateDate    :  2020/1/16$ 17:07$
 */
@RestController
public class SellLogController {
    @Autowired
    private SellLogService sellLogService;
@RequestMapping("logList")
    public Response<Page<SellLogList>> logList(SellLogQuery query){
    if (DataUtil.isEmpty(query.getPage())) {
        query.setPage(0);
    }
    if (DataUtil.isEmpty(query.getLimit())) {
        query.setLimit(10);
    }
    Pageable pageable = PageRequest.of(query.getPage(), query.getLimit());
    Page<SellLog> page= sellLogService.queryPage(query,pageable);
    List<SellLogList> volist=new ArrayList<>();
    for (SellLog sellLog:page.getContent()){
        volist.add(getVo(sellLog));
    }
    Page<SellLogList> vopage=new PageImpl<>(volist, pageable, page.getTotalElements());
    return Response.ok(vopage);
}
    private SellLogList getVo(SellLog sellLog) {
        SellLogList sellLogList = new SellLogList();
        sellLogList.setId(sellLog.getId());
        sellLogList.setProduct(sellLog.getProduct());
        sellLogList.setBuyerName(sellLog.getBuyerName());
        sellLogList.setMyOrderNo(sellLog.getMyOrderNo());
        sellLogList.setSellMoney(sellLog.getSellMoney());
        sellLogList.setAmyOrderNo(sellLog.getAmyOrderNo());
        sellLogList.setBuyMoney(sellLog.getBuyMoney());
        sellLogList.setProfit(sellLog.getProfit());
        sellLogList.setRefund(sellLog.getRefund());
        sellLogList.setRemark(sellLog.getRemark());
        sellLogList.setOrderDate(DateUtil.formatDate(sellLog.getOrderDate(), DateUtil.PATTERN_YYYY_MM_DD));
        return sellLogList;
    }
}
