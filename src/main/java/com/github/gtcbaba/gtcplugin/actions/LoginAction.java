package com.github.gtcbaba.gtcplugin.actions;

import com.github.gtcbaba.gtcplugin.config.ApiConfig;
import com.github.gtcbaba.gtcplugin.config.GlobalState;
import com.github.gtcbaba.gtcplugin.model.response.User;
import com.github.gtcbaba.gtcplugin.utils.PanelUtil;
import com.github.gtcbaba.gtcplugin.view.LoginDialog;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.components.JBPanel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.IOException;

@Slf4j
public class LoginAction extends AnAction implements DumbAware {

    private final DefaultActionGroup actionGroup;

    private JBPanel<?> mainPanel;

    // 构造函数
    public LoginAction(String text, Icon icon, DefaultActionGroup actionGroup, JBPanel<?> mainPanel) {
        // Action 名称
        super(text, text, icon);
        this.actionGroup = actionGroup;
        this.mainPanel = mainPanel;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        LoginDialog loginDialog = new LoginDialog(ProjectManager.getInstance().getDefaultProject());
        loginDialog.show();
        // 可能登陆成功了 也可能登陆失败或者直接关闭了
        // 具体要看登陆态来判断
        GlobalState globalState = GlobalState.getInstance();
        String token = globalState.getSavedToken();
        if (StringUtils.isEmpty(token)){
            return;
        }
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            User loginUser = null;
            try {
                loginUser = ApiConfig.maXiaoBaoApi.getLoginUser().execute().body().getData();
            } catch (IOException ex) {
                log.error("Failed to get login user", e);
            }
            if (loginUser == null) {
                return;
            }
            TaskAction taskAction = new TaskAction(mainPanel);
            DataContext dataContext = SimpleDataContext.getSimpleContext(CommonDataKeys.PROJECT, ProjectManager.getInstance().getDefaultProject());
            // 构建 AnActionEvent 对象
            AnActionEvent event = AnActionEvent.createFromAnAction(taskAction, null, "somePlace", dataContext);
            // 手动触发 action
            taskAction.actionPerformed(event);
            PanelUtil.modifyActionGroupWhenLogin(actionGroup, mainPanel, loginUser);
        });
    }




}
