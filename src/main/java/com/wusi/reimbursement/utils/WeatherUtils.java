package com.wusi.reimbursement.utils;

import com.wusi.reimbursement.entity.Weather;
import com.wusi.reimbursement.service.WeatherService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ Description   :  获取天气工具类
 * @ Author        :  wusi
 * @ CreateDate    :  2019/11/28$ 16:13$
 */
public class WeatherUtils {
    public  static   Weather getWeather() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=null;
        Weather data=new Weather();
        String urlapi="https://free-api.heweather.net/s6/weather/forecast?location=caidian&key=b941bbcd687b486aa07aab8586dc115e";
        RestTemplate restTemplate =new RestTemplate();
        String json= restTemplate.getForObject(urlapi, String.class);
        JSONObject jsonObject = JSONObject.fromObject(json);
        //获取第二层数据
        JSONArray jSONArray=jsonObject.getJSONArray("HeWeather6");
        JSONObject jsonObject2 = jSONArray.getJSONObject(0);
        JSONArray twos = jsonObject2.getJSONArray("daily_forecast");
        JSONObject basic = jsonObject2.getJSONObject("basic");
        JSONObject updateJson = jsonObject2.getJSONObject("update");
        List<Map<String,String>> list=new ArrayList<>();
        JSONObject two=null;
        for (int i = 0; i <twos.size(); i++) {
            two=twos.getJSONObject(i);
            Map<String,String> map=new LinkedHashMap<>();
            map.put ("日期",two.get("date").toString());
            map.put ("天气情况",two.get("cond_txt_d").toString());
            if(i==0){
                if(two.get("cond_txt_d").toString().indexOf("雨")!=-1){
                    //SMSUtil.sendSMS("15527875423", "小岩同学", 834979);
                    DingDingTalkUtils.sendDingDingMsg("今日武汉有雨,请带伞!");
                }
                //保存当天天气数据
                data.setCid(basic.getString("cid"));
                data.setLocation(basic.getString("location"));
                data.setParentCity(basic.getString("parent_city"));
                data.setProvince(basic.getString("admin_area"));
                data.setCountry(basic.getString("cnty"));
                data.setLat(basic.getString("lat"));
                data.setLon(basic.getString("lon"));
                data.setTimeZone(basic.getString("tz"));
                data.setLoc(updateJson.getString("loc"));
                data.setUtc(updateJson.getString("utc"));
                data.setCondTxtDay(two.getString("cond_txt_d"));
                data.setCondTxtNight(two.getString("cond_txt_n"));
                data.setDate(sdf.parse(two.getString("date")+" 00:00:00"));
                data.setHum(two.getString("hum"));
                data.setMoonRise(two.getString("mr"));
                data.setMoonSet(two.getString("ms"));
                data.setPres(Integer.valueOf(two.getString("pres")));
                data.setSunRise(two.getString("sr"));
                data.setSunSet(two.getString("ss"));
                data.setTmpMax(two.getString("tmp_max"));
                data.setTmpMin(two.getString("tmp_min"));
                data.setWindDir(two.getString("wind_dir"));
                data.setWindSc(two.getString("wind_sc"));
                data.setWindSpd(two.getString("wind_spd"));
                data.setCreateTime(new Date());
            }
            map.put ("湿度",two.get("hum").toString());
            map.put ("最高温度",two.get("tmp_max").toString()+"摄氏度");
            map.put ("最低温度",two.get("tmp_min").toString()+"摄氏度");
            list.add(map);
        }
        return data;
    }

}
