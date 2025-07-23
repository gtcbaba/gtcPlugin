package com.github.gtcbaba.gtcplugin.model.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务
 */
@Data
public class Task implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 类型（需求：1/缺陷：2）
     */
    private Integer taskType;

    /**
     * 名称（如修复.../开发...）
     */
    private String taskName;
    /**
     * 具体任务类型（如 前端开发/后端开发/算法）
     */
    private Integer codeType;

    /**
     * 任务状态 （如 待开发/开发中/开发完成）
     */
    private Integer status;

    /**
     * 排期
     */
    private Date scheduledTime;

}
