package com.github.gtcbaba.gtcplugin.model.dto;


import com.github.gtcbaba.gtcplugin.constant.CommonConstant;
import com.github.gtcbaba.gtcplugin.model.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class TaskTypeQueryRequest extends PageRequest implements Serializable {

    /**
     * 任务分类id 类型（需求：1/缺陷：2） 默认为0表示都查
     */
    private Long taskType = CommonConstant.DEFAULT_TASK_TYPE_CATEGORY_ID;

    /**
     * 需求/缺陷 名称
     */
    private String taskName;

    /**
     * 同时模糊查询 任务名称 和 任务类型
     */
    private String searchText;

    /**
     * 关联用户id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
