package com.abt.salary.entity;

import com.abt.common.model.AuditInfo;
import com.abt.salary.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sl_enc")
@DynamicInsert
@DynamicUpdate
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
public class SalaryEnc {
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;
  
  @Column(name="pwd_", columnDefinition="VARCHAR(500)")
  private String pwd;

  /**
   * 是否是初始设置
   */
  @Column(name="is_first", columnDefinition="BIT")
  private boolean isFirst = true;

  @Column(name="emp_num", columnDefinition="VARCHAR(32)")
  private String jobNumber;

  /**
   * 上次修改时间
   */
  @Column(name="last_update_time")
  @LastModifiedDate
  private LocalDateTime lastUpdateTime;

  @Column(name="last_update_userid", columnDefinition="VARCHAR(128)")
  @LastModifiedBy
  private String lastUpdateUserid;



  /**
   * 状态
   */
  @Column(name="state_", columnDefinition="TINYINT")
  private int state = STATE_NORMAL;

  //正常
  public static final int STATE_NORMAL = 0;

  //锁定
  public static final int STATE_LOCK = 9;

  //重置
  public SalaryEnc reset(String pwd) {
    this.isFirst = true;
    this.pwd = pwd;
    return this;
  }

  public static SalaryEnc create(String jobNumber) {
    SalaryEnc enc = new SalaryEnc();
    enc.jobNumber = jobNumber;
    return enc;
  }



  

}