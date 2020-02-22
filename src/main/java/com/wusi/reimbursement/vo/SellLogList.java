package com.wusi.reimbursement.vo;


import com.wusi.reimbursement.common.Identifiable;
import lombok.Data;

@Data
public class SellLogList implements Identifiable<Long> {

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
  private String orderDate;
  private String url;


}
