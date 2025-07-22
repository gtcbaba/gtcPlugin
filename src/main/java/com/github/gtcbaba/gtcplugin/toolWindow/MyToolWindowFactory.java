package com.github.gtcbaba.gtcplugin.toolWindow;

import com.intellij.openapi.application.ApplicationManager;
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

/**
 * @author pine
 */
@Slf4j
public class MyToolWindowFactory implements ToolWindowFactory {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.hide(null);
        LoginDialog loginDialog = new LoginDialog(project);
        loginDialog.show();
        ContentFactory contentFactory = ApplicationManager.getApplication().getService(ContentFactory.class);
        JBPanel<?> mainPanel = new JBPanel<>(new BorderLayout());
        mainPanel.setBorder(JBUI.Borders.empty());
        //检查登陆态
        if (loginDialog.isOK()) {
            // 登录成功，创建ToolWindow内容
            //ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            //JPanel contentPanel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("这是ToolWindow的内容");
            mainPanel.add(label, BorderLayout.CENTER);
            Content content = contentFactory.createContent(mainPanel, "Tasks", true);
            content.setCloseable(false);
            toolWindow.getContentManager().addContent(content);
            toolWindow.show();
        } else {
            // 登录失败，关闭ToolWindow
            toolWindow.hide(null);
            JLabel label = new JLabel("您尚未登陆");
            mainPanel.add(label, BorderLayout.CENTER);
            Content content = contentFactory.createContent(mainPanel, "Tasks", true);
            content.setCloseable(false);
            toolWindow.getContentManager().addContent(content);
        }
    }

    private static class LoginDialog extends JDialog {
        private final Project project;
        @Getter
        private boolean isOK = false;
        private final JTextField usernameField;
        private final JPasswordField passwordField;

        public LoginDialog(Project project) {
            super((Frame) null, "登录", true);
            this.project = project;

            setSize(300, 200);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            usernameField = new JTextField();
            passwordField = new JPasswordField();

            panel.add(new JLabel("用户名:"));
            panel.add(usernameField);
            panel.add(new JLabel("密码:"));
            panel.add(passwordField);

            JButton loginButton = new JButton("登录");
            JButton cancelButton = new JButton("取消");

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(loginButton);
            buttonPanel.add(cancelButton);

            add(panel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (validateLogin()) {
                        isOK = true;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginDialog.this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
        }

        private boolean validateLogin() {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            return USERNAME.equals(username) && PASSWORD.equals(password);
        }

    }

}
