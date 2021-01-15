package com.wusi.reimbursement.controller;

import com.wusi.reimbursement.common.Response;
import com.wusi.reimbursement.common.ratelimit.anonation.RateLimit;
import com.wusi.reimbursement.service.MathService;
import com.wusi.reimbursement.utils.DataUtil;
import com.wusi.reimbursement.vo.Math;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;

/**
 * @ Description   :  小柠檬专属controller
 * @ Author        :  wusi
 * @ CreateDate    :  2021/1/15$ 10:06$
 */
@RestController
@Slf4j
@RequestMapping(value = "api/math")
public class Mahtcontroller {
    @Autowired
    private MathService MathService;
    @ResponseBody
    @RequestMapping(value = "getTi")
    @RateLimit(permitsPerSecond = 0.05, ipLimit = true,description = "限制出题频率")
    public Response<Math> getTi() {
        Math res=new Math();
        while (true){
            Random r = new Random();
            int numberOne = r.nextInt(99);
            int numberTwo = r.nextInt(99);
            //int numberThree = r.nextInt(99);
            int number = r.nextInt(3);

            //45  99
            if(numberOne<numberTwo){
                int temp=0;
                temp=numberOne;
                numberOne=numberTwo;
                numberTwo=temp;
            }
            res.setNumOne(numberOne);
            res.setNumTwo(numberTwo);
            int result=0;
            if(number%2==1){
                res.setSymbolOne("-");
                result=  numberOne-numberTwo;
            }else{
                res.setSymbolOne("+");
                result=  numberOne+numberTwo;
            }
            if(result>100){
                continue;
            }
            int symbolTwo = r.nextInt(3);
            int numberThree = r.nextInt(result);
            res.setNumThree(numberThree);
            if(symbolTwo%2==1){
                res.setSymbolTwo("-");
                result=result-numberThree;
            }else{
                res.setSymbolTwo("+");
                result=result+numberThree;
            }
            res.setResult(result);
            if(result<100){
                break;
            }
        }
        return Response.ok(res);
    }

    @ResponseBody
    @RequestMapping(value = "checkTi")
    public Response<String> checkTi(Math math) {
        if(DataUtil.isEmpty(math.getResult())){
            return Response.ok("请输入你的答案!~") ;
        }
        int one=0;
        int two=0;
        if(math.getSymbolOne().equals("-")){
            one=math.getNumOne()-math.getNumTwo();
        }else{
            one=math.getNumOne()+math.getNumTwo();
        }
        if("-".equals(math.getSymbolTwo())){
            two= one-math.getNumThree();
        }else{
            two= one+math.getNumThree();
        }
        //记录
        com.wusi.reimbursement.entity.Math  log=new com.wusi.reimbursement.entity.Math();
        log.setContent(math.getNumOne()+math.getSymbolOne()+math.getNumTwo()+math.getSymbolTwo()+math.getNumThree()+"="+math.getResult());
        log.setCreateTime(new Date());
        if(two==math.getResult()){
            log.setResult("对");
        }else{
            log.setResult("错");
        }
        MathService.insert(log);
        if(two==math.getResult()){
            return Response.ok("答对了,小柠檬真棒~") ;
        }else{
            return Response.ok("答错了,小柠檬继续努力哦~") ;
        }

    }
}
