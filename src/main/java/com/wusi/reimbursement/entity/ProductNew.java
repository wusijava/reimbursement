package com.wusi.reimbursement.entity;

import com.wusi.reimbursement.common.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * 吴思 2020-12-14 淘宝产品实体类
 */
@Data
public class ProductNew implements Identifiable<Long> {

  private Long id;
  /**
   * 产品型号
   */
  private String model;
  /**
   * 爱美链接
   */
  private String amyUrl;
  /**
   * 我的链接
   */
  private String myUrl;
  /**
   * 爱美状态
   */
  private String amyState;
  /**
   * 我的状态
   */
  private String myState;
  /**
   * 创建时间
   */
  private Date createTime;
  /**
   * 产品图片url
   */
  private String image;


}
