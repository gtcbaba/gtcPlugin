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
public enum DevelopStatusEnum {

    TODO("待开发", 1),
    DOING("开发中", 2),
    DONE("开发完成", 3)
    ;

    private final String developStatus;
    private final int value;

    DevelopStatusEnum(String developStatus, int value) {
        this.developStatus = developStatus;
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
    public static DevelopStatusEnum getEnumByText(String text) {
        if (StrUtil.isBlank(text)) {
            return null;
        }
        for (DevelopStatusEnum item : DevelopStatusEnum.values()) {
            if (item.developStatus.equals(text)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 根据 value 获取枚举
     */
    public static DevelopStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (DevelopStatusEnum developStatusEnum : DevelopStatusEnum.values()) {
            if (developStatusEnum.value == value) {
                return developStatusEnum;
            }
        }
        return null;
    }

    public static String getDevelopStatusByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (DevelopStatusEnum developStatusEnum : DevelopStatusEnum.values()) {
            if (developStatusEnum.value == value) {
                return developStatusEnum.developStatus;
            }
        }
        return null;
    }
}
