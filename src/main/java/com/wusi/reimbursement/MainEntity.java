package com.wusi.reimbursement;

import com.wusi.reimbursement.entity.*;
import com.wusi.reimbursement.generator.CodeGenerator;

/**
 * @author duchong
 * @description
 * @date
 */
public class MainEntity {

    public static void main(String[] args) {
        String basePack = Main.class.getPackage().getName();
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.generateMybatisXml(basePack, Address.class);
        codeGenerator.generateDao(basePack, Address.class);
        codeGenerator.generateService(basePack, Address.class);
//        codeGenerator.generateCreateSqlForPackage("com.click.jd.merchant.modules");
    }
}
