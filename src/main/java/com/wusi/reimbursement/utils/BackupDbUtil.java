package com.wusi.reimbursement.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupDbUtil {
     public static String backup(String database) throws IOException {
      System.out.println("------开始备份数据库定时任务------");
      //String user = "root"; //数据库的用户名
      //String password = "13545630179qq.";//数据库的密码
      //String database = "red_packet";//要备份的数据库名
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      String filepath = "/home/back/dababase/"+sdf.format(date)+database+".sql";
      File file = new File(filepath);
      if(!file.exists()){
       file.createNewFile();  
      }
      String stmt1 ="docker exec -it  mysql5.7 mysqldump -uroot -p13545630179qq.  taobao >"+filepath;
      try {
        Runtime.getRuntime().exec(stmt1);
        System.out.println("------已经保存到 " + filepath + " 中------");
      } catch (IOException e) {
       e.printStackTrace();
      }
      return filepath;
     }
    }