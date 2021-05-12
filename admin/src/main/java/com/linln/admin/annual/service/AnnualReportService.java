package com.linln.admin.annual.service;

import com.linln.admin.annual.domain.AnnualReport;
import com.linln.admin.annual.domain.AnnualReportFlow;
import com.linln.admin.performance.domain.QuarterDoc;
import com.linln.common.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author why
 * @date 2020/10/23
 */
public interface AnnualReportService {

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<AnnualReport> getPageList(AnnualReport annualReport, Pageable pageable);

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    AnnualReport getById(Long id);

    /**
     * 保存数据
     * @param annualReport 实体对象
     */
    AnnualReport save(AnnualReport annualReport);


    /**
     * 提交年述职工作报告审核
     */
    @Transactional
    Boolean submitAudit(Long id);


    /**
     * 受理，领取任务
     * @param id
     * @return
     */
    @Transactional
    Boolean takeTask(Long id) ;

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);


    /**
     * 更新审批状态
     */
    @Transactional
    Boolean updateState(AnnualReport annualReport, long receiveId, String remark);


    /**
     * 取到审批详情信息
     */
    List<AnnualReportFlow> getApprovalDetail(Long id);


    /**
     * 根据情况获取年述职报告
     */
    AnnualReport getOneByConditon(AnnualReport annualReport);

}