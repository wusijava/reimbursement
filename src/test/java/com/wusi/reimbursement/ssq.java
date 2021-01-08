package com.wusi.reimbursement;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ Description   :  获取双色球开奖号码
 * @ Author        :  wusi
 * @ CreateDate    :  2021/1/4$ 16:13$
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ssq {
    @Test
    public void Test() throws IOException {
        String html = Jsoup.connect("https://shuangseqiu.cjcp.com.cn/").timeout(200000).execute().body();
        //System.out.println(html);
        List<String> list=GetJsonValue(html, "<div class=\"red_q\">");
        System.out.println(html);
    }
    public static List<String> GetJsonValue(String jsonStr, String key) {
        List<String> ball=new ArrayList<>();
        //获取期数
        int index = jsonStr.indexOf("中国福利彩票双色球第");
        String term=jsonStr.substring(index+"中国福利彩票双色球第".length(), index+"中国福利彩票双色球第".length()+7);
        //开奖日期
        int bonusIndex = jsonStr.indexOf("开奖日期:");
        String bonusTime=jsonStr.substring(bonusIndex+"开奖日期:".length(), bonusIndex+"开奖日期:".length()+"2021-03-07".length());
        System.out.println(term);
        System.out.println(bonusTime);
        for(int i=0;i<6;i++){
            String value=getStr(jsonStr,key);
            ball.add(value);
            jsonStr=jsonStr.replace(key+value, value);
        }
        String blue=getStr(jsonStr,"<div class=\"blue_q\">");
        ball.add(blue);
        return ball;
    }
    public static String getStr(String jons,String div){
        int index = jons.indexOf(div);
        int length = div.length();
        return jons.substring(index + length, index + length+2);
    }
    @Test
    public void getNum() throws ParseException {
      /*  String ssqNum = WeekUtils.getSsqNum();
        System.out.println(ssqNum);*/
       boolean boo= Integer.valueOf("03")>2;
        System.out.println(boo);
    }
}
