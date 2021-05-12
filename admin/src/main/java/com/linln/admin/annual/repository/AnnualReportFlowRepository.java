package com.linln.admin.annual.repository;

import com.linln.admin.annual.domain.AnnualReportFlow;
import com.linln.modules.system.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author why
 * @date 2020/10/26
 */
public interface AnnualReportFlowRepository extends BaseRepository<AnnualReportFlow, Long> {

    @Query(value="select * from per_annual_report_flow where receive_id=?1 and annual_report_id=?2 and state=?3",nativeQuery = true)
    AnnualReportFlow findAnnualReportFlowByReceiveIdAndAnnualReportIdAndState(Long receiveId, Long annualReportId, int state);

    @Query(value="select * from per_annual_report_flow where  annual_report_id=?1 ",nativeQuery = true)
    List<AnnualReportFlow> findAnnualReportFlowByAnnualReportId(Long annualReportId);


    @Query(value="select * from per_annual_report_flow where annual_report_id=?1 and state=?2 and receive_id is null order by update_date desc limit 1",nativeQuery = true)
    AnnualReportFlow findAnnualReportFlowByAnnualReportIdAndState(Long annualReportId, int state);

}