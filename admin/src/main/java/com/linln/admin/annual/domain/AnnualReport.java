package com.linln.admin.annual.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.StatusUtil;
import com.linln.modules.system.domain.User;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 * @author why
 * @date 2020/10/23
 */
@Data
@Entity
@Table(name="per_annual_report")
@EntityListeners(AuditingEntityListener.class)
@Where(clause = StatusUtil.NOT_DELETE)
public class AnnualReport implements Serializable {
    public static final String SLIP_TYPE="60";
    // 主键ID
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String slipType="60";
    // 年
    private Integer year;
    // 年述职报告
    private String content;
    // 备注
    private String remark;
    // 创建时间
    @CreatedDate
    private Date createDate;
    // 更新时间
    @LastModifiedDate
    private Date updateDate;
    // 创建者
    @CreatedBy
    @ManyToOne(fetch=FetchType.LAZY)
    @NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name="create_by")
    @JsonIgnore
    private User createBy;
    // 更新者
    @LastModifiedBy
    @ManyToOne(fetch=FetchType.LAZY)
    @NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name="update_by")
    @JsonIgnore
    private User updateBy;
    // 数据状态
    private Byte status = StatusEnum.OK.getCode();


    private String fileName;

    private String filePath;

    private String fileUrl;

    // 状态
    // 状态 0:新建 1:已提交
    private Integer state =0 ;
//
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "sys_user_bigevent",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "bigevent_id"))
//    @JsonIgnore
//    private Set<Bigevent> bigevents = new HashSet<>(0);
//
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "sys_user_quarter_result",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "quarter_result_id"))
//    @JsonIgnore
//    private Set<QuarterResult> quarterResults = new HashSet<>(0);
//
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "sys_user_standardAdditional",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "standard_additional_id"))
//    @JsonIgnore
//    private Set<StandardAdditional> standardAdditionals = new HashSet<>(0);
//
//    // 总得分
//    private Double ttlScore;

    @Transient
    private long receiveId;

    private long actReceiveId;

    // 用户ID
    @Transient
    private Long userId;

    @Transient
    private long dept;

}