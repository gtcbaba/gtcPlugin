package com.github.gtcbaba.gtcplugin.model.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class AppAndBranch {

    private Long appId;
    private String appName;
    private String branchName;
    private Long baseOnBranchId;
    private String baseOnBranchName;

    public String checkMyFields() {
        if (StringUtils.isBlank(appName)){
            return "请选择应用！";
        }
        if (StringUtils.isBlank(branchName)){
            return "新分支名不能为空！";
        }
        if (StringUtils.isBlank(baseOnBranchName)){
            return "请选择源分支！";
        }
        return null;
    }
}