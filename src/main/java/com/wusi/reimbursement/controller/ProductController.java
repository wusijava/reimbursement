package com.wusi.reimbursement.controller;

import com.wusi.reimbursement.entity.MonitorRecord;
import com.wusi.reimbursement.entity.ProductNew;
import com.wusi.reimbursement.service.MonitorRecordService;
import com.wusi.reimbursement.service.ProductNewService;
import com.wusi.reimbursement.utils.DataUtil;
import com.wusi.reimbursement.utils.DateUtil;
import com.wusi.reimbursement.utils.SMSUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "product")
@Slf4j

public class ProductController {
    static String PHONE_NUB="18602702325";
    //提示下架
    static int templateId=807342;
    //提示上架
    static int templatedIdOnline=817780;
    //统计汇总
    static int templatedIdTotal=817877;
    @Autowired
    private ProductNewService productNewService;
    @Autowired
    private MonitorRecordService monitorRecordService;
    @RequestMapping(value = "getProductState")
    @Scheduled(cron = "0 20 11 * * ?")
    public void product() throws IOException {
        log.error("定时任务已启动!", DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
        List<ProductNew> productNews = productNewService.queryList(new ProductNew());
        String html;
        int i=0;
        int amyOffNum=0;
        int amyOnNum=0;
        for(ProductNew productNew:productNews){
            i++;
            //判断爱美商品状态
            //增加延迟时间  线上
            html = Jsoup.connect(productNew.getAmyUrl()).timeout(200000).execute().body();
            //本地
            //html = Jsoup.connect(url).execute().body();
            String amyValue = GetJsonValue(html, "online");
            if(amyValue.equals(" true,")&&productNew.getAmyState().equals("offline")){
                amyOnNum++;
            }
            if(amyValue.equals("false")&&productNew.getAmyState().equals("online")){
                amyOffNum++;
            }
            productNew.setAmyState(amyValue.equals(" false")?"offline":"online");
            //判断自己状态
            String myValue=null;
            if(DataUtil.isNotEmpty(productNew.getMyUrl())){
                html = Jsoup.connect(productNew.getMyUrl()).execute().body();
                myValue = GetJsonValue(html, "online");
                productNew.setMyState(myValue.equals(" false")?"offline":"online");
            }

         if(amyValue.equals(" true,")&&(!amyValue.equals(myValue))){

             //提示已上架
             SMSUtil.sendSMS(PHONE_NUB, productNew.getModel(),templatedIdOnline);
         }else if(amyValue.equals("false")&&(!amyValue.equals(myValue))){
             //提示已下架
             if(productNew.getAmyState().equals("online")){
                 amyOffNum++;
             }
             SMSUtil.sendSMS(PHONE_NUB, productNew.getModel(),templateId );
         }
            productNew.setCreateTime(new Date());
            productNewService.updateById(productNew);
        }
        MonitorRecord record=new MonitorRecord();
        record.setTotal(String.valueOf(i));
        record.setCreateTime(new Date());
        record.setAmyOfflineNum(amyOffNum);
        record.setAmyOnNum(amyOnNum);
        monitorRecordService.insert(record);
        SMSUtil.sendSMS(PHONE_NUB, ":"+String.valueOf(i),templatedIdTotal );
        log.error("定时任务已结束!", DateUtil.formatDate(new Date(), "yyyy-MM-dd"));

    }

    public static String GetJsonValue(String jsonStr, String key)
    {
        int index=jsonStr.indexOf(key);
        String result=jsonStr.substring(index+18, index+24);
        return result;
    }

}
