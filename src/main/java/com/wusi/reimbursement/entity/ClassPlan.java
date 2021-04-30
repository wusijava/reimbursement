package com.wusi.reimbursement.entity;

import com.wusi.reimbursement.common.Identifiable;
import lombok.Data;

import java.util.Date;

/**
 * 排班计划
 */
@Data
public class ClassPlan implements Identifiable<Long> {

  private Long id;
  /**
   * 排班日期
   */
  private String date;
  /**
   * 员工姓名
   */
  private String name;
  /**
   * 班次0休息1早班2插班3晚班
   */
  private Integer classes;
  /**
   * 创建时间
   */
  private Date cteateTime;

  public enum Classes {
    xiuxi(0, "休息"),
    zao(1, "早班"),
    cha(3, "插班"),
    wan(2, "晚班");

    private Integer code;
    private String desc;

    Classes(Integer code, String desc) {
      this.code = code;
      this.desc = desc;
    }
    public Integer getCode() {
      return code;
    }

    public void setCode(Integer code) {
      this.code = code;
    }

    public String getDesc() {
      return desc;
    }

    public void setDesc(String desc) {
      this.desc = desc;
    }
  }
  public String getClassPlanDesc() {
    if (Classes.xiuxi.getCode().equals(classes)) {
      return "休息";
    } else if(Classes.zao.getCode().equals(classes)) {
      return "早班";
    }else if (Classes.cha.getCode().equals(classes)){
      return "插班";
    }else{
      return "晚班";
    }
  }

}
