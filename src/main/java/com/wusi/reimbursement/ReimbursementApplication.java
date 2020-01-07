package com.wusi.reimbursement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@MapperScan(value = {"com.wusi.reimbursement.mapper"})
@ServletComponentScan(basePackages = {"com.wusi.reimbursement.filter"})
@SpringBootApplication
public class ReimbursementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReimbursementApplication.class, args);
    }

}
