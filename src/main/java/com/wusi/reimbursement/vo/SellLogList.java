package com.wusi.reimbursement.vo;


import com.wusi.reimbursement.common.Identifiable;
import lombok.Data;

import java.util.Date;

@Data
public class SellLogList implements Identifiable<Long> {

  private Long id;
  private String product;
  private String buyerName;
  private String myOrderNo;
  private long sellMoney;
  private String amyOrderNo;
  private long buyMoney;
  private long profit;
  private long refund;
  private String remark;
  private String orderDate;



}
