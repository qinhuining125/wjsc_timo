package com.linln.admin.annual.service;

import com.linln.admin.annual.domain.AnnualReportFlow;
import com.linln.common.enums.StatusEnum;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author why
 * @date 2020/10/26
 */
public interface AnnualReportFlowService {

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    Page<AnnualReportFlow> getPageList(Example<AnnualReportFlow> example);

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    AnnualReportFlow getById(Long id);

    /**
     * 保存数据
     * @param annualReportFlow 实体对象
     */
    AnnualReportFlow save(AnnualReportFlow annualReportFlow);

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Transactional
    Boolean updateStatus(StatusEnum statusEnum, List<Long> idList);
}