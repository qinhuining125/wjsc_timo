package com.linln.admin.annual.validator;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author why
 * @date 2020/10/23
 */
@Data
public class AnnualReportValid implements Serializable {
    @NotNull(message = "年不能为空,且必须为数字")
    private Integer year;
    @NotEmpty(message = "年述职报告不能为空")
    private String content;
}