package com.github.gtcbaba.gtcplugin.model.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 具体任务类型（如前端开发:1/后端开发:2/算法开发:3）
 *
 * @author pine
 */
@Getter
public enum ScheduledTimeFactorEnum {

    DAY("今日", 1),
    WEEK("本周", 2),
    MONTH("本月", 3),
    QUARTER("本季度", 4),
    NEXT_QUARTER("下季度", 5)
    ;

    private final String scheduledTimeFactor;
    private final int value;

    ScheduledTimeFactorEnum(String scheduledTimeFactor, int value) {
        this.scheduledTimeFactor = scheduledTimeFactor;
        this.value = value;
    }

    /**
     * 获取值列表
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 text 获取枚举
     */
    public static ScheduledTimeFactorEnum getEnumByText(String text) {
        if (StrUtil.isBlank(text)) {
            return null;
        }
        for (ScheduledTimeFactorEnum item : ScheduledTimeFactorEnum.values()) {
            if (item.scheduledTimeFactor.equals(text)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 根据 value 获取枚举
     */
    public static ScheduledTimeFactorEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ScheduledTimeFactorEnum scheduledTimeFactorEnum : ScheduledTimeFactorEnum.values()) {
            if (scheduledTimeFactorEnum.value == value) {
                return scheduledTimeFactorEnum;
            }
        }
        return null;
    }

    public static String getScheduledTimeFactorByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ScheduledTimeFactorEnum scheduledTimeFactorEnum : ScheduledTimeFactorEnum.values()) {
            if (scheduledTimeFactorEnum.value == value) {
                return scheduledTimeFactorEnum.scheduledTimeFactor;
            }
        }
        return null;
    }
}
