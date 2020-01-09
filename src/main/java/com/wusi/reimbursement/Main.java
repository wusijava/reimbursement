package com.wusi.reimbursement;

import com.wusi.reimbursement.entity.*;
import com.wusi.reimbursement.generator.CodeGenerator;


public class Main {

    public static void main(String[] args) {
        String basePack = Main.class.getPackage().getName();
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.generateMybatisXml(basePack, Reimbursement.class);
       // codeGenerator.generateDao(basePack, Reimbursement.class);
      // codeGenerator.generateService(basePack, Reimbursement.class);

    }
}
