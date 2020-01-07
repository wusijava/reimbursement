package com.wusi.reimbursement;

import com.wusi.reimbursement.entity.User;
import com.wusi.reimbursement.generator.CodeGenerator;

/**
 * @author duchong
 * @description
 * @date
 */
public class Main {

    public static void main(String[] args) {
        String basePack = Main.class.getPackage().getName();
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.generateMybatisXml(basePack, User.class);
        codeGenerator.generateDao(basePack, User.class);
       codeGenerator.generateService(basePack, User.class);

    }
}
