package com.wusi.reimbursement;

import com.wusi.reimbursement.utils.WeekUtils;
import org.jsoup.Jsoup;
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
        String html = Jsoup.connect("https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&tn=baidu&wd=%E5%8F%8C%E8%89%B2%E7%90%83&fenlei=256&rsv_pq=80b37df9000408f5&rsv_t=010coSUSP%2FnvityAj%2FwITb2JWy1prLMoue7ZVxtbRk0cHcElsUmVreYjJzQ&rqlang=cn&rsv_dl=tb&rsv_enter=1&rsv_sug3=10&rsv_sug1=14&rsv_sug7=101&rsv_sug2=0&rsv_btype=i&inputT=4181&rsv_sug4=5061").timeout(200000).execute().body();
        //System.out.println(html);
        List<String> list=GetJsonValue(html, "<span class=\"c-icon c-icon-ball-red op_caipiao_ball_red c-gap-right-small\">");
        System.out.println(list);
    }
    public static List<String> GetJsonValue(String jsonStr, String key) {
        List<String> ball=new ArrayList<>();
        //获取期数
        int index = jsonStr.indexOf("期开奖结果");
        String term=jsonStr.substring(index-8, index);
        //开奖日期
        int bonusIndex = jsonStr.indexOf("开奖日期：");
        String bonusTime=jsonStr.substring(bonusIndex+"开奖日期：".length(), bonusIndex+"开奖日期：".length()+"2021-01-03 21:15:00".length());
        System.out.println(term);
        System.out.println(bonusTime);
        for(int i=0;i<6;i++){
            String value=getStr(jsonStr,key);
            ball.add(value);
            jsonStr=jsonStr.replace(key+value, value);
        }
        String blue=getStr(jsonStr,"<span class=\"c-icon c-icon-ball-blue op_caipiao_ball_blue c-gap-right-small\">");
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
