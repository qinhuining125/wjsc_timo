package com.linln.admin.annual.controller;

import com.linln.admin.annual.domain.AnnualReport;
import com.linln.admin.annual.domain.AnnualReportFlow;
import com.linln.admin.annual.repository.AnnualReportRepository;
import com.linln.admin.annual.service.AnnualReportService;
import com.linln.admin.annual.validator.AnnualReportValid;
import com.linln.admin.performance.domain.QuarterDoc;
import com.linln.admin.performance.domain.QuarterResult;
import com.linln.admin.performance.service.QuarterResultService;
import com.linln.common.constant.AdminConst;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.component.fileUpload.FileUpload;
import com.linln.component.shiro.ShiroUtil;
import com.linln.modules.bigevent.domain.Bigevent;
import com.linln.modules.bigevent.service.BigeventService;
import com.linln.modules.standard.domain.StandardAdditional;
import com.linln.modules.standard.service.StandardAdditionalService;
import com.linln.modules.system.domain.Role;
import com.linln.modules.system.domain.Upload;
import com.linln.modules.system.domain.User;
import com.linln.modules.system.service.DeptService;
import com.linln.modules.system.service.UploadService;
import com.linln.modules.system.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author why
 * @date 2020/10/23
 */
@Controller
@RequestMapping("/annual/annualReport")
public class AnnualReportController {

    @Autowired
    private AnnualReportService annualReportService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private UserService userService;

    @Autowired
    private StandardAdditionalService standardAdditionalService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private BigeventService bigeventService;


    @Autowired
    private QuarterResultService quarterResultService;

    @Autowired
    private AnnualReportRepository annualReportRepository;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("annual:annualReport:index")
    public String index(Model model, AnnualReport annualReport,  Pageable pageable) {

        User sysUser= ShiroUtil.getSubject();
        Boolean isHRAnnualReport=false;
        Boolean isAdmin=false;
        Boolean isEmployee=false;

        if(sysUser.getId().equals(AdminConst.ADMIN_ID)){//管理员
            isAdmin=true;
        }else{
            Set<String> authRoleNames = sysUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

            if(authRoleNames.contains("hrAnnualReport")){//HR 人事局
                isHRAnnualReport=true;
            }

            if(authRoleNames.contains("employee")){//普通员工
                isEmployee=true;
            }
        }
        if(isEmployee){
            User user=new User();
            user.setId(sysUser.getId());
            annualReport.setCreateBy(user);
            annualReport.setUserId(sysUser.getId());
        }
        if(isHRAnnualReport){
            long receiveId=sysUser.getId();
            annualReport.setReceiveId(receiveId);
        }

        Page<AnnualReport> list = annualReportService.getPageList(annualReport,pageable);

        // 封装数据
        model.addAttribute("user", sysUser);
        model.addAttribute("isHRAnnualReport",isHRAnnualReport);
        model.addAttribute("isEmployee",isEmployee);
        model.addAttribute("deptList", deptService.getAllDeptListForQuery());
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/annual/annualReport/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("annual:annualReport:add")
    public String toAdd(Model model) {
        AnnualReport annualReport = new AnnualReport();
        annualReportService.save(annualReport);
        model.addAttribute("annualReport",annualReport);
        return "/annual/annualReport/add";
    }




    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("annual:annualReport:edit")
    public String toEdit(@PathVariable("id") AnnualReport annualReport, Model model) {
        model.addAttribute("annualReport", annualReport);
        return "/annual/annualReport/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"annual:annualReport:add", "annual:annualReport:edit"})
    @ResponseBody
    public ResultVo save(@Validated AnnualReportValid valid, AnnualReport annualReport) {


        // 复制保留无需修改的数据
        if (annualReport.getId() != null) {
            AnnualReport beAnnualReport = annualReportService.getById(annualReport.getId());
            EntityBeanUtil.copyProperties(beAnnualReport, annualReport);
            Upload up = uploadService.getUploadBySlipIdType(annualReport.getId(),annualReport.getSlipType());

            if(up!=null){
                annualReport.setFileName(up.getName());
                annualReport.setFilePath(up.getPath());
                annualReport.setFileUrl(up.getUrl());
            }else {
                //do nothing
            }
        }

        // 保存数据
        annualReportService.save(annualReport);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 提交年述职工作报告审核
     */
    @RequestMapping("/submitAudit")
    @ResponseBody
    public ResultVo submitAudit(
            @RequestParam(value = "id", required = false) Long id) {


        AnnualReport arNeedValid=annualReportRepository.findById(id).orElse(null);

        if(arNeedValid.getYear()==null){
            return ResultVoUtil.error("年为必填值，请填写后再提交");
        }

        AnnualReport arCheck= annualReportService.getOneByConditon(arNeedValid);

        if(arCheck!=null){
            return ResultVoUtil.error("提交年述职报告失败，系统中已经存在用户为"+
                    arNeedValid.getCreateBy().getNickname()+"，年为"+ arNeedValid.getYear() +"的年述职报告");
        }else {
            if (annualReportService.submitAudit(id)) {
                return ResultVoUtil.success("提交年述职工作报告成功");
            } else {
                return ResultVoUtil.error("提交年述职工作报告失败，请重新操作");
            }
        }
    }

    /**
     * 受理（即领取任务）任务职工作报告
     */
    @RequestMapping("/takeTask")
    @ResponseBody
    public ResultVo takeTask(
            @RequestParam(value = "id", required = false) Long id) {

        if (annualReportService.takeTask(id)) {
            return ResultVoUtil.success("受理年述职工作报告成功");
        } else {
            return ResultVoUtil.error("受理年述职工作报告失败，请重新操作");
        }
    }


    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("annual:annualReport:detail")
    public String toDetail(@PathVariable("id") AnnualReport annualReport, Model model) {

//        long createById = annualReport.getCreateBy().getId();
//        int year = annualReport.getYear();
//
//        //需要增加年
//        List<Bigevent> bigevents = bigeventService.getAllBigeventList(createById,year);
//
//        Map<String, String> userMap = userService.getUserNameByUserIdMap();
//
//        //当年所有季度的考核结果
//        List<QuarterResult> quarterResults = quarterResultService.getQuarterResultList(createById,year);
//
//        // 获取指定用户的加减分项列表
//        Set<StandardAdditional> standardAdditionals = annualReport.getStandardAdditionals();
//
//        List<AnnualReportFlow> annualReportFlows = annualReportService.getApprovalDetail(annualReport.getId());
//
//        model.addAttribute("userMap", userMap);
//        model.addAttribute("annualReportFlows", annualReportFlows);
        model.addAttribute("annualReport",annualReport);
//        model.addAttribute("bigevents", bigevents);
//        model.addAttribute("quarterResults", quarterResults);
//        model.addAttribute("standardAdditionals",standardAdditionals);
        return "/annual/annualReport/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("annual:annualReport:status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (annualReportService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }


    /**
     * 跳转到选择加减分项页面  跳转到审批页面
     */
    @GetMapping("/standardAdditional/{id}")
//    @RequiresPermissions("annual:annualReport:standardAdditional")
    public String toApproval(@PathVariable("id") AnnualReport annualReport, Model model) {

//        long createById = annualReport.getCreateBy().getId();
//        int year = annualReport.getYear();
//
//        Map<String, String> userMap = userService.getUserNameByUserIdMap();
//
//        //所有状态下的大事件
//        List<Bigevent> bigeventList = bigeventService.getAllBigeventList(createById,year);
//
//        //当年所有季度的考核结果
//        List<QuarterResult> quarterResults = quarterResultService.getQuarterResultList(createById,year);
//
//        // 获取指定用户的加减分项列表
//        Set<StandardAdditional> standardAdditionals = annualReport.getStandardAdditionals();
//        // 获取全部加减分列表
//        List<StandardAdditional> list = standardAdditionalService.getAllStandardAdditionalList();
//
//        model.addAttribute("userMap", userMap);
//        model.addAttribute("bigeventList", bigeventList);
//        model.addAttribute("quarterResults", quarterResults);
//        model.addAttribute("annualReport", annualReport);
//        model.addAttribute("list", list);
//        model.addAttribute("standardAdditionals", standardAdditionals);
        return "/annual/annualReport/standardAdditional";
    }

    /**
     * 保存加减分信息
     */
    @PostMapping("/standardAdditional")
    @ResponseBody
    public ResultVo standardAdditional(
            @RequestParam(value = "id", required = true) AnnualReport annualReport, @RequestParam String remarkAdvice,
            @RequestParam(value = "standardAdditionalId", required = false) HashSet<StandardAdditional> standardAdditionals) {
//
//        // 设置加减分项
//        annualReport.setStandardAdditionals(standardAdditionals);
//
//        //计算总分值
//        double ttScore = standardAdditionals.stream().mapToDouble((StandardAdditional a) -> (a.getAddSub().equals("+")) ? a.getScore() : -a.getScore()).sum();
//        annualReport.setTtlScore(ttScore);
//
//
//
//        if(annualReport.getStatus()!=1) {//需要检查状态
//            return ResultVoUtil.error(401,"非法操作：状态有误");
//        }
//
//        annualReport.setState(2);//
//        annualReportService.updateState(annualReport, ShiroUtil.getSubject().getId(),remarkAdvice);
//        // 保存数据
//        annualReportService.save(annualReport);

        return ResultVoUtil.AGREE_SUCCESS;
    }

    @PostMapping("/upload")
    @RequiresPermissions({"annual:annualReport:add", "annual:annualReport:edit"})
    @ResponseBody
    public ResultVo uploadFile(@RequestParam(value = "file", required = false) MultipartFile file,
                               AnnualReport annualReport) throws IOException {

        // 创建Upload实体对象
        Upload upload = FileUpload.getFile(file, "/file");

        // 保存文件到指定路径
        file.transferTo(FileUpload.getDestFile(upload));

        upload.setSlipId(annualReport.getId());
        upload.setSlipType(annualReport.getSlipType());

        uploadService.deleteUploadBySlipIdAndSlipType(annualReport.getId(),annualReport.getSlipType());
        // 保存文件上传信息
        uploadService.save(upload);

        return ResultVoUtil.success(upload);

    }

}