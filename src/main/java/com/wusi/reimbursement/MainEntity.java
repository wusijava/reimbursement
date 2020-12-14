package com.wusi.reimbursement;

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
        codeGenerator.generateMybatisXml(basePack, MonitorRecord.class);
        codeGenerator.generateDao(basePack, MonitorRecord.class);
        codeGenerator.generateService(basePack, MonitorRecord.class);
//        codeGenerator.generateCreateSqlForPackage("com.click.jd.merchant.modules");
    }
}
