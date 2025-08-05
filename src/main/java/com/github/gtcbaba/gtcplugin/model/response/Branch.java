package com.github.gtcbaba.gtcplugin.model.response;

import lombok.Data;

import java.io.Serializable;


@Data
public class Branch implements Serializable {
    /**
     * 应用id
     */
    private Long id;

    /**
     * 分支名
     */
    private String branchName;

    /**
     * 应用 id
     */
    private Long appId;
    /**
     * 应用名
     */
    private String appName;

    /**
     * 创建分支的用户id
     */
    private Long userId;

    /**
     * 所属任务id
     */
    private Long taskId;

    /**
     * 被其他用户关联
     */
    private String link;

    private static final long serialVersionUID = 1L;
}