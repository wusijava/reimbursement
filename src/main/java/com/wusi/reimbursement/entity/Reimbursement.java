package com.wusi.reimbursement.entity;

import com.wusi.reimbursement.common.Identifiable;
import lombok.Data;

@Data
public class Reimbursement implements Identifiable<Long> {

  private Long id;
  private String productName;
  private String totalPrice;
  private String buyChannel;
  private java.sql.Timestamp buyDate;
  private java.sql.Timestamp reimbursementDate;
  private String remitDate;
  private String remark;




}
