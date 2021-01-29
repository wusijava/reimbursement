package com.wusi.reimbursement.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @ Description   :  天行数据接口测试
 * @ Author        :  wusi
 * @ CreateDate    :  2021/1/29$ 15:44$
 */
@RestController
@Slf4j
public class TianApiController {
    //key
    private static String httpArg = "key=7e110e1235a562e9e11b09cfd965c5a2";
    //毒鸡汤
    private static String duJiTang = "http://api.tianapi.com/txapi/dujitang/index";
    //每日一句
    private static String dayEnglish = "http://api.tianapi.com/txapi/everyday/index";
    //网易云
    private static String wangYi = "http://api.tianapi.com/txapi/hotreview/index";

    @RequestMapping(value = "du", produces = "application/json; charset=utf-8")
    public String duJiTang() {
        String jsonResult = request(duJiTang, httpArg);
        System.out.println(jsonResult);
        return jsonResult;
    }

    private static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(value = "english", produces = "application/json; charset=utf-8")
    public String english() {
        String jsonResult = request(dayEnglish, httpArg);
        return jsonResult;
    }

    @RequestMapping(value = "wangYi", produces = "application/json; charset=utf-8")
    public String wangYi() {
        String jsonResult = request(wangYi, httpArg);
        return jsonResult;
    }
}
