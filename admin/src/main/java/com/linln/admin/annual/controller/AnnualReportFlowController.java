package com.linln.admin.annual.controller;

import com.linln.admin.annual.domain.AnnualReportFlow;
import com.linln.admin.annual.service.AnnualReportFlowService;
import com.linln.admin.annual.validator.AnnualReportFlowValid;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author why
 * @date 2020/10/26
 */
@Controller
@RequestMapping("/annual/annualReportFlow")
public class AnnualReportFlowController {

    @Autowired
    private AnnualReportFlowService annualReportFlowService;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("annual:annualReportFlow:index")
    public String index(Model model, AnnualReportFlow annualReportFlow) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();

        // 获取数据列表
        Example<AnnualReportFlow> example = Example.of(annualReportFlow, matcher);
        Page<AnnualReportFlow> list = annualReportFlowService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/annual/annualReportFlow/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("annual:annualReportFlow:add")
    public String toAdd() {
        return "/annual/annualReportFlow/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("annual:annualReportFlow:edit")
    public String toEdit(@PathVariable("id") AnnualReportFlow annualReportFlow, Model model) {
        model.addAttribute("annualReportFlow", annualReportFlow);
        return "/annual/annualReportFlow/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"annual:annualReportFlow:add", "annual:annualReportFlow:edit"})
    @ResponseBody
    public ResultVo save(@Validated AnnualReportFlowValid valid, AnnualReportFlow annualReportFlow) {
        // 复制保留无需修改的数据
        if (annualReportFlow.getId() != null) {
            AnnualReportFlow beAnnualReportFlow = annualReportFlowService.getById(annualReportFlow.getId());
            EntityBeanUtil.copyProperties(beAnnualReportFlow, annualReportFlow);
        }

        // 保存数据
        annualReportFlowService.save(annualReportFlow);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("annual:annualReportFlow:detail")
    public String toDetail(@PathVariable("id") AnnualReportFlow annualReportFlow, Model model) {
        model.addAttribute("annualReportFlow",annualReportFlow);
        return "/annual/annualReportFlow/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("annual:annualReportFlow:status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (annualReportFlowService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }
}