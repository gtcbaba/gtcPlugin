package com.github.gtcbaba.gtcplugin.actions;

import cn.hutool.core.util.BooleanUtil;
import com.github.gtcbaba.gtcplugin.config.ApiConfig;

import com.github.gtcbaba.gtcplugin.config.GlobalState;
import com.github.gtcbaba.gtcplugin.constant.IconConstant;
import com.github.gtcbaba.gtcplugin.constant.KeyConstant;
import com.github.gtcbaba.gtcplugin.model.common.BaseResponse;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;

import static com.github.gtcbaba.gtcplugin.constant.KeyConstant.LOGIN;
import static com.github.gtcbaba.gtcplugin.constant.KeyConstant.LOGIN_ZH;


/**
 * 注销
 *
 * @author pine
 */
public class LogoutAction extends AnAction implements DumbAware {

    private final DefaultActionGroup actionGroup;
    private JBPanel<?> mainPanel;

    // 构造函数
    public LogoutAction(String text, Icon icon, DefaultActionGroup actionGroup, JBPanel<?> mainPanel) {
        // Action 名称
        super(text, text, icon);
        this.actionGroup = actionGroup;
        this.mainPanel = mainPanel;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            // 1. 调用退出登录接口
            Boolean logout = null;
            int code = 0;
            try {
                BaseResponse<Boolean> response = ApiConfig.maXiaoBaoApi.userLogout().execute().body();
                code = response.getCode();
                logout = response.getData();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            if (code == 0 && BooleanUtil.isFalse(logout)) {
                return;
            }
            ApplicationManager.getApplication().invokeLater(() -> {
                // 2. 删除本地存储的登录态
                GlobalState globalState = GlobalState.getInstance();
                globalState.removeSavedToken();
                globalState.removeSavedUser();
                // 3. 更改 actionGroup
                ActionManager actionManager = ActionManager.getInstance();
                // 3.1 删除 注销
                AnAction logoutAction = actionManager.getAction(KeyConstant.LOGOUT);
                if (logoutAction == null) {
                    return;
                }
                actionGroup.remove(logoutAction);
                actionManager.unregisterAction(KeyConstant.LOGOUT);

                // 3.2 删除 用户图标
                AnAction userAction = actionManager.getAction(KeyConstant.IDP);
                if (userAction == null) {
                    return;
                }
                actionGroup.remove(userAction);
                actionManager.unregisterAction(KeyConstant.IDP);

                // 3.3 增加 登录
                LoginAction loginAction = new LoginAction(LOGIN_ZH, IconConstant.LOGIN, actionGroup, mainPanel);
                actionGroup.add(loginAction);
                actionManager.registerAction(LOGIN, loginAction);

                // 移除信息区域
                mainPanel.remove(1);
            });
        });
    }
}
