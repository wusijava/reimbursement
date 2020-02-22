package com.wusi.reimbursement.entity;


import com.wusi.reimbursement.common.Identifiable;
import lombok.Data;

import java.util.Date;

@Data
public class SellLog implements Identifiable<Long> {

  private Long id;
  private String product;
  private String buyerName;
  private String myOrderNo;
  private Integer sellMoney;
  private String amyOrderNo;
  private Integer buyMoney;
  private Integer profit;
  private Integer refund;
  private String remark;
  private Date orderDate;
  private String url;



}
