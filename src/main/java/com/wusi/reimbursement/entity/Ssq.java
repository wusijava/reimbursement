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
   * 0待开奖 1中奖 -1 未中奖
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

  public enum IsBonus {
    no("1", "未中奖"),
    waiting("0", "待开奖"),
    success("1", "已中奖");

    private String code;
    private String desc;

    IsBonus(String code, String desc) {
      this.code = code;
      this.desc = desc;
    }

    public String getCode() {
      return code;
    }

    public void setCode(String code) {
      this.code = code;
    }

    public String getDesc() {
      return desc;
    }

    public void setDesc(String desc) {
      this.desc = desc;
    }
  }
  public String getIsBonusDesc(){
    if (IsBonus.success.code.equals(isBonus)){
      return IsBonus.success.getDesc();
    }  else if (IsBonus.waiting.code.equals(isBonus)){
      return IsBonus.waiting.getDesc();
    }else {
      return IsBonus.no.getDesc();
    }
  }

}
