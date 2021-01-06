package com.wusi.reimbursement.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @ Description   :  计算ssq期号
 * @ Author        :  wusi
 * @ CreateDate    :  2021/1/6$ 12:32$
 */
public class WeekUtils {
    public static String getSsqNum() throws ParseException {
        DateFormat sdf = new SimpleDateFormat(DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS);
        Calendar calendar=Calendar.getInstance();
        String yearStr = String.valueOf(calendar.get(Calendar.YEAR));
        long days = DateUtil.betweenDays(sdf.parse(yearStr +"-01-01 00:00:00"), new Date());
        Calendar start=Calendar.getInstance();
        start.setTime(sdf.parse(yearStr +"-01-01 00:00:00"));
        int num=0;
        for (int i=0;i<days;i++){
            int w = start.get(Calendar.DAY_OF_WEEK)-1;
            if (w < 0) {
                w = 0;
            }
            if(w==0||w==2||w==4){
                num++;
            }
            start.add(Calendar.DAY_OF_YEAR,1);
        }
        String numStr=String.valueOf(num+1);
        if(String.valueOf(num).length()<3){
            for( int j=3-String.valueOf(num).length();j<=3;j++ ){
                numStr="0"+numStr;
            }
        }
        String result=yearStr+"-"+numStr;
        return result;
    }
}
