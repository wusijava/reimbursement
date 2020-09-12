package com.wusi.reimbursement;

import com.wusi.reimbursement.entity.*;
import com.wusi.reimbursement.generator.CodeGenerator;
import com.wusi.reimbursement.utils.QrUtil;

import java.awt.image.BufferedImage;


public class Main {

    public static void main(String[] args) throws Exception {
      /*  String basePack = Main.class.getPackage().getName();
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.generateMybatisXml(basePack, Reimbursement.class);*/
       // codeGenerator.generateDao(basePack, Reimbursement.class);
      // codeGenerator.generateService(basePack, Reimbursement.class);
        BufferedImage grayQuickMarkImage = QrUtil.buildGrayQuickMarkImage("http://49.233.192.222:8082/#/entrance/select-action");
        QrUtil.writeToLocal("D:/灰码.jpg",grayQuickMarkImage);
    }
}
