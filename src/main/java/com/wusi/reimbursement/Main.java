package com.wusi.reimbursement;

import com.wusi.reimbursement.entity.*;
import com.wusi.reimbursement.generator.CodeGenerator;
import com.wusi.reimbursement.service.*;
import com.wusi.reimbursement.service.impl.SmsServiceImpl;
import com.wusi.reimbursement.utils.QrUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {

    public static void main(String[] args) throws Exception {
        /*SmsService  service=new SmsServiceImpl();
        SmsProxy proxy= new SmsProxy(service);
        proxy.send("c++");*/
       /* SmsService service = (SmsService) JdkProxyFactory.getProxy(new SmsServiceImpl());
        service.send("ss++");*/
        AliSmsService service =(AliSmsService) CglibProxyFactory.getProxy(AliSmsService.class);
        service.send("QQQQQ");
      /*  String basePack = Main.class.getPackage().getName();
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.generateMybatisXml(basePack, Reimbursement.class);*/
       // codeGenerator.generateDao(basePack, Reimbursement.class);
      // codeGenerator.generateService(basePack, Reimbursement.class);
      /*  BufferedImage grayQuickMarkImage = QrUtil.buildGrayQuickMarkImage("http://49.233.192.222:8082/#/entrance/select-action");
        QrUtil.writeToLocal("D:/灰码.jpg",grayQuickMarkImage);*/
      /*String str= new StringBuilder().append("ja").append("va").toString();
        System.out.println(System.identityHashCode(str.intern()));
        System.out.println(System.identityHashCode(str));
        System.out.println(str.intern()==str);

        String a1=new String("java");
        String a2=new String("java");
        System.out.println(a1==a2);
        System.out.println(a1.intern()==a2.intern());*/
        /*List<String>  list=new ArrayList<>();
        list.add("1");
        list.add("2");
        for (String s:list){
            list.remove("1");
            //list.removeIf(o->s.equals("1"));
        }
        System.out.println(list);*/

       /* Runtime run= Runtime.getRuntime();
        System.out.println(run.maxMemory());
        System.out.println(run.totalMemory());
        System.out.println(run.freeMemory());
        Executors.newFixedThreadPool(10);
        Executors.newCachedThreadPool();
        Executors.newScheduledThreadPool(10);
        Executors.newSingleThreadExecutor();*/
      /*  HashMap<String,String> map=new HashMap<>();
        map.put("1", "test1");
        map.put("2", "test2");
        map.put("3", "test3");
        map.put("4", "test4");
        map.forEach((key,value)->System.out.println(key+value));
        ExecutorService pool = Executors.newFixedThreadPool(2);*/
        /*while(true){
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName()+"is running!");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }*/
        /*ConcurrentHashMap ConcurrentHashMap=new ConcurrentHashMap();
        ConcurrentHashMap.put("test", "test");*/

    }
}
