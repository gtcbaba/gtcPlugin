package com.github.gtcbaba.gtcplugin.model.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class BranchVO implements Serializable {

    private Long id;
    private String appName;
    private String branchName;
    private String gitCommand;
    private Integer isMine;

    private static final long serialVersionUID = 1L;
}
