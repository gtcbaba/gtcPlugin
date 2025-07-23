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
public enum CodeTypeEnum {

    FRONTEND("前端开发", 1),
    BACKEND("后端开发", 2),
    ALGORITHM("算法开发", 3)
    ;

    private final String codeType;
    private final int value;

    CodeTypeEnum(String codeType, int value) {
        this.codeType = codeType;
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
    public static CodeTypeEnum getEnumByText(String text) {
        if (StrUtil.isBlank(text)) {
            return null;
        }
        for (CodeTypeEnum item : CodeTypeEnum.values()) {
            if (item.codeType.equals(text)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 根据 value 获取枚举
     */
    public static CodeTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (CodeTypeEnum codeTypeEnum : CodeTypeEnum.values()) {
            if (codeTypeEnum.value == value) {
                return codeTypeEnum;
            }
        }
        return null;
    }

    public static String getCodeTypeByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (CodeTypeEnum codeTypeEnum : CodeTypeEnum.values()) {
            if (codeTypeEnum.value == value) {
                return codeTypeEnum.codeType;
            }
        }
        return null;
    }
}
