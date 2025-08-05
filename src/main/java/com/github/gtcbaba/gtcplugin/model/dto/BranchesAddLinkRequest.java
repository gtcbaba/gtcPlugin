package com.github.gtcbaba.gtcplugin.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class BranchesAddLinkRequest implements Serializable {

    /**
     * 要创建关联的所有分支
     */
    private List<Long> branchIds;

    /**
     * 要创建关联复用这个分支的用户
     */
    private Long userId;

    /**
     * 所属任务id
     */
    private Long taskId;


    private static final long serialVersionUID = 1L;
}