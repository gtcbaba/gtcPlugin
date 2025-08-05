package com.github.gtcbaba.gtcplugin.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BranchesSearchRequest implements Serializable {

    private Long taskId;

//    private Long appId;

    private Long userId;

    private static final long serialVersionUID = 1L;
}
