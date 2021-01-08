package com.wusi.reimbursement.controller;

import com.alibaba.fastjson.JSONObject;
import com.wusi.reimbursement.common.Response;
import com.wusi.reimbursement.entity.RequestContext;
import com.wusi.reimbursement.entity.Ssq;
import com.wusi.reimbursement.entity.SsqBonus;
import com.wusi.reimbursement.mapper.SsqMapper;
import com.wusi.reimbursement.query.SsqBonusQuery;
import com.wusi.reimbursement.query.SsqParam;
import com.wusi.reimbursement.query.SsqQuery;
import com.wusi.reimbursement.service.SsqBonusService;
import com.wusi.reimbursement.service.SsqService;
import com.wusi.reimbursement.utils.*;
import com.wusi.reimbursement.vo.SsqBonusVo;
import com.wusi.reimbursement.vo.SsqVo;
import com.wusi.reimbursement.vo.suiJi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ Description   :  菩萨保佑 让我走向人生巅峰
 * @ Author        :  wusi
 * @ CreateDate    :  2021/1/5$ 10:13$
 */
@RestController
@Slf4j
@RequestMapping(value = "api/ssq")
public class SsqController {
    @Autowired
    private SsqBonusService SsqBonusService;
    @Autowired
    private SsqService SsqService;
    @Autowired
    private SsqMapper SsqMapper;

    @RequestMapping(value = "getBonusNum")
    @ResponseBody
    @Scheduled(cron = "0 0 22 * * ?")
    public void getBonusNum() throws IOException, ParseException {
        SsqBonus ssq = SsqNumGuanWangUtils.getSsqNum();
        SsqBonus query = new SsqBonus();
        query.setTerm(ssq.getTerm());
        Long num = SsqBonusService.queryCount(query);
        if (num < 1) {
            SsqBonusService.insert(ssq);
            //去查找有误购买此期的购买记录
            Ssq buy=new Ssq();
            ssq.setTerm(buy.getTerm());
            List<Ssq> buyList = SsqService.queryList(buy);
            if(DataUtil.isNotEmpty(buyList)){
                for(Ssq buyOne:buyList){
                    if(!ssq.getTerm().equals(buyOne.getTerm())){
                        break;
                    }
                    int redNum=0;
                    int blueNum=0;
                    if(buyOne.getBlue().equals(ssq.getBlue())){
                        blueNum=1;
                    }
                    List<String> myNum=new ArrayList<>();
                    myNum.add(buyOne.getRed1());
                    myNum.add(buyOne.getRed2());
                    myNum.add(buyOne.getRed3());
                    myNum.add(buyOne.getRed4());
                    myNum.add(buyOne.getRed5());
                    myNum.add(buyOne.getRed6());
                    for(int i=0;i<6;i++){
                        for(int j=0;j<6;j++){
                            if(myNum.get(i).equals(1)){
                                redNum++;
                            }
                        }
                    }
                    buyOne.setRedNum(String.valueOf(redNum));
                    buyOne.setBlueNum(String.valueOf(blueNum));
                    if (redNum>=4||blueNum==1){
                        buyOne.setIsBonus("1");
                        if(blueNum==1&&redNum<3){
                            buyOne.setBonus("5");
                        }else if(blueNum==1&&redNum==3){
                            buyOne.setBonus("10");
                        }else if(blueNum==0&&redNum==4){
                            buyOne.setBonus("10");
                        }else if((blueNum==1&&redNum==4)||redNum==5){
                            buyOne.setBonus("200");
                        }else if(blueNum==1&&redNum==5){
                            buyOne.setBonus("3000");
                        }else if(redNum==6&&blueNum==0){
                            buyOne.setBonus("二等奖");
                        }else{
                            buyOne.setBonus("一等奖");
                        }
                    }else{
                        //未中奖
                        buyOne.setIsBonus("-1");
                        buyOne.setBonus("0");
                    }
                    buyOne.setWeek(ssq.getWeek());
                    buyOne.setBonusTime(ssq.getCreateTime());
                    SsqService.updateById(buyOne);
                }

            }else{
                log.error("本期未购买,{}", ssq.getTerm());
            }
        }
        log.error("获取开奖号码执行结束,{}", DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }



    @RequestMapping(value = "getSsqRecord")
    @ResponseBody
    public Response<Page<SsqBonusVo>> getSsqRecord(SsqBonusQuery query) {
        if (DataUtil.isEmpty(query.getPage())) {
            query.setPage(0);
        }
        query.setLimit(2);
        Pageable pageable = PageRequest.of(query.getPage(), query.getLimit());
        Page<SsqBonus> page = SsqBonusService.queryPage(query, pageable);
        List<SsqBonusVo> voList = new ArrayList<>();
        for (SsqBonus ssqBonus : page.getContent()) {
            voList.add(getSsqVo(ssqBonus));
        }
        Page<SsqBonusVo> voPage = new PageImpl<>(voList, pageable, page.getTotalElements());
        return Response.ok(voPage);
    }

    private SsqBonusVo getSsqVo(SsqBonus ssqBonus) {
        SsqBonusVo vo = new SsqBonusVo();
        vo.setId(ssqBonus.getId());
        vo.setTerm(ssqBonus.getTerm());
        vo.setRed1(ssqBonus.getRed1());
        vo.setRed2(ssqBonus.getRed2());
        vo.setRed3(ssqBonus.getRed3());
        vo.setRed4(ssqBonus.getRed4());
        vo.setRed5(ssqBonus.getRed5());
        vo.setRed6(ssqBonus.getRed6());
        vo.setBlue(ssqBonus.getBlue());
        vo.setCreateTime(DateUtil.formatDate(ssqBonus.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        vo.setWeek(ssqBonus.getWeek());
        //查找自己的购买记录
        Ssq ssq=new Ssq();
        ssq.setTerm(vo.getTerm());
        List<Ssq> ssqs = SsqService.queryList(ssq);
        if(DataUtil.isNotEmpty(ssqs)){
            List<SsqVo> list=new ArrayList<>();
            for(Ssq s:ssqs){
                SsqVo ssqVo=new SsqVo();
                ssqVo.setId(s.getId());
                ssqVo.setTerm(s.getTerm());
                ssqVo.setRed4(s.getRed4());
                ssqVo.setRed5(s.getRed5());
                ssqVo.setRed6(s.getRed6());
                ssqVo.setRed1(s.getRed1());
                ssqVo.setRed2(s.getRed2());
                ssqVo.setRed3(s.getRed3());
                ssqVo.setBlue(s.getBlue());
                ssqVo.setNum(s.getNum());
                ssqVo.setBlue(s.getBlue());
                ssqVo.setRedNum(s.getRedNum());
                ssqVo.setBlueNum(s.getBlueNum());
                ssqVo.setBonus(s.getBonus());
                list.add(ssqVo);
            }
            vo.setList(list);
        }else{
            vo.setList(null);
        }
        return vo;
    }
    @RequestMapping(value = "getSsqNum")
    @ResponseBody
    public Response<String> getSsqNum() throws ParseException {
        return Response.ok(WeekUtils.getSsqNum());
    }
    @RequestMapping(value = "addSsq")
    @ResponseBody
    public Response<String> addSsq(Ssq Ssq) {
        RequestContext.RequestUser loginUser = RequestContext.getCurrentUser();
        HashSet set=new HashSet();
        set.add(Ssq.getRed1());
        set.add(Ssq.getRed2());
        set.add(Ssq.getRed3());
        set.add(Ssq.getRed4());
        set.add(Ssq.getRed5());
        set.add(Ssq.getRed6());
        if(set.size()<6){
            return Response.fail("红球有重复!");
        }
        if(  (Integer.valueOf(Ssq.getRed1())>33||Integer.valueOf(Ssq.getRed1())<1)||Ssq.getRed1().length()!=2){
            return Response.fail("红球范围或格式错误错误!");
        }
        if(  (Integer.valueOf(Ssq.getRed2())>33||Integer.valueOf(Ssq.getRed2())<1)||Ssq.getRed1().length()!=2){
            return Response.fail("红球范围或格式错误错误!");
        }
        if(  (Integer.valueOf(Ssq.getRed3())>33||Integer.valueOf(Ssq.getRed3())<1)||Ssq.getRed1().length()!=2){
            return Response.fail("红球范围或格式错误错误!");
        }
        if(  (Integer.valueOf(Ssq.getRed4())>33||Integer.valueOf(Ssq.getRed4())<1)||Ssq.getRed1().length()!=2){
            return Response.fail("红球范围或格式错误错误!");
        }
        if(  (Integer.valueOf(Ssq.getRed5())>33||Integer.valueOf(Ssq.getRed5())<1)||Ssq.getRed1().length()!=2){
            return Response.fail("红球范围或格式错误错误!");
        }
        if( ( Integer.valueOf(Ssq.getRed6())>33||Integer.valueOf(Ssq.getRed6())<1)||Ssq.getRed1().length()!=2){
            return Response.fail("红球范围或格式错误错误!");
        }
        if(  (Integer.valueOf(Ssq.getBlue())>16||Integer.valueOf(Ssq.getBlue())<1)||Ssq.getRed1().length()!=2){
            return Response.fail("篮球范围或格式错误错误!");
        }
        Ssq.setCreateTime(new Date());
        Ssq.setIsBonus("0");
        Ssq.setBonus("0");
        Ssq.setUser(loginUser.getNickName());
        //代买
        if(Ssq.getType().equals(2)){
            //代买者
            if(DataUtil.isEmpty(Ssq.getBuyer())){
                return Response.fail("请指定购买者!!");
            }
            if(Ssq.getBuyer().equals(loginUser.getNickName())){
                return Response.fail("无法指定自己购买!!");
            }
            Ssq.setState(0);
        }else{
            Ssq.setType(1);
            Ssq.setState(1);
        }


        try {
            SsqService.insert(Ssq);
        } catch (Exception e) {
            log.error("新增异常,{}", JSONObject.toJSONString(Ssq));
        }
        return Response.ok("定能暴富!");
    }

    /**
     * 分页查询
     * @param query
     * @return
     */
    @RequestMapping(value = "buyRecord")
    @ResponseBody
    public Response<List<SsqParam>> buyRecord(SsqQuery query) {
        RequestContext.RequestUser loginUser = RequestContext.getCurrentUser();
        if (DataUtil.isEmpty(query.getPage())) {
            query.setPage(0);
        }
        query.setLimit(5);
        if(query.getType().equals(3)){
            query.setBuyer(loginUser.getNickName());
        }
        query.setPage(query.getLimit()*(query.getPage()));
        Map<String, Object> value = SsqMapper.getValue();
        String bonus=(String.valueOf(value.get("bonus")));
        String spend=MoneyUtil.multiply((String.valueOf(value.get("count"))),"2") ;
        long count=SsqService.querycount();
        long page=0;
        if(count%query.getLimit()==0){
            page=count/query.getLimit();
        }else{
            page=count/query.getLimit()+1;
        }
        List<Ssq> ssqs1 = SsqService.queryListByParam(query);
        List<String> list = ssqs1.stream().map(Ssq::getTerm).collect(Collectors.toList());
        ArrayList<SsqParam> ssqParams = new ArrayList<>();
        long commission=SsqMapper.getCommission(loginUser.getNickName());
        for (String s : list) {
            SsqQuery ssqQuery = new SsqQuery();
            ssqQuery.setTerm(s);
            ssqQuery.setBuyer(loginUser.getNickName());
            List<Ssq> ssqs = SsqService.queryList(ssqQuery);
            List<SsqVo> list2=new ArrayList<>();
            for(Ssq Ssq:ssqs){
                list2.add(getSsqVo2(Ssq));
            }
            ssqParams.add(new SsqParam(list2,ssqQuery.getTerm(),page,bonus,spend,commission));
        }
        return Response.ok(ssqParams);
    }



    private SsqVo getSsqVo2(Ssq ssq) {
        SsqVo vo= new SsqVo();
        vo.setRed5(ssq.getRed5());
        vo.setRed6(ssq.getRed6());
        vo.setBlue(ssq.getBlue());
        vo.setRed1(ssq.getRed1());
        vo.setRed2(ssq.getRed2());
        vo.setRed3(ssq.getRed3());
        vo.setRed4(ssq.getRed4());
        vo.setNum(ssq.getNum());
        vo.setTerm(ssq.getTerm());
        vo.setIsBonus(ssq.getIsBonusDesc());
        vo.setState(ssq.getState());
        vo.setId(ssq.getId());
        vo.setCreateTime(DateUtil.formatDate(ssq.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
        return vo;
    }

    @RequestMapping(value = "suiJi")
    @ResponseBody
    public Response<suiJi> buyRecord() {
        suiJi suiJi=new suiJi();
        List<Integer> num=new ArrayList<>();
        Random r = new Random();
        int i=0;
        while(true) {
            int number = r.nextInt(32)+1;
            if(!num.contains(number)) {
                num.add(number);
                i++;
                if(i==6){
                    break;
                }
            }
        }
        Collections.sort(num, (y,x)->{
            return x-y;
        });
        List<String> str=new ArrayList<>();
        for (int j=0;j<6;j++){
            if(String.valueOf(num.get(j)).length()<2){
                str.add(j, "0"+String.valueOf(num.get(j)));
            }else{
                str.add(j, String.valueOf(num.get(j)));
            }
        }
        suiJi.setRed6(str.get(0));
        suiJi.setRed5(str.get(1));
        suiJi.setRed4(str.get(2));
        suiJi.setRed3(str.get(3));
        suiJi.setRed2(str.get(4));
        suiJi.setRed1(str.get(5));
        int blue = r.nextInt(15)+1;
        if(String.valueOf(blue).length()<2){
            suiJi.setBlue("0"+String.valueOf(blue));
        }else{
            suiJi.setBlue(String.valueOf(blue));
        }
        return Response.ok(suiJi);
    }

    @RequestMapping(value = "changeSsqState")
    @ResponseBody
    public Response<String> changeSsqState(Long id,Integer state) {
        Ssq ssq = SsqService.queryById(id);
        ssq.setState(state);
        try {
            SsqService.updateById(ssq);
        } catch (Exception e) {
            log.error("更新异常，{}", id);
            return Response.fail("操作失败！");
        }
        return Response.ok("操作成功！");
    }



}
