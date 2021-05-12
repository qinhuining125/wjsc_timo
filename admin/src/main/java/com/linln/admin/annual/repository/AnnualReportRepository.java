package com.linln.admin.annual.repository;

import com.linln.admin.annual.domain.AnnualReport;
import com.linln.admin.performance.domain.QuarterDoc;
import com.linln.modules.system.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

/**
 * @author why
 * @date 2020/10/23
 */
public interface AnnualReportRepository extends BaseRepository<AnnualReport, Long> {




    @Query(nativeQuery = true, value = "SELECT doc.* FROM per_quarter_doc doc " +
            "left join sys_user su on doc.create_by=su.id " +
            "left join sys_dept dept on su.dept_id=dept.id " +
            "where doc.status<>3 " +
            "and if(:#{#annualReport.content}!= '', doc.content like :#{#annualReport.content},1=1) " +
            "and if(:#{#annualReport.state}!= '',doc.state=:#{#annualReport.state},1=1) " +
            "and if(:#{#annualReport.year}!= '',doc.year=:#{#annualReport.year},1=1) " +
//            "and if(:#{#annualReport.quarter}!=0,doc.quarter=:#{#quarterDoc.quarter},1=1) " +
            "and if(:#{#annualReport.dept}!=0,dept.id=:#{#annualReport.dept},1=1) ",
//            "and if(:#{#annualReport.receiveId}!= '', doc.id in (select annual_report_id from per_annual_report_flow where receive_id=:#{#annualReport.receiveId}),1=1)",
            countQuery = "SELECT count(*) FROM per_annual_report  where status<>3 " +
                    "and if(:#{#annualReport.content}!= '',content like :#{#annualReport.content},1=1) " +
                    "and if(:#{#annualReport.createBy.id}!= '',create_by=:#{#annualReport.createBy.id},1=1)  " )
//                    "and if(:#{#annualReport.receiveId}!= '', id in (select annual_report_id from per_annual_report_flow where receive_id=:#{#annualReport.receiveId}),1=1) ")
    Page<AnnualReport> findAllPage(AnnualReport annualReport, Pageable pageable);


    @Query(nativeQuery = true, value = "SELECT doc.* FROM per_annual_report doc " +
            "left join sys_user su on doc.create_by=su.id " +
            "left join sys_dept dept on su.dept_id=dept.id " +
            "where doc.status<>3 " +
            "and if(:#{#annualReport.content}!= '', doc.content like :#{#annualReport.content},1=1) " +
            "and if(:#{#annualReport.state}!= '',doc.state=:#{#annualReport.state},1=1) " +
            "and if(:#{#annualReport.year}!= '',doc.year=:#{#annualReport.year},1=1) " +
            "and if(:#{#annualReport.userId} !='', doc.create_by=:#{#annualReport.userId},1=1)  " +
            "and if(:#{#annualReport.dept}!=0,dept.id=:#{#annualReport.dept},1=1) " +
            "and if(:#{#annualReport.receiveId}!='', doc.state in (1,2,5), 1=1 ) " ,
//            "and if(:#{#annualReport.receiveId}!= '', doc.id in (select annual_report_id from per_annual_report_flow where receive_id=:#{#annualReport.receiveId}),1=1)",
            countQuery = "SELECT count(*) FROM per_annual_report  where status<>3 " +
                    "and if(:#{#annualReport.content}!= '',content like :#{#annualReport.content},1=1) " +
                    "and if(:#{#annualReport.createBy.id}!= '',create_by=:#{#annualReport.createBy.id},1=1)  " +
                    "and if(:#{#annualReport.receiveId}!= '', id in (select annual_report_id from per_annual_report_flow where receive_id=:#{#annualReport.receiveId}),1=1) ")
    Page<AnnualReport> findAllPage2(AnnualReport annualReport,Pageable pageable);




    @Query(value= "select * from per_annual_report  where create_by=:#{#annualReport.createBy.id} " +
                    "and status<>3 and state=2 and year=:#{#annualReport.year}", nativeQuery=true)
    AnnualReport findByCreateByAndStateAndYear(AnnualReport annualReport);


}