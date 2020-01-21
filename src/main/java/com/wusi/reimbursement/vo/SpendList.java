package com.wusi.reimbursement.vo;


import com.wusi.reimbursement.common.Identifiable;
import lombok.Data;

import java.util.Date;

@Data
public class SpendList implements Identifiable<Long> {

  private Long id;
  private String item;
  private double price;
  private String consumer;
  private String date;
  private String remark;




}
