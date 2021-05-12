package com.linln.admin.annual.service.impl;

import com.linln.admin.annual.domain.AnnualReport;
import com.linln.admin.annual.domain.AnnualReportFlow;
import com.linln.admin.annual.repository.AnnualReportFlowRepository;
import com.linln.admin.annual.repository.AnnualReportRepository;
import com.linln.admin.annual.service.AnnualReportService;
import com.linln.admin.performance.domain.QuarterDoc;
import com.linln.common.data.PageSort;
import com.linln.common.enums.StatusEnum;
import com.linln.component.shiro.ShiroUtil;
import com.linln.modules.system.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.List;

/**
 * @author why
 * @date 2020/10/23
 */
@Service
public class AnnualReportServiceImpl implements AnnualReportService {

    @Autowired
    private AnnualReportRepository annualReportRepository;

    @Autowired
    private AnnualReportFlowRepository annualReportFlowRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public AnnualReport getById(Long id) {
        return annualReportRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @return 返回分页数据
     */
    @Override
    public Page<AnnualReport> getPageList(AnnualReport annualReport, Pageable pageable){

        if(!StringUtils.isEmpty(annualReport.getContent())){
            String content="%"+annualReport.getContent()+"%";
            annualReport.setContent(content);
        }
        return annualReportRepository.findAllPage2(annualReport,PageSort.pageRequest2());

    }

    /**
     * 保存数据
     * @param annualReport 实体对象
     */
    @Override
    public AnnualReport save(AnnualReport annualReport) {
        return annualReportRepository.save(annualReport);
    }

    @Override
    @Transactional
    public Boolean submitAudit(Long id) {
        boolean flag=true;
        try{

//            AnnualReportFlow annualReportFlow=new AnnualReportFlow();
//            annualReportFlow.setAnnualReportId(id);
//            annualReportFlow.setState(5);//表示待受理
//            annualReportFlowRepository.save(annualReportFlow);

            //换另外一个方法，不再继续使用这个状态值来表示删除
            AnnualReport ar=annualReportRepository.findById(id).orElse(null);
            ar.setState(2);//表示已提交，去掉了审批流程，默认提交完毕
            annualReportRepository.saveAndFlush(ar);
        }catch(Exception e){
            System.out.println(e.getMessage());
            flag=false;
        }finally{
            return flag;
        }
    }

    @Override
    @Transactional
    public Boolean takeTask(Long id) {
        boolean flag=true;
        try{
            User sysUser= ShiroUtil.getSubject();

            Long receiveId=sysUser.getId();

            AnnualReportFlow annualReportFlow = new AnnualReportFlow();
            annualReportFlow.setAnnualReportId(id);
            annualReportFlow.setReceiveId(receiveId);
            annualReportFlow.setState(1);//表示待审核
            annualReportFlowRepository.save(annualReportFlow);

            //换另外一个方法，不再继续使用这个状态值来表示删除
            AnnualReport ar=annualReportRepository.findById(id).orElse(null);
            ar.setActReceiveId(receiveId);//设置实际的接收人
            ar.setState(1);//表示待审核
            annualReportRepository.saveAndFlush(ar);
        }catch(Exception e){
            System.out.println(e.getMessage());
            flag=false;
        }finally{
            return flag;
        }
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return annualReportRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }

    /**
     * 进行审批操作
     */
    @Override
    @Transactional
    public Boolean updateState(AnnualReport annualReport,long receiveId,String remark) {
        boolean flag=false;
        try{
            AnnualReport ar=annualReportRepository.findById(annualReport.getId()).orElse(null);
            ar.setState(annualReport.getState());
            annualReportRepository.saveAndFlush(ar);
            AnnualReportFlow arflow = new AnnualReportFlow();
            arflow.setAnnualReportId(annualReport.getId());
            arflow.setReceiveId(receiveId);
            arflow.setState(annualReport.getState());
            arflow.setRemark(remark);
            annualReportFlowRepository.saveAndFlush(arflow);
            flag=true;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }finally{
            return flag;
        }
    }

    @Override
    public List<AnnualReportFlow> getApprovalDetail(Long id) {
        return annualReportFlowRepository.findAnnualReportFlowByAnnualReportId(id);
    }


    @Override
    public AnnualReport getOneByConditon(AnnualReport annualReport) {
        return annualReportRepository.findByCreateByAndStateAndYear(annualReport);
    }

}