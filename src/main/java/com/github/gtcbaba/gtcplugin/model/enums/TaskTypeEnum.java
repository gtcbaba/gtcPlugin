package com.github.gtcbaba.gtcplugin.model.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务类型枚举
 * 需求 = 1，缺陷 = 2
 *
 * @author pine
 */
@Getter
public enum TaskTypeEnum {

    DEMAND("需求", 1),
    BUG("缺陷", 2),
    ;

    private final String taskType;
    private final long value;

    TaskTypeEnum(String taskType, long value) {
        this.taskType = taskType;
        this.value = value;
    }

    /**
     * 获取值列表
     */
    public static List<Long> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 text 获取枚举
     */
    public static TaskTypeEnum getEnumByText(String text) {
        if (StrUtil.isBlank(text)) {
            return null;
        }
        for (TaskTypeEnum item : TaskTypeEnum.values()) {
            if (item.taskType.equals(text)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 根据 value 获取枚举
     */
    public static TaskTypeEnum getEnumByValue(Long value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (TaskTypeEnum taskTypeEnum : TaskTypeEnum.values()) {
            if (taskTypeEnum.value == value) {
                return taskTypeEnum;
            }
        }
        return null;
    }
}
