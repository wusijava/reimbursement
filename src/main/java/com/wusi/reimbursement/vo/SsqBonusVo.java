package com.wusi.reimbursement.vo;


import lombok.Data;

import java.util.List;


@Data
public class SsqBonusVo  {

  private Long id;
  private String term;
  private String red1;
  private String red2;
  private String red3;
  private String red4;
  private String red5;
  private String red6;
  private String blue;
  private String createTime;
  private String week;
  private List<SsqVo> list;



}
