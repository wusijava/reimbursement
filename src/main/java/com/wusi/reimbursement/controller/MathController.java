package com.wusi.reimbursement.controller;

import com.wusi.reimbursement.common.Response;
import com.wusi.reimbursement.common.ratelimit.anonation.RateLimit;
import com.wusi.reimbursement.entity.MathPlan;
import com.wusi.reimbursement.mapper.MathMapper;
import com.wusi.reimbursement.query.MathQuery;
import com.wusi.reimbursement.service.MathPlanService;
import com.wusi.reimbursement.service.MathService;
import com.wusi.reimbursement.utils.DataUtil;
import com.wusi.reimbursement.utils.DateUtil;
import com.wusi.reimbursement.utils.MoneyUtil;
import com.wusi.reimbursement.vo.Math;
import com.wusi.reimbursement.vo.MathParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
            if (result > (size)) {
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
        return Response.ok(res);
    }

    @ResponseBody
    @RequestMapping(value = "checkTi")
    public Response<String> checkTi(Math math) {
        if (DataUtil.isEmpty(math.getResult())) {
            return Response.ok("请输入你的答案!~");
        }
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
            newPlan.setTask("50");
            newPlan.setWeiDo("50");
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
            if(Integer.valueOf(secPlan.getWeiDo())<0){
                secPlan.setWeiDo("0");
            }
            secPlan.setRight(MoneyUtil.add(secPlan.getRight(), "1"));
        } else {
            log.setResult("错");
            log.setRightResult(two);
            secPlan.setError(MoneyUtil.add(secPlan.getError(), "1"));
        }
        log.setTime(DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
        MathService.insert(log);
        MathPlanService.updateById(secPlan);
        if (two == math.getResult()) {
            return Response.ok("答对了,小柠檬真棒~");
        } else {
            return Response.ok("答错了,小柠檬继续努力哦~");
        }

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
}
