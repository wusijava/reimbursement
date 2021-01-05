package com.wusi.reimbursement.entity;

import com.wusi.reimbursement.common.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * 购买的ssq
 */
@Data
public class Ssq implements Identifiable<Long> {

  private Long id;
  /**
   * 期数
   */
  private String term;
  /**
   * 红
   */
  private String red1;
  private String red2;
  private String red3;
  private String red4;
  private String red5;
  private String red6;
  private String blue;
  /**
   * 金额
   */
  private String bonus;
  private Date createTime;
  /**
   * 开奖时间
   */
  private Date bonusTime;
  /**
   * 是否
   */
  private String isBonus;
  /**
   * 是否领取
   */
  private String isReceive;
  /**
   * 星期
   */
  private String week;
  /**
   * 注数
   */
  private String num;
  /**
   * 红
   */
  private String redNum;
  /**
   * 蓝
   */
  private String blueNum;



}
