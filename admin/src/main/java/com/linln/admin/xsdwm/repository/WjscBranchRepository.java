package com.linln.admin.xsdwm.repository;

import com.linln.admin.xsdwm.domain.Wjsc;
import com.linln.admin.xsdwm.domain.WjscBranch;
import com.linln.modules.dbigevent.domain.Dbigevent;
import com.linln.modules.system.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author 秦惠宁
 * @date 2021/04/16
 */
public interface WjscBranchRepository extends BaseRepository<WjscBranch, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM xsdwm_wjsc_branch where  status<>3 " +
            "and if(:#{#wjscBranch.task}!= '',task = :#{#wjscBranch.task},1=1) " +
            "and if(:#{#wjscBranch.liable}!= '',liable=:#{#wjscBranch.liable},1=1) " +
            "and if(:#{#wjscBranch.dataState}!= '',data_state = :#{#wjscBranch.dataState},1=1) ",
            countQuery = "SELECT count(*) FROM xsdwm_wjsc_branch  where  status<>3 " +
                    "and if(:#{#wjscBranch.task}!= '',task = :#{#wjscBranch.task},1=1) " +
                    "and if(:#{#wjscBranch.liable}!= '',liable=:#{#wjscBranch.liable},1=1)  " +
                    "and if(:#{#wjscBranch.dataState}!= '',data_state = :#{#wjscBranch.state},1=1)  ")
    Page<WjscBranch> findAllPage(WjscBranch wjscBranch, Pageable pageable);
    @Query(nativeQuery = true, value = "SELECT * FROM xsdwm_wjsc_branch where  status<>3 and data_state<> '审批通过'" +
            "and if(:#{#wjscBranch.task}!= '',task = :#{#wjscBranch.task},1=1) " +
            "and if(:#{#wjscBranch.liable}!= '',liable=:#{#wjscBranch.liable},1=1)  ",
            countQuery = "SELECT count(*) FROM xsdwm_wjsc_branch  where  status<>3  and data_state<> '审批通过'" +
                    "and if(:#{#wjscBranch.task}!= '',task = :#{#wjscBranch.task},1=1) " +
                    "and if(:#{#wjscBranch.liable}!= '',liable=:#{#wjscBranch.liable},1=1)  ")
    Page<WjscBranch> findAllNoStatePage(WjscBranch wjscBranch, Pageable pageable);
    @Query(value = "SELECT * FROM xsdwm_wjsc_branch where task=?1", nativeQuery = true)
    List<WjscBranch> getWjscBranchByTask(Long id);
}