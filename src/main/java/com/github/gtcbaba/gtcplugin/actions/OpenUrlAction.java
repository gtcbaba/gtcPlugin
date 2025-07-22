package com.github.gtcbaba.gtcplugin.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author pine
 */
public class OpenUrlAction extends AnAction implements DumbAware {

    private final String url;

    // 构造函数
    public OpenUrlAction(String text, String url, Icon icon) {
        // Action 名称
        super(text, text, icon);
        this.url = url;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 使用默认浏览器打开指定网址
        BrowserUtil.browse(url);
    }
}
