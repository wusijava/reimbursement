package com.wusi.reimbursement.controller;

import com.wusi.reimbursement.common.Response;
import com.wusi.reimbursement.common.ratelimit.anonation.RateLimit;
import com.wusi.reimbursement.entity.MathPlan;
import com.wusi.reimbursement.mapper.MathMapper;
import com.wusi.reimbursement.query.MathPlanQuery;
import com.wusi.reimbursement.query.MathQuery;
import com.wusi.reimbursement.service.MathPlanService;
import com.wusi.reimbursement.service.MathService;
import com.wusi.reimbursement.utils.DataUtil;
import com.wusi.reimbursement.utils.DateUtil;
import com.wusi.reimbursement.utils.MoneyUtil;
import com.wusi.reimbursement.utils.RedisUtil;
import com.wusi.reimbursement.vo.Math;
import com.wusi.reimbursement.vo.MathParam;
import com.wusi.reimbursement.vo.MathPlanVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ Description   :  小柠檬专属controller
 * @ Author        :  wusi
 * @ CreateDate    :  2021/1/15$ 10:06$
 */
@RestController
@Slf4j
@RequestMapping(value = "api/math")
public class MathController {
    @Autowired
    private MathService MathService;
    @Autowired
    private MathMapper MathMapper;
    @Autowired
    private MathPlanService MathPlanService;

    @ResponseBody
    @RequestMapping(value = "getTi")
    @RateLimit(permitsPerSecond = 1, ipLimit = true, description = "限制出题频率")
    public Response<Math> getTi(Integer size) {
        //先判断缓存是否有未做的题
        //System.out.println(RedisUtil.get("redisTi"));
        if(RedisUtil.get("redisTi")!=null){
            Math ma=(Math)RedisUtil.get("redisTi");
            MathPlan mp= getPlan();
            if(DataUtil.isNotEmpty(mp)){
                ma.setNum(mp.getWeiDo());
            }else{
                ma.setNum(null);
            }
            return Response.ok(ma);
        }
        Math res = new Math();
        while (true) {
            Random r = new Random();
            int numberOne = r.nextInt(size);
            int numberTwo = r.nextInt(size);
            int number = r.nextInt(3);
            if (numberOne < numberTwo) {
                int temp = 0;
                temp = numberOne;
                numberOne = numberTwo;
                numberTwo = temp;
            }
            res.setNumOne(numberOne);
            res.setNumTwo(numberTwo);
            int result = 0;
            if (number % 2 == 1) {
                res.setSymbolOne("-");
                result = numberOne - numberTwo;
            } else {
                res.setSymbolOne("+");
                result = numberOne + numberTwo;
            }
            if (result > (size)||result<=0) {
                continue;
            }
            int symbolTwo = r.nextInt(3);
            int numberThree = r.nextInt(result);
            res.setNumThree(numberThree);
            if (symbolTwo % 2 == 1) {
                res.setSymbolTwo("-");
                result = result - numberThree;
            } else {
                res.setSymbolTwo("+");
                result = result + numberThree;
            }
            res.setResult(result);
            if (result < 100) {
                break;
            }
        }
        MathPlan plan = getPlan();
        if(DataUtil.isNotEmpty(plan)){
            res.setNum(plan.getWeiDo());
        }
        //避免退出刷新换题
        RedisUtil.set("redisTi", res);
        return Response.ok(res);
    }

    @ResponseBody
    @RequestMapping(value = "checkTi")
    public Response<String> checkTi(Math math,Long rowId,Integer type) {
        com.wusi.reimbursement.entity.Math count=null;
        if(DataUtil.isNotEmpty(rowId)){
            count = MathService.queryById(rowId);
        }
        if (DataUtil.isEmpty(math.getResult())) {
            return Response.ok("请输入你的答案!~");
        }
        int two=check(math,rowId,count);
        if (two == math.getResult()) {
            //获取缓存题
            Math m=null;
            if(type.equals(1)){
                m= (Math)RedisUtil.get("cuoTi");
            }else{
                m= (Math)RedisUtil.get("redisTi");
            }
            if(m.getNumOne().equals(math.getNumOne())&&m.getNumTwo().equals(math.getNumTwo())&&m.getNumThree().equals(math.getNumThree())&&m.getSymbolOne().equals(math.getSymbolOne())&&m.getSymbolTwo().equals(math.getSymbolTwo())){
                //表示错题
                if(type.equals(1)){
                    RedisUtil.del("cuoTi");
                }else{
                    RedisUtil.del("redisTi");
                }

            }
            return Response.ok("答对了,小柠檬不错哦~");
        } else {
            return Response.ok("答错了,小柠檬加油哦~");
        }

    }
    public int check(Math math, Long rowId, com.wusi.reimbursement.entity.Math count){
        int one = 0;
        int two = 0;
        if (math.getSymbolOne().equals("-")) {
            one = math.getNumOne() - math.getNumTwo();
        } else {
            one = math.getNumOne() + math.getNumTwo();
        }
        if ("-".equals(math.getSymbolTwo())) {
            two = one - math.getNumThree();
        } else {
            two = one + math.getNumThree();
        }
        //记录
        com.wusi.reimbursement.entity.Math log = new com.wusi.reimbursement.entity.Math();
        log.setContent(math.getNumOne() + math.getSymbolOne() + math.getNumTwo() + math.getSymbolTwo() + math.getNumThree() + "=" + math.getResult());
        log.setCreateTime(new Date());

        //加入任务
        MathPlan query = new MathPlan();
        query.setTime(DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
        MathPlan plan = MathPlanService.queryOne(query);
        if (DataUtil.isEmpty(plan)) {
            MathPlan newPlan = new MathPlan();
            newPlan.setTime(DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            //星期六或星期天暂定150题
            if (w == 0 || w == 6) {
                newPlan.setTask("150");
            } else {
                //平时做50题
                newPlan.setTask("50");
            }
            newPlan.setWeiDo(newPlan.getTask());
            newPlan.setYiDo("0");
            newPlan.setRight("0");
            newPlan.setError("0");
            newPlan.setCreateTime(new Date());
            MathPlanService.insert(newPlan);
        }
        //再查一次
        MathPlan sec = new MathPlan();
        sec.setTime(DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
        MathPlan secPlan = MathPlanService.queryOne(sec);
        if (two == math.getResult()) {
            log.setResult("对");
            secPlan.setYiDo(MoneyUtil.add(secPlan.getYiDo(), "1"));
            secPlan.setWeiDo(MoneyUtil.subtract(secPlan.getTask(), secPlan.getYiDo()));
            if (Integer.valueOf(secPlan.getWeiDo()) < 0) {
                secPlan.setWeiDo("0");
            }
            secPlan.setRight(MoneyUtil.add(secPlan.getRight(), "1"));
            if(DataUtil.isNotEmpty(rowId)){
                count.setCount(count.getCount()+1);
                MathService.updateById(count);
            }
        } else {
            log.setResult("错");
            log.setRightResult(two);
            log.setCount(0);
            secPlan.setError(MoneyUtil.add(secPlan.getError(), "1"));
            secPlan.setTask(MoneyUtil.add(secPlan.getTask(), "2"));
            secPlan.setWeiDo(MoneyUtil.add(secPlan.getWeiDo(), "2"));
        }
        log.setTime(DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
        MathService.insert(log);
        MathPlanService.updateById(secPlan);
        return two;
    }
    @RequestMapping(value = "homeworkLog")
    @ResponseBody
    public Response<List<MathParam>> homeworkLog(MathQuery query) {
        if (DataUtil.isEmpty(query.getPage())) {
            query.setPage(0);
        }
        query.setLimit(1);
        long count = MathService.querycount();
        long page = 0;
        if (count % query.getLimit() == 0) {
            page = count / query.getLimit();
        } else {
            page = count / query.getLimit() + 1;
        }
        List<String> result = MathMapper.getResult();
        query.setPage(query.getLimit() * (query.getPage()));
        List<com.wusi.reimbursement.entity.Math> maths = MathService.queryListByParam(query);
        List<String> list = maths.stream().map(com.wusi.reimbursement.entity.Math::getTime).collect(Collectors.toList());
        ArrayList<MathParam> MathParam = new ArrayList<>();
        for (String time : list) {
            String task = null;
            String yiDo = null;
            String weiDo = null;
            String rightToday;
            String errorToday;
            MathPlan plan = new MathPlan();
            plan.setTime(time);
            MathPlan queryPlan = MathPlanService.queryOne(plan);
            task = queryPlan.getTask();
            yiDo = queryPlan.getYiDo();
            weiDo = queryPlan.getWeiDo();
            rightToday = queryPlan.getRight();
            errorToday = queryPlan.getError();
            com.wusi.reimbursement.entity.Math math = new com.wusi.reimbursement.entity.Math();
            math.setTime(time);
            List<com.wusi.reimbursement.entity.Math> ssqs = MathService.queryList(math);
            List<com.wusi.reimbursement.entity.Math> list2 = new ArrayList<>();
            for (com.wusi.reimbursement.entity.Math com : ssqs) {
                list2.add(com);
            }
            MathParam.add(new MathParam(list2, time, page, result.get(0), result.get(1), task, yiDo, weiDo, rightToday, errorToday));
        }
        return Response.ok(MathParam);
    }

    //错题
    @ResponseBody
    @RequestMapping(value = "cuoTi")
    @RateLimit(permitsPerSecond = 1, ipLimit = true, description = "限制出题频率")
    public Response<Math> cuoTi() {
        if(RedisUtil.get("cuoTi")!=null){
            Math ma=(Math)RedisUtil.get("cuoTi");
            MathPlan mp= getPlan();
            if(DataUtil.isNotEmpty(mp)){
                ma.setNum(mp.getWeiDo());
            }else{
                ma.setNum(null);
            }
            ma.setSource("来自未做错题缓存!");
            return Response.ok(ma);
        }
        List<com.wusi.reimbursement.entity.Math> maths ;
        String source="";
        if (DataUtil.isEmpty(RedisUtil.get("ti"))) {
            log.error("无缓存,数据库取值");
            com.wusi.reimbursement.entity.Math cuoTi = new com.wusi.reimbursement.entity.Math();
            cuoTi.setResult("错");
            cuoTi.setCount(1);
            maths = MathService.queryList(cuoTi);
            if(maths.size()==0){
                return Response.ok(null);
            }
            source="无缓存,数据库取值,共计剩余:"+maths.size()+"道错题!";
            //存缓存  存5分钟
            long time=maths.size();
            RedisUtil.set("ti", maths,  time);
        } else {
            long ti = RedisUtil.getExpire("ti");
            source="有缓存,redis取值,缓存剩余"+ti+"秒!";
            log.error("有缓存,redis取值");
            Object object = RedisUtil.get("ti");
            maths = (List<com.wusi.reimbursement.entity.Math>) object;
        }
        Integer size = maths.size();
        Random r = new Random();
        int index = r.nextInt(size);
        com.wusi.reimbursement.entity.Math ti = maths.get(index);
        String content = ti.getContent();
        String back = content.replace("-", "+");
        int i = back.indexOf("+");
        int j = back.indexOf("+", i + 1);
        int k = back.indexOf("=");
        Math vo = new Math();
        vo.setNumOne(Integer.valueOf(back.substring(0, i)));
        vo.setSymbolOne(content.substring(i, i + 1));
        vo.setNumTwo(Integer.valueOf(back.substring(i + 1, j)));
        vo.setSymbolTwo(content.substring(j, j + 1));
        vo.setNumThree(Integer.valueOf(back.substring(j + 1, k)));
        vo.setSource(source);
        vo.setRowId(ti.getId());
        MathPlan plan=getPlan();
        if(DataUtil.isNotEmpty(plan)){
            vo.setNum(plan.getWeiDo());
        }
        RedisUtil.set("cuoTi", vo);
        return Response.ok(vo);
    }

    private MathPlan getPlan() {
        MathPlan query = new MathPlan();
        query.setTime(DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
        MathPlan plan = MathPlanService.queryOne(query);
        return plan;
    }

    @ResponseBody
    @RequestMapping(value = "homeworkTotal")
    @RateLimit(permitsPerSecond = 1, ipLimit = true, description = "限制出题频率")
    public Response<Page<MathPlanVo>> homeworkTotal(MathPlanQuery query) {
        if (DataUtil.isEmpty(query.getPage())) {
            query.setPage(0);
        }
        if (DataUtil.isEmpty(query.getLimit())) {
            query.setLimit(2);
        }
        Pageable pageable = PageRequest.of(query.getPage(), query.getLimit());
        Page<MathPlan> page = MathPlanService.queryPage(query, pageable);
        List<MathPlanVo> voList = new ArrayList<>();
        for (MathPlan plan : page.getContent()) {
            voList.add(getPlanVo(plan));
        }
        Page<MathPlanVo> voPage = new PageImpl<>(voList, pageable, page.getTotalElements());
        return Response.ok(voPage);
    }

    private MathPlanVo getPlanVo(MathPlan plan) {
        MathPlanVo vo = new MathPlanVo();
        vo.setTask(plan.getTask());
        vo.setYiDo(plan.getYiDo());
        vo.setWeiDo(plan.getWeiDo());
        vo.setTime(plan.getTime());
        vo.setRight(plan.getRight());
        vo.setError(plan.getError());
        BigDecimal one=new BigDecimal(plan.getRight());
        BigDecimal two=new BigDecimal(MoneyUtil.add(plan.getRight(), plan.getError()));
        vo.setRate(one.divide(two, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).setScale(2).toString()+"%");
        return vo;
    }

    @ResponseBody
    @RequestMapping(value = "getTiKindTwo")
    @RateLimit(permitsPerSecond = 1, ipLimit = true, description = "限制出题频率")
    public Response<Math> homeworkTotal() {
        Response<Math> ti = this.getTi(10);
        Math data = ti.getData();
        Random r = new Random();
        int numberOne = r.nextInt(3);
        if(numberOne==0){
            data.setNumOne(null);
        }
        if(numberOne==1){
            data.setNumTwo(null);
        }
        if(numberOne==2){
            data.setNumThree(null);
        }
        MathPlan plan = getPlan();
        if(DataUtil.isNotEmpty(plan)){
            data.setNum(plan.getWeiDo());
        }
        return Response.ok(data);
    }

    @ResponseBody
    @RequestMapping(value = "checkTiKindTwo")
    @RateLimit(permitsPerSecond = 1, ipLimit = true, description = "限制出题频率")
    public Response<String> checkTiKindTwo(Math math) {
        if(DataUtil.isEmpty(math.getNumOne())||DataUtil.isEmpty(math.getNumTwo())||DataUtil.isEmpty(math.getNumThree())){
            return Response.fail("请填写答案!");
        }
        int b=check(math, null, null);
        if(b==math.getResult()){
            RedisUtil.del("redisTi");
            return Response.ok("答对了,小柠檬不错哦~");
        } else {
            return Response.ok("答错了,小柠檬加油哦~");
        }
    }

}
