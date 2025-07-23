package com.github.gtcbaba.gtcplugin.actions;

import com.github.gtcbaba.gtcplugin.config.ApiConfig;
import com.github.gtcbaba.gtcplugin.config.GlobalState;
import com.github.gtcbaba.gtcplugin.constant.CommonConstant;
import com.github.gtcbaba.gtcplugin.constant.PageConstant;
import com.github.gtcbaba.gtcplugin.constant.TextConstant;
import com.github.gtcbaba.gtcplugin.model.common.Page;
import com.github.gtcbaba.gtcplugin.model.common.PageRequest;
import com.github.gtcbaba.gtcplugin.model.dto.TaskTypeQueryRequest;
import com.github.gtcbaba.gtcplugin.model.enums.CodeTypeEnum;
import com.github.gtcbaba.gtcplugin.model.enums.DevelopStatusEnum;
import com.github.gtcbaba.gtcplugin.model.enums.TaskTypeEnum;
import com.github.gtcbaba.gtcplugin.model.response.Task;
import com.github.gtcbaba.gtcplugin.model.response.User;
import com.github.gtcbaba.gtcplugin.utils.ContentUtil;
import com.github.gtcbaba.gtcplugin.utils.PanelUtil;
import com.github.gtcbaba.gtcplugin.view.LoginDialog;
import com.github.gtcbaba.gtcplugin.view.MTabModel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.WrapLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class TaskAction extends AnAction {

    private JBPanel<?> labelPanel;
    private MTabModel tableModel;
    private JBPanel<?> paginationPanel;
    private JBPanel<?> mainPanel;

    // 给页码组件渲染用的（根据它决定有没有上下页）
    private final int[] currentPage = new int[]{1};
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private final TaskTypeQueryRequest queryRequest = new TaskTypeQueryRequest();

    Vector<String> demandColumns = new Vector<>(Arrays.asList("需求id", "需求名称", "任务类型", "任务状态", "排期"));
    Vector<String> bugColumns = new Vector<>(Arrays.asList("缺陷id", "缺陷名称", "任务类型", "任务状态", "排期"));
    Vector<String> defaultColumns = new Vector<>(Arrays.asList("id", "名称", "任务类型", "任务状态", "排期"));

    public TaskAction(String text, Icon icon) {
        super(text, text, icon);
    }

    // 首页显示
    public TaskAction(JBPanel<?> mainPanel) {
        this.mainPanel = mainPanel;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();

        //创建一块区域tabPanel  要么显示在首页  要么显示在新的tab页
        JBPanel<?> tabPanel = new JBPanel<>(new BorderLayout());
        if (mainPanel != null) {
            // 显示在首页
            mainPanel.add(tabPanel, BorderLayout.CENTER);
        }else {
            ContentUtil.createContent(tabPanel, TextConstant.MY_TASKS, false, project);
        }

        // 标签栏  显示 需求/缺陷
        loadLabelPanel();
        //tabPanel.add(labelPanel, BorderLayout.NORTH);

        // 搜索条
        JPanel searchPanel = this.getSearchPanel();
        //tabPanel.add(searchPanel, BorderLayout.NORTH);

        JBPanel<?> finalFindPanel = new JBPanel<>(new BorderLayout());
        finalFindPanel.add(labelPanel, BorderLayout.NORTH);
        finalFindPanel.add(searchPanel, BorderLayout.CENTER);
        tabPanel.add(finalFindPanel, BorderLayout.NORTH);

        // 数据表
        loadDataTable(tabPanel, project);

        //分页条
        paginationPanel = new JBPanel<>(new GridBagLayout());
        tabPanel.add(paginationPanel, BorderLayout.SOUTH);
    }


    // 初始化（缺陷/需求）标签块
    private void loadLabelPanel() {
        labelPanel = new JBPanel<>(new WrapLayout(FlowLayout.LEFT, 5, 5));
        JBLabel[] selectedLabel = {null};

        //浅灰色
        JBColor customLightGray = new JBColor(Gray._220, Gray._80);
        //更浅的灰色
        JBColor customGray = new JBColor(Gray._180, Gray._40);

        ApplicationManager.getApplication().invokeLater(() -> {
           for (TaskTypeEnum taskType : TaskTypeEnum.values()) {
               JBLabel label = new JBLabel(taskType.getTaskType());
               label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
               label.setOpaque(true);
               label.setBackground(customLightGray);
               label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

               //绑定点击事件
               label.addMouseListener(new MouseAdapter() {
                   @Override
                   public void mouseClicked(MouseEvent e) {
                       //如果这次点击的标签和上一个不一样 则取消上一个高亮的标签
                       if (selectedLabel[0] != null && selectedLabel[0] != label) {
                           selectedLabel[0].setBackground(customLightGray);
                       }
                       // 如果当前点击的是已经选中的标签，取消高亮
                       if (label == selectedLabel[0]) {
                           label.setBackground(customLightGray);
                           // 同时取消筛选条件
                           queryRequest.setTaskType(CommonConstant.DEFAULT_TASK_TYPE_CATEGORY_ID);
                           searchAndLoadData(queryRequest);
                           // 清空选中标签
                           selectedLabel[0] = null;
                       }else {
                           // 高亮当前标签
                           label.setBackground(customGray);
                                //重置当前页为第一页
                           currentPage[0] = PageConstant.FIRST_PAGE;
                           queryRequest.setTaskType(taskType.getValue());
                           queryRequest.setCurrent(PageConstant.FIRST_PAGE);
                           searchAndLoadData(queryRequest);
                           // 更新当前选中的标签
                           selectedLabel[0] = label;
                       }
                   }

                   @Override
                   public void mouseEntered(MouseEvent e) {
                       if (label != selectedLabel[0]) {
                           // 鼠标悬浮效果，仅当标签未被选中时生效
                           label.setBackground(customGray);
                       }
                   }

                   @Override
                   public void mouseExited(MouseEvent e) {
                       if (label != selectedLabel[0]) {
                           // 鼠标离开时恢复原始背景色，仅当标签未被选中时生效
                           label.setBackground(customLightGray);
                       }
                   }
               });

               labelPanel.add(label);
           }
        });
    }

    //清空表格、查数据再渲染表格、更新分页栏
    private void searchAndLoadData(TaskTypeQueryRequest queryRequest) {
        if (tableModel == null) {
            return;
        }
        // 清空现有表格数据
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            Page<Task> data = this.fetchDataFromApi(queryRequest);
            ApplicationManager.getApplication().invokeLater(() -> {
                //清空表格数据
                tableModel.setRowCount(0);

                Long taskType = queryRequest.getTaskType();
                if (CommonConstant.DEMAND_TASK_TYPE_CATEGORY_ID.equals(taskType)) {
                    tableModel.setColumnIdentifiers(demandColumns);
                } else if (CommonConstant.BUG_TASK_TYPE_CATEGORY_ID.equals(taskType)) {
                    tableModel.setColumnIdentifiers(bugColumns);
                } else {
                    tableModel.setColumnIdentifiers(defaultColumns);
                }

                // 添加新数据到表格模型
                for (Task row : data.getRecords()) {
                    tableModel.addRow(new Object[]{row.getId().toString(), row.getTaskName(), CodeTypeEnum.getCodeTypeByValue(row.getCodeType()),
                            DevelopStatusEnum.getDevelopStatusByValue(row.getStatus()), sdf.format(row.getScheduledTime())});
                }

                // 重新渲染表格
                tableModel.fireTableDataChanged();
                // 更新分页栏
                PanelUtil.updatePaginationPanel(paginationPanel, data.getTotal(), currentPage, this::loadPage);
            });
        });
    }


    // 根据 queryRequest 查询数据
    private Page<Task> fetchDataFromApi(TaskTypeQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new IllegalArgumentException("queryRequest cannot be null");
        }
        GlobalState globalState = GlobalState.getInstance();
        User loginUser = globalState.getSavedUser();
        if (loginUser != null) {
            queryRequest.setUserId(loginUser.getId());
        }

        try {
            if (CommonConstant.DEFAULT_TASK_TYPE_CATEGORY_ID.equals(queryRequest.getTaskType())) {
                return ApiConfig.maXiaoBaoApi.getTaskList(queryRequest).execute().body().getData();
            } else {
                return ApiConfig.maXiaoBaoApi.getTaskList(queryRequest).execute().body().getData();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // 给更新页码组件用的  实现分页逻辑，并更新表格数据
    private void loadPage(int page, int pageSize) {
        queryRequest.setPageSize(pageSize);
        queryRequest.setCurrent(page);
        this.searchAndLoadData(queryRequest);
    }


    // 将表格数据显示出来
    // 把模型数据 tableModel 放到 JBTable 中， 再把 JBTable 放到 JBScrollPane 中，最后再放到 tabPanel 中
    private void loadDataTable(JBPanel<?> tabPanel, Project project) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            Page<Task> data = this.fetchDataFromApi(queryRequest);
            // 创建表格数据模型
            ApplicationManager.getApplication().invokeLater(() -> {
                tableModel = new MTabModel();
//                if (queryRequest.getTaskTypeId() ==)
                tableModel.addColumn("id");
                tableModel.addColumn("名称");
                tableModel.addColumn("任务类型");
                tableModel.addColumn("任务状态");
                tableModel.addColumn("排期");

                // 将数据添加到表格模型
                for (Task row : data.getRecords()) {
                    tableModel.addRow(new Object[]{row.getId().toString(), row.getTaskName(), CodeTypeEnum.getCodeTypeByValue(row.getCodeType()),
                            DevelopStatusEnum.getDevelopStatusByValue(row.getStatus()), sdf.format(row.getScheduledTime())});
                }
                // 把 tableModel 放到 JBTable 中，并给 JBTable绑定双击事件
                JBTable table = PanelUtil.createTablePanel(tableModel, (tempTable, mouseEvent) -> {
                    int selectedRow = tempTable.getSelectedRow();
                    if (selectedRow != -1) {
                        // 获取选中行的数据
                        String id = (String) tempTable.getValueAt(selectedRow, 0);

                        // 打开包含该行数据的新选项卡
//                        QuestionListManager questionListManager = new QuestionListManager();
//                        questionListManager.addQuestionTab(Long.valueOf(id), project);
                        JOptionPane.showMessageDialog(null, id, "提示", JOptionPane.INFORMATION_MESSAGE);
                    }
                }, 2);
                // 将表格添加到滚动面板
                JBScrollPane scrollPane = new JBScrollPane(table);
                // 确保表格充满视口
                scrollPane.setViewportView(table);
                tabPanel.add(scrollPane, BorderLayout.CENTER);

                // 更新分页条
                PanelUtil.updatePaginationPanel(paginationPanel, data.getTotal(), currentPage, this::loadPage);
            });
        });
    }

    // 搜索条
    private @NotNull JPanel getSearchPanel() {
        JBPanel<?> searchPanel = new JBPanel<>(new BorderLayout());

        //点击清除按钮后触发
        SearchTextField searchField = new SearchTextField() {
            @Override
            protected void onFieldCleared() {
                queryRequest.setCurrent(PageConstant.FIRST_PAGE);
                //从第一页开始查  重置当前页为第一页
                currentPage[0] = PageConstant.FIRST_PAGE;
                searchAndLoadData(queryRequest);
            }
        };
        // 监听搜索框内容变化
        searchField.addDocumentListener(new DocumentListener() {
            private void onTextChanged() {
                // 这里是文本变化时执行的操作
                String text = searchField.getText();
                queryRequest.setSearchText(text);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                onTextChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onTextChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onTextChanged();
            }
        });

        // 绑定回车事件
        searchField.getTextEditor().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    //按回车执行搜索和刷新界面
                    String text = searchField.getText();
                    queryRequest.setSearchText(text);
                    queryRequest.setCurrent(PageConstant.FIRST_PAGE);
                    currentPage[0] = PageConstant.FIRST_PAGE;
                    searchAndLoadData(queryRequest);
                }
            }
        });

        searchField.getTextEditor().getEmptyText().setText(TextConstant.TITLE_SEARCH_PLACE_HOLDER);
        JButton searchButton = new JButton(TextConstant.SEARCH);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        // 给搜索按钮绑定点击事件（要么回车、要么点按钮执行搜索）
        searchButton.addActionListener(e -> {
            String text = searchField.getText();
            queryRequest.setSearchText(text);
            queryRequest.setCurrent(PageConstant.FIRST_PAGE);
            currentPage[0] = PageConstant.FIRST_PAGE;
            searchAndLoadData(queryRequest);
        });
        // 添加筛选框区域
        // todo

        return searchPanel;

    }
}
