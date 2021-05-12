package com.linln.admin.annual.service.impl;

import com.linln.admin.annual.domain.AnnualReportFlow;
import com.linln.admin.annual.repository.AnnualReportFlowRepository;
import com.linln.admin.annual.service.AnnualReportFlowService;
import com.linln.common.data.PageSort;
import com.linln.common.enums.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author why
 * @date 2020/10/26
 */
@Service
public class AnnualReportFlowServiceImpl implements AnnualReportFlowService {

    @Autowired
    private AnnualReportFlowRepository annualReportFlowRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public AnnualReportFlow getById(Long id) {
        return annualReportFlowRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<AnnualReportFlow> getPageList(Example<AnnualReportFlow> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return annualReportFlowRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param annualReportFlow 实体对象
     */
    @Override
    public AnnualReportFlow save(AnnualReportFlow annualReportFlow) {
        return annualReportFlowRepository.save(annualReportFlow);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return annualReportFlowRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}