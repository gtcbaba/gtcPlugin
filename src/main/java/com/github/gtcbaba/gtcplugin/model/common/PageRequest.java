package com.github.gtcbaba.gtcplugin.model.common;


import com.github.gtcbaba.gtcplugin.constant.PageConstant;
import lombok.Data;

/**
 * 分页请求
 *
 * @author pine
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private long current = 1;

    /**
     * 页面大小
     */
    // private long pageSize = Objects.requireNonNull(GlobalState.getInstance().getState()).pageSize;
    private long pageSize = PageConstant.PAGE_SIZE;

}
