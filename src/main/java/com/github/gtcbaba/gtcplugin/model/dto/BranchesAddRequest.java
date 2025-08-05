package com.github.gtcbaba.gtcplugin.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BranchesAddRequest implements Serializable {

//    /**
//     * 分支名
//     */
//    private String branchName;
//
//    /**
//     * 应用名
//     */
//    private String appName;

    private List<AppAndBranch> appAndBranches;


    /**
     * 创建分支的用户id
     */
    private Long userId;

    /**
     * 所属任务id
     */
    private Long taskId;


    private static final long serialVersionUID = 1L;
}