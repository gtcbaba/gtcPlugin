package com.github.gtcbaba.gtcplugin.view;

import com.github.gtcbaba.gtcplugin.config.ApiConfig;
import com.github.gtcbaba.gtcplugin.config.GlobalState;
import com.github.gtcbaba.gtcplugin.model.dto.UserLoginRequest;
import com.github.gtcbaba.gtcplugin.model.response.User;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class LoginDialog extends JDialog {

        private static final Logger logger = Logger.getInstance(LoginDialog.class);


        private final Project project;
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

            // 绑定回车事件
            passwordField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (validateLogin()) {
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(LoginDialog.this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });

            // 点击登陆按钮事件
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (validateLogin()) {
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
            UserLoginRequest userLoginRequest = new UserLoginRequest();
            userLoginRequest.setUserAccount(username);
            userLoginRequest.setUserPassword(password);
            try {
                User user = ApiConfig.maXiaoBaoApi.userLogin(userLoginRequest).execute().body().getData();
                GlobalState globalState = GlobalState.getInstance();
                //登陆成功  则保存token
                if (user == null) {
                    return false;
                }
                globalState.saveToken(user.getToken());
                globalState.saveUser(user);
            } catch (IOException e) {
                logger.warn("获取登录用户失败");
                return false;
            }
            return true;
        }

    }
