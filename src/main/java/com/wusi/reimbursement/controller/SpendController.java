package com.wusi.reimbursement.controller;

import com.wusi.reimbursement.common.Response;
import com.wusi.reimbursement.entity.Reimbursement;
import com.wusi.reimbursement.entity.Spend;
import com.wusi.reimbursement.query.ReimbursementQuery;
import com.wusi.reimbursement.query.SpendQuery;
import com.wusi.reimbursement.service.SpendService;
import com.wusi.reimbursement.utils.DataUtil;
import com.wusi.reimbursement.utils.DateUtil;
import com.wusi.reimbursement.vo.ReimbursementList;
import com.wusi.reimbursement.vo.SellLogList;
import com.wusi.reimbursement.vo.SpendList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ Description   :  消费类controller
 * @ Author        :  wusi
 * @ CreateDate    :  2020/1/21$ 15:46$
 */
@RestController
public class SpendController {
    @Autowired
    SpendService spendService;
    @RequestMapping("spendList")
    @ResponseBody
    public Response<Page<SpendList>> productList(SpendQuery query) {
        System.out.println(query);
        if (DataUtil.isEmpty(query.getPage())) {
            query.setPage(0);
        }
        if (DataUtil.isEmpty(query.getLimit())) {
            query.setLimit(10);
        }
        Pageable pageable = PageRequest.of(query.getPage(), query.getLimit());
        Page<Spend>  page= spendService.queryPage(query,pageable);
        List<SpendList> volist=new ArrayList<>();
        for (Spend spend:page.getContent()){
            volist.add(getVo(spend));
        }
        Page<SpendList> vopage=new PageImpl<>(volist, pageable, page.getTotalElements());
        return Response.ok(vopage);
    }
    private SpendList getVo(Spend spend) {
        SpendList spendList=new SpendList();
        spendList.setId(spend.getId());
        spendList.setItem(spend.getItem());
        spendList.setPrice(spend.getPrice());
        spendList.setConsumer(spend.getConsumer());
        spendList.setDate(DateUtil.formatDate(spend.getDate(), DateUtil.PATTERN_YYYY_MM_DD));
        spendList.setRemark(spend.getRemark());
        return spendList;
    }
    @RequestMapping(value = "/spendDetail", method = RequestMethod.POST)
    @ResponseBody
    public Response<SpendList> todetails(SpendQuery query) {
        Spend Spend=spendService.queryOne(query);
        return Response.ok(getVo(Spend));
    }
    @RequestMapping(value = "/spendUpdate", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> update(SpendList query) throws ParseException {
        Spend spend=getSpend(query);
        spendService.updateById(spend);
        return Response.ok("ok");
    }

    private Spend getSpend(SpendList spendList) throws ParseException {
        Spend spend = new Spend();
        spend.setId(spendList.getId());
        spend.setItem(spendList.getItem());
        spend.setPrice(spendList.getPrice());
        spend.setConsumer(spendList.getConsumer());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date buydate=simpleDateFormat.parse(spendList.getDate());
        spend.setDate(buydate);
        spend.setRemark(spendList.getRemark());
        return  spend;
    }
    @RequestMapping(value = "/spendDel", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> del(SpendList query) {
        spendService.delById(query.getId());
        return Response.ok("");
    }
    //saveSpend
    @RequestMapping(value = "/saveSpend", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> save(SpendList spendList) throws ParseException {
        System.out.println(spendList.getDate());
        Spend spend=getSpend(spendList);
      spendService.insert(spend);
        return Response.ok("");
    }
}
