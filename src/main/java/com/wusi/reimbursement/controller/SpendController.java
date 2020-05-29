package com.wusi.reimbursement.controller;

import com.alibaba.fastjson.JSONObject;
import com.wusi.reimbursement.aop.SysLog;
import com.wusi.reimbursement.common.Response;
import com.wusi.reimbursement.entity.ExcelDto;
import com.wusi.reimbursement.entity.Spend;
import com.wusi.reimbursement.query.SpendQuery;
import com.wusi.reimbursement.service.SpendService;
import com.wusi.reimbursement.utils.DataUtil;
import com.wusi.reimbursement.utils.DateUtil;
import com.wusi.reimbursement.utils.RedisUtil;
import com.wusi.reimbursement.vo.SpendList;
import com.wusi.reimbursement.vo.SpendVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ Description   :  消费类controller
 * @ Author        :  wusi
 * @ CreateDate    :  2020/1/21$ 15:46$
 */
@RestController
public class SpendController {
    @Autowired
    SpendService spendService;

    @Value("${excelDownloadUrl}")
    private  String excelDownloadUrl;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("spendList")
    @ResponseBody
    @SysLog("测试")
    public Response<Page<SpendList>> productList(SpendQuery query) {
            if (DataUtil.isEmpty(query.getPage())) {
                query.setPage(0);
            }
            if (DataUtil.isEmpty(query.getLimit())) {
                query.setLimit(18);
            }
            Pageable pageable = PageRequest.of(query.getPage(), query.getLimit());
            Page<Spend>  page= spendService.queryPage(query,pageable);
            //重新封装一个query 不带分页的query
            SpendQuery newQuery=new SpendQuery();
            newQuery.setItem(query.getItem());
            newQuery.setStartTime(query.getStartTime());
            newQuery.setEndTime(query.getEndTime());
            newQuery.setConsumer(query.getConsumer());
            List<Spend> List=spendService.queryList(newQuery);
            List<SpendList> volist=new ArrayList<>();
            for (Spend spend:page.getContent()){
                volist.add(getVo(spend));
            }
        BigDecimal total=new BigDecimal("0.00");
        BigDecimal personTotal=new BigDecimal("0.00");
        for(Spend spend:List){
            BigDecimal bigDecimal=new BigDecimal(spend.getPrice());
            total=bigDecimal.add(total);
        }
        for(SpendList spendlist:volist){
            BigDecimal bigDecimal=new BigDecimal(spendlist.getPrice());
            personTotal=bigDecimal.add(personTotal);
        }
        for(SpendList spendList:volist){
            spendList.setTotal(total.toString());
            spendList.setPersonTotal(personTotal.toString());
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
        spendList.setUrl(spend.getUrl());
        return spendList;
    }
    @RequestMapping(value = "/spendDetail", method = RequestMethod.POST)
    @ResponseBody
    @SysLog
    public Response<SpendList> todetails(SpendQuery query) {
        Spend Spend=spendService.queryOne(query);
        return Response.ok(getVo(Spend));
    }
    @RequestMapping(value = "/spendUpdate", method = RequestMethod.POST)
    @ResponseBody
    @SysLog
    public Response<String> update(SpendList query) throws ParseException {
        System.out.println(query);
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
        spend.setUrl(spendList.getUrl());
        return  spend;
    }
    @RequestMapping(value = "/spendDel", method = RequestMethod.POST)
    @ResponseBody
    @SysLog
    public Response<String> del(SpendList query) {
        try {
            spendService.delById(query.getId());
            return Response.ok("删除SUCCESS!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.fail("删除失败!!!");
    }
    //saveSpend
    @RequestMapping(value = "/saveSpend", method = RequestMethod.POST)
    @ResponseBody
    @SysLog
    public Response<String> save(SpendList spendList) throws ParseException {

        System.out.println(spendList.getUrl());
        Spend spend=getSpend(spendList);
      spendService.insert(spend);
        return Response.ok("");
    }
    @RequestMapping(value = "out", method = RequestMethod.POST)
    @ResponseBody
    @SysLog
    public Response<String> batchExport(SpendQuery query) {
        List<Spend> spendList = spendService.queryList(query);
        List<SpendVo> voList = new ArrayList<>();
        int index = 1;
        for (Spend spend : spendList) {
            SpendVo spendVo = getSpendVo(spend);
            spendVo.setIndex(index);
            voList.add(spendVo);
            index++;
        }
        ExcelDto dto = new ExcelDto();
        dto.setHeaders(SpendVo.headers);
        dto.setKeys(SpendVo.keys);
        dto.setObjectList(parser(voList));
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        RedisUtil.set(key, dto, 1000 * 60 * 30L);
        String url = excelDownloadUrl + key;
        return Response.ok(url);
    }
    private List<JSONObject> parser(List<SpendVo> spendVo) {
        return JSONObject.parseArray(JSONObject.toJSONString(spendVo), JSONObject.class);
    }
    private SpendVo getSpendVo(Spend spend) {
        SpendVo spendVo = new SpendVo();
       // reimbursementList.setId(reimbursement.getId());
        spendVo.setId(spend.getId());
        spendVo.setItem(spend.getItem());
        spendVo.setPrice(spend.getPrice());
        spendVo.setConsumer(spend.getConsumer());
        spendVo.setDate(DateUtil.formatDate(spend.getDate(), DateUtil.PATTERN_YYYY_MM_DD));
        spendVo.setRemark(spend.getRemark());
        return spendVo;
    }
    //统计本月消费金额
    @RequestMapping(value = "/spendMonth", method = RequestMethod.POST)
    @ResponseBody
    @SysLog
    public Response spendMonth() {
        Date d = null;
        DateFormat sdf = new SimpleDateFormat(DateUtil.PATTERN_YYYY_MM_DD);
        d = new Date();
         String date = sdf.format(d);
         //当月消费
        String sql="select sum(price)  as s from spend where date_format(date,'%Y-%m')=date_format(now(),'%Y-%m');";
        List<Map<String, Object>> map = jdbcTemplate.queryForList(sql);
        //本年消费
        String sqlYear="select sum(price)  as y from spend where date_format(date,'%Y')=date_format(now(),'%Y');";
        List<Map<String, Object>> mapYear = jdbcTemplate.queryForList(sqlYear);
        Object sum=map.get(0).getOrDefault("s", 0);
        Object year=mapYear.get(0).getOrDefault("y", 0);
        Spend spend=new Spend();
        spend.setPrice(sum.toString());
        spend.setItem(year.toString());
        return Response.ok(spend);
    }
}
