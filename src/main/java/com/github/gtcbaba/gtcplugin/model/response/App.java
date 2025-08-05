package com.github.gtcbaba.gtcplugin.model.response;
import lombok.Data;

import java.io.Serializable;

@Data
public class App implements Serializable {
    /**
     * 应用id
     */
    private Long id;

    /**
     * 应用名
     */
    private String appName;

    /**
     * 所属代码仓库id
     */
    private Long codeRepositoryId;

    /**
     * 所属代码仓库名
     */
    private String codeRepositoryName;

    private static final long serialVersionUID = 1L;
}