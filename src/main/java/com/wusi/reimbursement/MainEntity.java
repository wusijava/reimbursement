package com.wusi.reimbursement;

import com.wusi.reimbursement.entity.Housework;
import com.wusi.reimbursement.entity.MonitorRecord;
import com.wusi.reimbursement.entity.OffLineRecord;
import com.wusi.reimbursement.entity.ProductNew;
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
        codeGenerator.generateMybatisXml(basePack, Housework.class);
        codeGenerator.generateDao(basePack, Housework.class);
        codeGenerator.generateService(basePack, Housework.class);
//        codeGenerator.generateCreateSqlForPackage("com.click.jd.merchant.modules");
    }
}
