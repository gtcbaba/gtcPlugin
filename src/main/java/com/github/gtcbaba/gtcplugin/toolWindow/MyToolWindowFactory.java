package com.github.gtcbaba.gtcplugin.toolWindow;

import cn.hutool.core.util.StrUtil;
import com.github.gtcbaba.gtcplugin.actions.LoginAction;
import com.github.gtcbaba.gtcplugin.actions.LogoutAction;
import com.github.gtcbaba.gtcplugin.actions.OpenUrlAction;
import com.github.gtcbaba.gtcplugin.actions.TaskAction;
import com.github.gtcbaba.gtcplugin.config.ApiConfig;
import com.github.gtcbaba.gtcplugin.config.GlobalState;
import com.github.gtcbaba.gtcplugin.constant.CommonConstant;
import com.github.gtcbaba.gtcplugin.constant.IconConstant;
import com.github.gtcbaba.gtcplugin.constant.KeyConstant;
import com.github.gtcbaba.gtcplugin.model.common.BaseResponse;
import com.github.gtcbaba.gtcplugin.model.enums.ErrorCode;
import com.github.gtcbaba.gtcplugin.model.response.User;
import com.github.gtcbaba.gtcplugin.view.LoginDialog;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.JBUI;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import static com.github.gtcbaba.gtcplugin.constant.KeyConstant.*;

/**
 * @author pine
 */
@Slf4j
public class MyToolWindowFactory implements ToolWindowFactory {

    private static final Logger logger = Logger.getInstance(MyToolWindowFactory.class);

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        GlobalState globalState = GlobalState.getInstance();
        try {
            //有token的话就根据这个token去getLoginUser（addHeader拦截器会把token放到请求头中）
            User loginUser = ApiConfig.maXiaoBaoApi.getLoginUser().execute().body().getData();
            if (loginUser == null) {
                globalState.removeSavedToken();
                globalState.removeSavedUser();
                //用户第一次点击插件时会弹出登陆框  而不会展示插件主界面
                toolWindow.hide(null);
                LoginDialog loginDialog = new LoginDialog(project);
                loginDialog.show();
            }
        } catch (IOException e) {
            logger.warn("获取登录用户失败");
        }
        ContentFactory contentFactory = ApplicationManager.getApplication().getService(ContentFactory.class);
        JBPanel<?> mainPanel = new JBPanel<>(new BorderLayout());
        mainPanel.setBorder(JBUI.Borders.empty());

        String token = globalState.getSavedToken();
        //登陆框关闭（可能是登陆成功、也可能是手动关闭）后检查登陆态
        if (token != null && !token.isEmpty()) {
            // 登录成功，创建ToolWindow内容
            //ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            //JPanel contentPanel = new JPanel(new BorderLayout());
//            JLabel label = new JLabel("这是ToolWindow的内容");
//            mainPanel.add(label, BorderLayout.CENTER);

            Content content = contentFactory.createContent(mainPanel, "开发任务", true);
            content.setCloseable(false);
            ActionToolbar actionToolbar = createToolbar(content, mainPanel);
            mainPanel.add(actionToolbar.getComponent(), BorderLayout.NORTH);
            TaskAction taskAction = new TaskAction(mainPanel);
            DataContext dataContext = SimpleDataContext.getSimpleContext(CommonDataKeys.PROJECT, project);

            // 构建 AnActionEvent 对象
            AnActionEvent event = AnActionEvent.createFromAnAction(taskAction, null, "somePlace", dataContext);
            // 手动触发 action
            taskAction.actionPerformed(event);
            toolWindow.getContentManager().addContent(content);
            toolWindow.show();
        } else {
            // 登录失败，关闭ToolWindow
            toolWindow.hide(null);
//            JLabel label = new JLabel("您尚未登陆");
//            mainPanel.add(label, BorderLayout.CENTER);
            Content content = contentFactory.createContent(mainPanel, "开发任务", true);
            content.setCloseable(false);
            ActionToolbar actionToolbar = createToolbar(content, mainPanel);
            mainPanel.add(actionToolbar.getComponent(), BorderLayout.NORTH);
            toolWindow.getContentManager().addContent(content);
        }
    }


    private ActionToolbar createToolbar(Content content, JBPanel<?> mainPanel) {
        // 定义一个动作组
        ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        DefaultActionGroup action = (DefaultActionGroup) actionManager.getAction(ACTION_BAR);
        if (action != null) {
            // 创建工具栏
            ActionToolbar actionToolbar = actionManager.createActionToolbar(ACTION_BAR, action, true);
            // 设置目标组件
            actionToolbar.setTargetComponent(mainPanel);
            return actionToolbar;
        }

//        QuestionBankAction questionBankAction = new QuestionBankAction(QUESTION_BANK_ZH, AllIcons.Scope.ChangedFilesAll);
//        actionGroup.add(questionBankAction);
//        actionManager.registerAction(QUESTION_BANK, questionBankAction);
//
//        QuestionAction questionAction = new QuestionAction(QUESTION_ZH, AllIcons.Scope.ChangedFiles);
//        actionGroup.add(questionAction);
//        actionManager.registerAction(QUESTION, questionAction);
//
//        OpenUrlAction webAction = new OpenUrlAction(WEB_ZH, CommonConstant.WEB_HOST, IconConstant.WEB);
//        actionGroup.add(webAction);
//        actionManager.registerAction(WEB, webAction);
//
//        OpenUrlAction helpDocAction = new OpenUrlAction(HELP_ZH, CommonConstant.HELP_DOC, IconConstant.HELP);
//        actionGroup.add(helpDocAction);
//        actionManager.registerAction(HELP, helpDocAction);

        GlobalState globalState = GlobalState.getInstance();
        String token = globalState.getSavedToken();
        if (StrUtil.isBlank(token)) {
            LoginAction loginAction = new LoginAction(LOGIN_ZH, IconConstant.LOGIN, actionGroup, mainPanel);
            actionGroup.add(loginAction);
            actionManager.registerAction(LOGIN, loginAction);
        } else {
            // 登陆态有token的话（可能会过期）
            User loginUser = null;
            try {
                //有token的话就根据这个token去getLoginUser（addHeader拦截器会把token放到请求头中）
                BaseResponse<User> response = ApiConfig.maXiaoBaoApi.getLoginUser().execute().body();
                if (response != null && response.getCode() == ErrorCode.SUCCESS.getCode()) {
                    loginUser = response.getData();
                    globalState.saveUser(loginUser);
                }
            } catch (IOException e) {
                logger.warn("获取登录用户失败");
            }
            // token过期 或 服务端异常没查出来 就需要登陆
            if (loginUser == null) {
                // 登陆态全部清空
                globalState.removeSavedToken();
                globalState.removeSavedUser();

                LoginAction loginAction = new LoginAction(LOGIN_ZH, IconConstant.LOGIN, actionGroup, mainPanel);
                actionGroup.add(loginAction);
                actionManager.registerAction(LOGIN, loginAction);
            } else {
                //有token且token有效没过期 则说明登陆了
                OpenUrlAction vipAction = new OpenUrlAction(IDP_ZH, CommonConstant.IDP_HOST, AllIcons.General.User);
                actionGroup.add(vipAction);
                actionManager.registerAction(IDP, vipAction);

                LogoutAction logoutAction = new LogoutAction(LOGOUT_ZH, IconConstant.LOGOUT, actionGroup, mainPanel);
                actionGroup.add(logoutAction);
                actionManager.registerAction(LOGOUT, logoutAction);

                String userName = loginUser.getUserName();
                if (StrUtil.isNotBlank(userName)) {
                    content.setDisplayName("开发任务 - " + userName);
                }
            }
        }

        // 创建工具栏
        actionManager.registerAction(ACTION_BAR, actionGroup);
        ActionToolbar actionToolbar = actionManager.createActionToolbar(ACTION_BAR, actionGroup, true);
        // 设置目标组件
        actionToolbar.setTargetComponent(mainPanel);
        return actionToolbar;
    }


}
