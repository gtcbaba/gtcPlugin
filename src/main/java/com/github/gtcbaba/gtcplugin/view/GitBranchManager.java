package com.github.gtcbaba.gtcplugin.view;

import com.github.gtcbaba.gtcplugin.utils.ContentUtil;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;

public class GitBranchManager
{

    public void addGitTab(Long taskId, String taskName, Project project) {

        // 新建 tab 页
        JBPanel<?> newTabPanel = new JBPanel<>(new BorderLayout());
        ContentUtil.createContent(newTabPanel, taskName, false, project);

        JButton jButton = new JButton(taskId.toString());
//        jButton.setIcon(new ImageIcon("/Users/ybbj-1100659/IdeaProjects/gtcwork/gtcPlugin/src/main/resources/icons/yuanbao.png"));
//        // 搜索条
//        JPanel searchPanel = this.getSearchPanel();
//        newTabPanel.add(searchPanel, BorderLayout.NORTH);
//
//        // 数据表格
//        this.getDataPanel(project, newTabPanel);
//
//        // 分页条
//        paginationPanel = new JBPanel<>(new GridBagLayout());
        newTabPanel.add(jButton, BorderLayout.SOUTH);

    }
}
