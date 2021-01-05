package com.wusi.reimbursement.controller;

import com.wusi.reimbursement.common.Response;
import com.wusi.reimbursement.entity.Ssq;
import com.wusi.reimbursement.entity.SsqBonus;
import com.wusi.reimbursement.query.SsqQuery;
import com.wusi.reimbursement.service.SsqBonusService;
import com.wusi.reimbursement.service.SsqService;
import com.wusi.reimbursement.utils.DataUtil;
import com.wusi.reimbursement.utils.DateUtil;
import com.wusi.reimbursement.vo.HouseworkVO;
import com.wusi.reimbursement.vo.SsqBonusVo;
import com.wusi.reimbursement.vo.SsqVo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @ Description   :  菩萨保佑 让我走向人生巅峰
 * @ Author        :  wusi
 * @ CreateDate    :  2021/1/5$ 10:13$
 */
@RestController
@Slf4j
@RequestMapping(value = "/ssq")
public class SsqController {
    @Autowired
    private SsqBonusService SsqBonusService;
    @Autowired
    private SsqService SsqService;

    @RequestMapping(value = "getBonusNum")
    @ResponseBody
    @Scheduled(cron = "0 0 22 * * ?")
    public void getBonusNum() throws IOException, ParseException {
        log.error("获取开奖号码开始执行,{}", DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        SsqBonus ssq = new SsqBonus();
        String html = Jsoup.connect("https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&tn=baidu&wd=%E5%8F%8C%E8%89%B2%E7%90%83&fenlei=256&rsv_pq=80b37df9000408f5&rsv_t=010coSUSP%2FnvityAj%2FwITb2JWy1prLMoue7ZVxtbRk0cHcElsUmVreYjJzQ&rqlang=cn&rsv_dl=tb&rsv_enter=1&rsv_sug3=10&rsv_sug1=14&rsv_sug7=101&rsv_sug2=0&rsv_btype=i&inputT=4181&rsv_sug4=5061").timeout(200000).execute().body();
        //System.out.println(html);
        List<String> list = GetJsonValue(html, "<span class=\"c-icon c-icon-ball-red op_caipiao_ball_red c-gap-right-small\">");
        ssq.setTerm(list.get(0));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ssq.setCreateTime(sdf.parse(list.get(1)));
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(ssq.getCreateTime());
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        ssq.setWeek(weekDays[w]);
        ssq.setRed1(list.get(2));
        ssq.setRed2(list.get(3));
        ssq.setRed3(list.get(4));
        ssq.setRed4(list.get(5));
        ssq.setRed5(list.get(6));
        ssq.setRed6(list.get(7));
        ssq.setBlue(list.get(8));
        SsqBonus query = new SsqBonus();
        query.setTerm(list.get(0));
        Long num = SsqBonusService.queryCount(query);
        if (num < 1) {
            SsqBonusService.insert(ssq);
            //去查找有误购买此期的购买记录
            Ssq buy=new Ssq();
            ssq.setTerm(buy.getTerm());
            List<Ssq> buyList = SsqService.queryList(buy);
            if(DataUtil.isNotEmpty(buyList)){
                for(Ssq buyOne:buyList){
                    if(!list.get(0).equals(buyOne.getTerm())){
                        break;
                    }
                    int redNum=0;
                    int blueNum=0;
                    if(buyOne.getBlue().equals(list.get(8))){
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
                            if(myNum.get(i).equals(list.get(j+2))){
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
                        buyOne.setIsBonus("0");
                    }
                    buyOne.setWeek(ssq.getWeek());
                    buyOne.setBonusTime(sdf.parse(list.get(1)));
                    SsqService.updateById(buyOne);
                }

            }else{
                log.error("本期未购买,{}", ssq.getTerm());
            }
        }
        log.error("获取开奖号码执行结束,{}", DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }

    public static List<String> GetJsonValue(String jsonStr, String key) {
        List<String> ball = new ArrayList<>();
        //获取期数
        int index = jsonStr.indexOf("期开奖结果");
        String term = jsonStr.substring(index - 8, index);
        ball.add(term);
        //开奖日期
        int bonusIndex = jsonStr.indexOf("开奖日期：");
        String bonusTime = jsonStr.substring(bonusIndex + "开奖日期：".length(), bonusIndex + "开奖日期：".length() + "2021-01-03 21:15:00".length());
        ball.add(bonusTime);
        for (int i = 0; i < 6; i++) {
            String value = getStr(jsonStr, key);
            ball.add(value);
            jsonStr = jsonStr.replace(key + value, value);
        }
        String blue = getStr(jsonStr, "<span class=\"c-icon c-icon-ball-blue op_caipiao_ball_blue c-gap-right-small\">");
        ball.add(blue);
        return ball;
    }

    public static String getStr(String jons, String div) {
        int index = jons.indexOf(div);
        int length = div.length();
        return jons.substring(index + length, index + length + 2);
    }

    @RequestMapping(value = "getSsqRecord")
    @ResponseBody
    public Response<Page<SsqBonusVo>> getSsqRecord(SsqQuery query) {
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
}
