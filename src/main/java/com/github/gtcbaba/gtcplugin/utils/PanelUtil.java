package com.github.gtcbaba.gtcplugin.utils;


import com.github.gtcbaba.gtcplugin.actions.LogoutAction;
import com.github.gtcbaba.gtcplugin.actions.OpenUrlAction;
import com.github.gtcbaba.gtcplugin.constant.CommonConstant;
import com.github.gtcbaba.gtcplugin.constant.IconConstant;
import com.github.gtcbaba.gtcplugin.constant.KeyConstant;
import com.github.gtcbaba.gtcplugin.model.response.User;
import com.github.gtcbaba.gtcplugin.view.MTabModel;
import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBOptionButton;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.github.gtcbaba.gtcplugin.constant.KeyConstant.LOGOUT_ZH;
import static com.github.gtcbaba.gtcplugin.constant.PageConstant.PAGE_SIZE;


/**
 * @author pine
 */
public class PanelUtil {

    private static final GridBagConstraints prevConstraints = new GridBagConstraints();
    private static final GridBagConstraints pageConstraints = new GridBagConstraints();
    private static final GridBagConstraints nextConstraints = new GridBagConstraints();
    private static final GridBagConstraints totalConstraints = new GridBagConstraints();

    static {
        prevConstraints.gridx = 0;       // 第 0 列
        prevConstraints.gridy = 0;
        prevConstraints.insets = JBUI.insets(5, 5, 0, 5);
        pageConstraints.gridx = 1;       // 第 1 列
        pageConstraints.gridy = 0;
        pageConstraints.insets = JBUI.insets(5, 5, 0, 5);
        nextConstraints.gridx = 2;       // 第 1 列
        nextConstraints.gridy = 0;
        nextConstraints.insets = JBUI.insets(5, 5, 0, 5);
        totalConstraints.gridx = 0;       // 第 1 列
        totalConstraints.gridy = 1;
        totalConstraints.gridwidth = 3;   // 跨两列，实现居中显示
        totalConstraints.insets = JBUI.insetsBottom(10);
    }

    public static JPanel createClosePanel(String title, JPanel tabPanel, JBTabbedPane tabbedPane) {
        JPanel tabLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabLabel.setOpaque(false);

        JLabel tabTitle = new JLabel(title);
        tabLabel.add(tabTitle);

        JButton closeButton = new JButton(AllIcons.Actions.Close);
        closeButton.setPreferredSize(new Dimension(16, 16));
        closeButton.setBorder(BorderFactory.createCompoundBorder());
        closeButton.setContentAreaFilled(false);

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 使用组件引用动态获取索引
                int tabIndex = tabbedPane.indexOfComponent(tabPanel);
                if (tabIndex >= 0) {
                    tabbedPane.remove(tabIndex);
                }
            }
        });

        // 添加鼠标事件监听器以处理悬浮效果
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setOpaque(true);
                closeButton.setBackground(JBColor.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setOpaque(false);
                closeButton.setBackground(null);
            }
        });

        tabLabel.add(closeButton);

        return tabLabel;
    }

    public static JBTable createTablePanel(MTabModel tableModel, BiConsumer<JBTable, MouseEvent> consumer, int columnIndex) {
        // 创建表格
        JBTable table = new JBTable(tableModel);
        table.setFillsViewportHeight(true);

        // 鼠标双击事件监听
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    consumer.accept(table, mouseEvent);
                }
            }
        });

        // 自定义单元格渲染器
        TableCellRenderer categoryRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof List<?>) {
                    List<?> list = (List<?>) value;
                    JBLabel jbLabel = new JBLabel();
                    jbLabel.setText(list.stream()
                            .map(Object::toString)
                            .collect(Collectors.joining("  ")));
                    jbLabel.setOpaque(true); // 确保背景颜色生效
                    jbLabel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    jbLabel.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                    return jbLabel;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(categoryRenderer);

        // 设置列宽为0，使列存在但不可见
        TableColumn column = table.getColumnModel().getColumn(5);
        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setPreferredWidth(0);
        table.setFillsViewportHeight(true);

        return table;
    }

    // 重置分页组件为第一页  并根据总数渲染页数  同时绑定点击上下页的事件
    public static void updatePaginationPanel(JBPanel<?> paginationPanel, long total, int[] currentPage, BiConsumer<Integer, Integer> loadPage) {
        paginationPanel.removeAll();
        // int pageSize = Objects.requireNonNull(GlobalState.getInstance().getState()).pageSize;
        long totalPage = (long) Math.ceil((double) total / PAGE_SIZE);

        JBLabel pageLabel = new JBLabel("第 " + currentPage[0] + " / " + totalPage + " 页");

        JButton prevButton = new JButton("上一页");
        JButton nextButton = new JButton("下一页");

        prevButton.setEnabled(currentPage[0] > 1);
        nextButton.setEnabled(currentPage[0] < totalPage);

        prevButton.addActionListener(e -> {
            if (currentPage[0] > 1) {
                currentPage[0]--;
                loadPage.accept(currentPage[0], PAGE_SIZE);
            }
        });

        nextButton.addActionListener(e -> {
            if (currentPage[0] < totalPage) {
                currentPage[0]++;
                loadPage.accept(currentPage[0], PAGE_SIZE);
            }
        });

        paginationPanel.add(prevButton, prevConstraints);
        paginationPanel.add(pageLabel, pageConstraints);
        paginationPanel.add(nextButton, nextConstraints);
        JBLabel totalLabel = new JBLabel("共 " + total + " 条");
        Color fadedColor = new JBColor(new Color(0, 0, 0, 128), new Color(255, 255, 255, 128)); // 黑色，Alpha = 128
        totalLabel.setForeground(fadedColor);
        paginationPanel.add(totalLabel, totalConstraints);

        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    public static JBPanel<?> getNeedVipPanel() {
        JBPanel<?> needVipPanel = new JBPanel<>();
        AbstractAction needVipAction = new AbstractAction("仅会员可见内容，请先开通会员", AllIcons.General.User) {
            @Override
            public void actionPerformed(ActionEvent e) {
                BrowserUtil.browse(CommonConstant.VIP);
            }
        };
        needVipPanel.add(new JBOptionButton(needVipAction, null));
        return needVipPanel;
    }

//    public static JBPanel<?> getNeedLoginPanel() {
//        JBPanel<?> needLoginPanel = new JBPanel<>();
//        AbstractAction needVipAction = new AbstractAction(TextConstant.LOGIN, AllIcons.General.User) {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                LoginPanel loginPanel = new LoginPanel(ProjectManager.getInstance().getDefaultProject());
//                loginPanel.show();
//            }
//        };
//        needLoginPanel.add(new JBOptionButton(needVipAction, null));
//        return needLoginPanel;
//    }

    public static void modifyActionGroupWhenLogin(DefaultActionGroup actionGroup, JBPanel<?> mainPanel, User loginUser) {
        ApplicationManager.getApplication().invokeLater(() -> {
            ActionManager actionManager = ActionManager.getInstance();

            // 删除 登录
            AnAction loginAction = actionManager.getAction(KeyConstant.LOGIN);
            if (loginAction == null) {
                return;
            }
            actionGroup.remove(loginAction);
            actionManager.unregisterAction(KeyConstant.LOGIN);

            // 增加 用户
            OpenUrlAction userAction = new OpenUrlAction(loginUser.getUserName(), CommonConstant.IDP_HOST, AllIcons.General.User);
            actionGroup.add(userAction);
            actionManager.registerAction(KeyConstant.IDP, userAction);

            // 增加 注销
            LogoutAction logoutAction = new LogoutAction(LOGOUT_ZH, IconConstant.LOGOUT, actionGroup, mainPanel);
            actionGroup.add(logoutAction);
            actionManager.registerAction(KeyConstant.LOGOUT, logoutAction);
        });
    }

}
