package com.github.gtcbaba.gtcplugin.view;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.github.gtcbaba.gtcplugin.config.GlobalState;
import com.github.gtcbaba.gtcplugin.constant.TextConstant;
import com.github.gtcbaba.gtcplugin.model.common.BaseResponse;
import com.github.gtcbaba.gtcplugin.model.dto.AppAndBranch;
import com.github.gtcbaba.gtcplugin.model.dto.BranchesAddLinkRequest;
import com.github.gtcbaba.gtcplugin.model.dto.BranchesAddRequest;
import com.github.gtcbaba.gtcplugin.model.dto.BranchesSearchRequest;
import com.github.gtcbaba.gtcplugin.model.enums.ErrorCode;
import com.github.gtcbaba.gtcplugin.model.response.App;
import com.github.gtcbaba.gtcplugin.model.response.Branch;
import com.github.gtcbaba.gtcplugin.model.response.BranchVO;
import com.github.gtcbaba.gtcplugin.model.response.User;
import com.github.gtcbaba.gtcplugin.utils.ContentUtil;
import com.github.gtcbaba.gtcplugin.utils.PanelUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.WrapLayout;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.compress.utils.Lists;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.gtcbaba.gtcplugin.config.ApiConfig.maXiaoBaoApi;

public class GitBranchManager
{
    // 创建分支相关组件
    private JBPanel<?> createBranchPanel;
    private List<CreateRowPanel> createRowPanelList = new ArrayList<>();
    // 关联分支相关组件
    private JBPanel<?> linkBranchPanel;
    private List<LinkRowPanel> linkRowPanelList = new ArrayList<>();

    private MTabModel tableModel;

    //private Long taskId;
    //private Long userId;

    public void addGitTab(Long codeRepositoryId, Long taskId, String taskName, Project project) {

        // 新建 tab 页
        JBPanel<?> newTabPanel = new JBPanel<>(new BorderLayout());
        ContentUtil.createContent(newTabPanel, taskName, false, project);
        ApplicationManager.getApplication().invokeLater(() -> {
            // 获取 id
            GlobalState globalState = GlobalState.getInstance();
            User loginUser = globalState.getSavedUser();

            //this.taskId = taskId;
            //this.userId = loginUser.getId();


            // 创建两个单选按钮  使用 ButtonGroup 将按钮分组  切换按钮放在 optionPanel 中
            JBPanel<?> idAndOptionPanel = new JBPanel<>(new GridLayout(0, 1));
            idAndOptionPanel.add(createPanelWithSpacing(new Label("任务ID：" + taskId.toString())));
            JBPanel<?> optionPanel = new JBPanel<>(new WrapLayout(FlowLayout.LEFT, 10, 5));
            idAndOptionPanel.add(optionPanel);
            JBRadioButton radioButton1 = new JBRadioButton("创建分支", true);
            JBRadioButton radioButton2 = new JBRadioButton("关联分支");
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(radioButton1);
            buttonGroup.add(radioButton2);
            optionPanel.add(radioButton1);
            optionPanel.add(radioButton2);



            // 创建分支的主体部分 createBranchPanel
            createBranchPanel = new JBPanel<>();
            createBranchPanel.setLayout(new GridLayout(0, 1));
                // createBranchPanel 的新增按钮
            JButton createAddButton = new JButton("+ 新增");
            createAddButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addCreateRow(codeRepositoryId);
                }
            });
            createBranchPanel.add(createPanelWithSpacing(createAddButton, 12));

            // 关联分支的主体部分 linkBranchPanel
            linkBranchPanel = new JBPanel<>();
            linkBranchPanel.setLayout(new GridLayout(0, 1));
                // linkBranchPanel 的新增按钮
            JButton linkAddButton = new JButton("+ 新增");
            linkAddButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addLinkRow(codeRepositoryId);
                }
            });
            linkBranchPanel.add(createPanelWithSpacing(linkAddButton, 12));


                // createBranchPanel 的确定按钮
            JButton createConfirmButton = new JButton("确定");
            JBPanel<?> createConfirmButtonPanel = new JBPanel<>(new FlowLayout(FlowLayout.RIGHT));
            createConfirmButtonPanel.add(createConfirmButton);
            createBranchPanel.add(createConfirmButtonPanel);
            createConfirmButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BranchesAddRequest branchesAddRequest = new BranchesAddRequest();
                    ArrayList<AppAndBranch> appAndBranches = Lists.newArrayList();
                    for ( CreateRowPanel createRowPanel : createRowPanelList ) {
                        AppAndBranch appAndBranch = new AppAndBranch();
                        GitComboBoxItem selectedAppItem = (GitComboBoxItem) createRowPanel.getAppCombo().getSelectedItem();
                        if (selectedAppItem != null) {
                            appAndBranch.setAppId(Long.valueOf(selectedAppItem.key));
                            appAndBranch.setAppName(selectedAppItem.value);
                        }
                        GitComboBoxItem selectedSourceBranchItem = (GitComboBoxItem) createRowPanel.getSourceBranchCombo().getSelectedItem();
                        if (selectedSourceBranchItem != null) {
                            appAndBranch.setBaseOnBranchId(Long.valueOf(selectedSourceBranchItem.key));
                            appAndBranch.setBaseOnBranchName(selectedSourceBranchItem.value);
                        }
                        appAndBranch.setBranchName(createRowPanel.getNewBranchField());
                        String checkResult = appAndBranch.checkMyFields();
                        if ( checkResult != null){
                            JOptionPane.showMessageDialog(null, checkResult, "提示", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        appAndBranches.add(appAndBranch);
                    }
                    branchesAddRequest.setAppAndBranches(appAndBranches);
                    branchesAddRequest.setTaskId(taskId);
                    branchesAddRequest.setUserId(loginUser.getId());
                    try {
                        BaseResponse<Boolean> result = maXiaoBaoApi.addBranchesUnderApps(branchesAddRequest).execute().body();
                        if (result.getCode() == ErrorCode.PARAMS_ERROR.getCode()) {
                            JOptionPane.showMessageDialog(null, result.getMessage(), "提示", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            searchAndReloadData(taskId, loginUser.getId());
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "错误", "提示", JOptionPane.INFORMATION_MESSAGE);
                        throw new RuntimeException(ex);
                    }
                }
            });


                // linkBranchPanel 的确定按钮
            JButton linkConfirmButton = new JButton("确定");
            JBPanel<?> linkConfirmButtonPanel = new JBPanel<>(new FlowLayout(FlowLayout.RIGHT));
            linkConfirmButtonPanel.add(linkConfirmButton);
            linkBranchPanel.add(linkConfirmButtonPanel);
            linkConfirmButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    BranchesAddLinkRequest branchesAddLinkRequest = new BranchesAddLinkRequest();
                    ArrayList<Long> branchIds = Lists.newArrayList();
                    linkRowPanelList.forEach(linkRowPanel -> {
                        GitComboBoxItem selectedBranchItem = (GitComboBoxItem) linkRowPanel.getBranchCombo().getSelectedItem();
                        if (selectedBranchItem != null) {
                            branchIds.add(Long.valueOf(selectedBranchItem.key));
                        }
                    });
                    if (CollUtil.isEmpty(branchIds) || branchIds.size() != linkRowPanelList.size()) {
                        JOptionPane.showMessageDialog(null, "请选择要关联的分支", "提示", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    branchesAddLinkRequest.setBranchIds(branchIds);
                    branchesAddLinkRequest.setUserId(loginUser.getId());
                    branchesAddLinkRequest.setTaskId(taskId);
                    try {
                        BaseResponse<Boolean> result = maXiaoBaoApi.addLinksToBranches(branchesAddLinkRequest).execute().body();
                        if (result.getCode() != ErrorCode.SUCCESS.getCode()) {
                            JOptionPane.showMessageDialog(null, result.getMessage(), "提示", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            searchAndReloadData(taskId, loginUser.getId());
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "错误", "提示", JOptionPane.INFORMATION_MESSAGE);
                        throw new RuntimeException(ex);
                    }
//                    createRowPanelList.forEach(createRowPanel -> {
//                        AppAndBranch appAndBranch = new AppAndBranch();
//                        GitComboBoxItem selectedAppItem = (GitComboBoxItem) createRowPanel.getAppCombo().getSelectedItem();
//                        if (selectedAppItem != null) {
//                            appAndBranch.setAppId(Long.valueOf(selectedAppItem.key));
//                            appAndBranch.setAppName(selectedAppItem.value);
//                        }
//                        GitComboBoxItem selectedSourceBranchItem = (GitComboBoxItem) createRowPanel.getSourceBranchCombo().getSelectedItem();
//                        if (selectedSourceBranchItem != null) {
//                            appAndBranch.setBaseOnBranchId(Long.valueOf(selectedSourceBranchItem.key));
//                            appAndBranch.setBaseOnBranchName(selectedSourceBranchItem.value);
//                        }
//                        appAndBranch.setBranchName(createRowPanel.getNewBranchField());
//                        String checkResult = appAndBranch.checkMyFields();
//                        if ( checkResult != null){
//                            JOptionPane.showMessageDialog(null, checkResult, "提示", JOptionPane.INFORMATION_MESSAGE);
//                            return;
//                        }
//                        appAndBranches.add(appAndBranch);
//                    });
//                    branchesAddRequest.setAppAndBranches(appAndBranches);
//                    branchesAddRequest.setTaskId(taskId);
//                    branchesAddRequest.setUserId(loginUser.getId());
//                    try {
//                        BaseResponse<Boolean> result = maXiaoBaoApi.addBranchesUnderApps(branchesAddRequest).execute().body();
//                        if (result.getCode() == ErrorCode.PARAMS_ERROR.getCode()) {
//                            JOptionPane.showMessageDialog(null, result.getMessage(), "提示", JOptionPane.INFORMATION_MESSAGE);
//                        } else {
//                            searchAndReloadData(taskId, loginUser.getId());
//                        }
//                    } catch (IOException ex) {
//                        JOptionPane.showMessageDialog(null, "错误", "提示", JOptionPane.INFORMATION_MESSAGE);
//                        throw new RuntimeException(ex);
//                    }
                }
            });


            // 打开后默认有一行
            addCreateRow(codeRepositoryId);
            addLinkRow(codeRepositoryId);
            linkBranchPanel.setVisible(false);



            // optionAndCreateOrLinkPanel 放在 整个 newTabPanel 的中间
            // optionAndCreateOrLinkPanel 装有 optionPanel 和 CreateOrLinkPanel
            // CreateOrLinkPanel 装有 createBranchPanel 和 linkBranchPanel
            JBPanel<?> optionAndCreateOrLinkPanel = new JBPanel<>(new BorderLayout());
            optionAndCreateOrLinkPanel.add(idAndOptionPanel, BorderLayout.NORTH);
            //JBPanel<?> createOrLinkPanel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT));
            //JBPanel<?> createOrLinkPanel = new JBPanel<>(new BorderLayout());

            //JBPanel<?> createOrLinkPanel = new JBPanel<>(new GridLayout(1,2));
            JBPanel<?> createOrLinkPanel = new JBPanel<>(new FlowLayout());
            createOrLinkPanel.add(createBranchPanel);
            createOrLinkPanel.add(linkBranchPanel);
            // 将表格添加到滚动面板
            JBScrollPane scrollPane = new JBScrollPane(createOrLinkPanel);
            // 确保表格充满视口
            scrollPane.setViewportView(createOrLinkPanel);
            optionAndCreateOrLinkPanel.add(scrollPane, BorderLayout.CENTER);

            // 添加 ActionListener 点击单选按钮切换 panel
            ActionListener listener1 = e -> {
                createBranchPanel.setVisible(true);
                linkBranchPanel.setVisible(false);
            };
            ActionListener listener2 = e -> {
                createBranchPanel.setVisible(false);
                linkBranchPanel.setVisible(true);
            };
            radioButton1.addActionListener(listener1);
            radioButton2.addActionListener(listener2);

            newTabPanel.add(optionAndCreateOrLinkPanel, BorderLayout.NORTH);


            // 列表 内容是用户在当前任务下创建的分支  有：app名、分支名、操作（复制 git 命令）
            // 数据表
            loadBranchesDataTable(newTabPanel, taskId, loginUser.getId());

        });

    }


    private static JPanel createPanelWithSpacing(Component component, int spacing) {
        // 创建一个面板，并设置间距
        JPanel spacingPanel = new JPanel(new BorderLayout());
        spacingPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, spacing, 0)); // 上下间距为 spacing，左右间距为 0
        spacingPanel.add(component, BorderLayout.CENTER);

        return spacingPanel;
    }

    private static JPanel createPanelWithSpacing(Component component) {
        // 创建一个面板，并设置间距
        JPanel spacingPanel = new JPanel(new BorderLayout());
        spacingPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); // 上下间距为 spacing，左右间距为 0
        spacingPanel.add(component, BorderLayout.CENTER);

        return spacingPanel;
    }


    // 创建分支行添加
    private void addCreateRow(Long codeRepositoryId) {
        CreateRowPanel rowPanel = new CreateRowPanel(codeRepositoryId);
        createRowPanelList.add(rowPanel);
        int componentCount = createBranchPanel.getComponentCount();
        createBranchPanel.add(rowPanel, componentCount - 2);
        if (createRowPanelList.size() == 2){
            createRowPanelList.get(0).removeButton.setEnabled(true);
        }
        createBranchPanel.revalidate();
        createBranchPanel.repaint();
    }


    // 关联分支行添加
    private void addLinkRow(Long codeRepositoryId) {
        LinkRowPanel rowPanel = new LinkRowPanel(codeRepositoryId);
        linkRowPanelList.add(rowPanel);
        int componentCount = linkBranchPanel.getComponentCount();
        linkBranchPanel.add(rowPanel, componentCount - 2);
        if (linkRowPanelList.size() == 2){
            linkRowPanelList.get(0).removeButton.setEnabled(true);
        }
        linkBranchPanel.revalidate();
        linkBranchPanel.repaint();
    }


    // 代表一行
    @EqualsAndHashCode(callSuper = true)
    @Data
    private class CreateRowPanel extends JPanel {

        private JComboBox<GitComboBoxItem> appCombo;
        private String newBranchField;
        private JComboBox<GitComboBoxItem> sourceBranchCombo = new ComboBox<>();
        private JButton removeButton = new JButton("-");

        // 传入任务id
        public CreateRowPanel(Long codeRepositoryId) {
            //setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
            setLayout(new GridLayout(1, 0));
            appCombo = createGitCustomFilterBox(() -> {
                try {
                    BaseResponse<List<App>> baseResponse = maXiaoBaoApi.listAppsUndercodeRepositoryId(codeRepositoryId).execute().body();
                    return baseResponse.getData().stream()
                            .map(app -> new GitComboBoxItem(app.getId().toString(), app.getAppName()))
                            .collect(Collectors.toList());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, "请选择源分支", sourceBranchCombo);
            JBPanel<?> appPanel = new JBPanel<>(new FlowLayout());
            appPanel.add(new JBLabel("应用："));
            appPanel.add(appCombo);
            add(appPanel);

            // 源分支
            JBPanel<?> sourceBranchPanel = new JBPanel<>(new FlowLayout());
            sourceBranchPanel.add(new JBLabel("源分支："));
            sourceBranchPanel.add(sourceBranchCombo);
            add(sourceBranchPanel);

            //新分支
            SearchTextField searchField = new SearchTextField(){
                // 点击清除按钮后触发
                @Override
                protected void onFieldCleared() {
                    //todo 提示不能为空
                }
            };
            searchField.getTextEditor().getEmptyText().setText(TextConstant.GIT_PLACE_HOLDER);
            // 添加 DocumentListener 以监听内容变化
            searchField.addDocumentListener(new DocumentListener() {
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

                private void onTextChanged() {
                    // 这里是文本变化时执行的操作
                    String text = searchField.getText();
                    newBranchField = text;
                }
            });
            JBPanel<?> newBranchPanel = new JBPanel<>(new FlowLayout());
            newBranchPanel.add(new JBLabel("新分支："));
            newBranchPanel.add(searchField);


            JBPanel<?> removeButtonPanel = new JBPanel<>();
            removeButtonPanel.add(removeButton);
            removeButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 15));

            removeButton.setPreferredSize(new Dimension(20, 20));
            if (createRowPanelList.isEmpty()){
                removeButton.setEnabled(false);
            }

            removeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        createRowPanelList.remove(CreateRowPanel.this);
                        createBranchPanel.remove(CreateRowPanel.this);
                        if (createRowPanelList.size() == 1) {
                            createRowPanelList.get(0).removeButton.setEnabled(false);
                        }
                        createBranchPanel.revalidate();
                        createBranchPanel.repaint();
                    });
                }
            });

            newBranchPanel.add(removeButtonPanel);
            add(newBranchPanel);

        }
    }

    // 代表一行
    @EqualsAndHashCode(callSuper = true)
    @Data
    private class LinkRowPanel extends JPanel {

        private JComboBox<GitComboBoxItem> appCombo;
//        private String newBranchField;
        private JComboBox<GitComboBoxItem> branchCombo = new ComboBox<>();
        private JButton removeButton = new JButton("-");

        // 传入任务id
        public LinkRowPanel(Long codeRepositoryId) {
            setLayout(new GridLayout(1, 0));
            appCombo = createGitCustomFilterBox(() -> {
                try {
                    BaseResponse<List<App>> baseResponse = maXiaoBaoApi.listAppsUndercodeRepositoryId(codeRepositoryId).execute().body();
                    return baseResponse.getData().stream()
                            .map(app -> new GitComboBoxItem(app.getId().toString(), app.getAppName()))
                            .collect(Collectors.toList());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, "请选择您要关联的分支", branchCombo);
            JBPanel<?> appPanel = new JBPanel<>(new FlowLayout());
            appPanel.add(new JBLabel("应用："));
            appPanel.add(appCombo);
            add(appPanel);

            // 分支
            JBPanel<?> branchPanel = new JBPanel<>(new FlowLayout());
            branchPanel.add(new JBLabel("分支："));
            branchPanel.add(branchCombo);


            JBPanel<?> removeButtonPanel = new JBPanel<>();
            removeButtonPanel.add(removeButton);
            removeButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 15));

            removeButton.setPreferredSize(new Dimension(20, 20));
            if (linkRowPanelList.isEmpty()){
                removeButton.setEnabled(false);
            }
            removeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        linkRowPanelList.remove(LinkRowPanel.this);
                        linkBranchPanel.remove(LinkRowPanel.this);
                        if (linkRowPanelList.size() == 1) {
                            linkRowPanelList.get(0).removeButton.setEnabled(false);
                        }
                        linkBranchPanel.revalidate();
                        linkBranchPanel.repaint();
                    });
                }
            });

            branchPanel.add(removeButtonPanel);
            add(branchPanel);

        }
    }

    /**
     * @param supplier 提供 comboBox 的数据来源
//     * @param lastSelectedIndex  代表不同的筛选框
//     * @param fieldName 代表搜索哪个字段
     * @param placeHolder 没选项时的占位符
     * @return 返回封装好的筛选框组件
     */
    private JComboBox<GitComboBoxItem> createGitCustomFilterBox (Supplier<java.util.List<GitComboBoxItem>> supplier,
//                                                                      int lastSelectedIndex,
//                                                                      String fieldName,
                                                                      String placeHolder,
                                                                 JComboBox<GitComboBoxItem> sourceBranchCombo
                                                                 ) {
        // ComboBox代表筛选框  里面装着一个个ComboBoxItem
        ComboBox<GitComboBoxItem> comboBox = new ComboBox<>();
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            // 获得筛选框数据来源
            List<GitComboBoxItem> comboBoxItems = supplier.get();
            ApplicationManager.getApplication().invokeLater(() -> {
                comboBox.setModel(new DefaultComboBoxModel<>(ArrayUtil.toArray(comboBoxItems, GitComboBoxItem.class)));
                comboBox.setSelectedIndex(-1);
//                comboBoxLastSelectedItem[lastSelectedIndex] = nullIndex;
                comboBox.setRenderer(new GitCheckmarkRenderer(comboBox, "请选择应用"));
                comboBox.setPreferredSize(new Dimension(200, 30));
                sourceBranchCombo.setPreferredSize(new Dimension(200, 30));
                sourceBranchCombo.setRenderer(new GitCheckmarkRenderer(sourceBranchCombo, placeHolder));
//                if (CollUtil.isNotEmpty(comboBoxItems)) {
//                    GitComboBoxItem gitComboBoxItem = comboBoxItems.get(0);
//                    try {
//                        BaseResponse<List<Branch>> baseResponse = maXiaoBaoApi.listBranchesUnderAppId(Long.parseLong(gitComboBoxItem.key)).execute().body();
//                        List<GitComboBoxItem> branchesItem = baseResponse.getData().stream()
//                                .map(branch -> new GitComboBoxItem(branch.getId().toString(), branch.getBranchName()))
//                                .collect(Collectors.toList());
//                        sourceBranchCombo.setModel(new DefaultComboBoxModel<>(ArrayUtil.toArray(branchesItem, GitComboBoxItem.class)));
//                        sourceBranchCombo.setRenderer(new GitCheckmarkRenderer(sourceBranchCombo, "请选择源分支"));
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }

                comboBox.addActionListener(e -> {
                    GitComboBoxItem selectedItem = (GitComboBoxItem) comboBox.getSelectedItem();
                    try {
                        if (selectedItem != null) {
                            BaseResponse<List<Branch>> baseResponse = maXiaoBaoApi.listBranchesUnderAppId(Long.parseLong(selectedItem.key)).execute().body();
                            List<GitComboBoxItem> branchesItem = baseResponse.getData().stream()
                                    .map(branch -> new GitComboBoxItem(branch.getId().toString(), branch.getBranchName()))
                                    .collect(Collectors.toList());
                            sourceBranchCombo.setModel(new DefaultComboBoxModel<>(ArrayUtil.toArray(branchesItem, GitComboBoxItem.class)));
                            //sourceBranchCombo.setSelectedIndex(-1);
                            sourceBranchCombo.setRenderer(new GitCheckmarkRenderer(sourceBranchCombo, CollUtil.isEmpty(branchesItem) ? "当前应用暂无分支" : placeHolder));
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                // 添加点击搜索逻辑
//                comboBox.addActionListener(new ActionListener() {
//                    // 防止递归调用
//                    private boolean ignoreAction = false;
//
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        if (ignoreAction) {
//                            return;
//                        }
//                        ignoreAction = true;
//
//                        int selectedIndex = comboBox.getSelectedIndex();
//                        // 获取下拉列表选中的值
//                        TaskAction.ComboBoxItem selectedItem = (TaskAction.ComboBoxItem) comboBox.getSelectedItem();
//                        // 如果选择的还是上次筛选的条件  则取消它（将对应条件字段置null）
//                        if (comboBoxLastSelectedItem[lastSelectedIndex] == selectedIndex) {
//                            ReflectUtil.setFieldValue(queryRequest, fieldName, null);
//                            comboBox.setSelectedIndex(nullIndex);
//                            comboBoxLastSelectedItem[lastSelectedIndex] = nullIndex;
//                        } else {
//                            // 如果这次选择的不是上次选的条件  就将对应条件字段置为它 同时记录下这个筛选框上次选择的条件
//                            assert selectedItem != null;
//                            String selectedItemKey = selectedItem.key;
//                            ReflectUtil.setFieldValue(queryRequest, fieldName, Integer.valueOf(selectedItemKey));
//                            comboBoxLastSelectedItem[lastSelectedIndex] = selectedIndex;
//                        }
//
//                        ignoreAction = false;
//
//                        queryRequest.setCurrent(PageConstant.FIRST_PAGE);
//                        currentPage[0] = PageConstant.FIRST_PAGE;
//                        searchAndLoadData(queryRequest);
//                    }
//                });
            });
        });
        return comboBox;
    }


    // 将表格数据显示出来
    // 把模型数据 tableModel 放到 JBTable 中， 再把 JBTable 放到 JBScrollPane 中，最后再放到 tabPanel 中
    private void loadBranchesDataTable(JBPanel<?> tabPanel, Long taskId, Long userId) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            BranchesSearchRequest branchesSearchRequest = new BranchesSearchRequest();
            branchesSearchRequest.setTaskId(taskId);
            branchesSearchRequest.setUserId(userId);

            try {
                List<BranchVO> branchVOList = maXiaoBaoApi.listMyBranchesUnderTaskId(branchesSearchRequest).execute().body().getData();
                // 创建表格数据模型
                ApplicationManager.getApplication().invokeLater(() -> {
                    tableModel = new MTabModel();
                    tableModel.addColumn("ID");
                    tableModel.addColumn("应用名");
                    tableModel.addColumn("分支名");
                    tableModel.addColumn("操作");
                    tableModel.addColumn("git命令");

                    // 将数据添加到表格模型
                    if (CollUtil.isNotEmpty(branchVOList)) {
                        for (BranchVO branchVO : branchVOList) {
                            tableModel.addRow(new Object[]{branchVO.getId(), branchVO.getAppName(), branchVO.getBranchName(), branchVO.getIsMine() == 1 ? "创建的分支" : "关联的分支", branchVO.getGitCommand()});
                        }
                    }
                    // 把 tableModel 放到 JBTable 中，并给 JBTable绑定双击事件
                    JBTable table = PanelUtil.createGitTablePanel(tableModel, (tempTable, mouseEvent) -> {
                        int selectedRow = tempTable.getSelectedRow();
                        if (selectedRow != -1) {
                            // 获取选中行的数据
                            String gitCmd = (String) tempTable.getValueAt(selectedRow, 4);
                            JOptionPane.showMessageDialog(null, gitCmd, "提示", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });

                    // 将表格添加到滚动面板
                    JBScrollPane scrollPane = new JBScrollPane(table);
                    // 确保表格充满视口
                    scrollPane.setViewportView(table);
                    tabPanel.add(scrollPane, BorderLayout.CENTER);
                    tabPanel.validate();

//                    // 暴露 table，使得重新设置列名后再重新设置列宽
//                    this.table = table;

                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

    private void searchAndReloadData(Long taskId, Long userId) {
        if (tableModel == null) {
            return;
        }
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            BranchesSearchRequest branchesSearchRequest = new BranchesSearchRequest();
            branchesSearchRequest.setTaskId(taskId);
            branchesSearchRequest.setUserId(userId);
            try {
                List<BranchVO> branchVOList = maXiaoBaoApi.listMyBranchesUnderTaskId(branchesSearchRequest).execute().body().getData();
                ApplicationManager.getApplication().invokeLater(() -> {
                    tableModel.setRowCount(0);
                    // 将数据添加到表格模型
                    if (CollUtil.isNotEmpty(branchVOList)) {
                        for (BranchVO branchVO : branchVOList) {
                            tableModel.addRow(new Object[]{branchVO.getId(), branchVO.getAppName(), branchVO.getBranchName(), branchVO.getIsMine() == 1 ? "创建的分支" : "关联的分支", branchVO.getGitCommand()});
                        }
                    }
                    tableModel.fireTableDataChanged();
                });
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        });
    }

    // 自定义渲染器，处理对号的显示
    private static class GitCheckmarkRenderer extends DefaultListCellRenderer {
        private final JComboBox<GitComboBoxItem> comboBox;
        private final String placeHolder;

        public GitCheckmarkRenderer(JComboBox<GitComboBoxItem> comboBox, String placeHolder) {
            this.comboBox = comboBox;
            this.placeHolder = placeHolder;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (index == comboBox.getSelectedIndex()) {
                label.setText(value + " ✅");
            } else {
                label.setText(value != null ? value.toString() : "");
            }
            if (value == null) {
                label.setText(placeHolder);
            }
            return label;
        }
    }

    // 自定义类，用于存储 key-value
    @AllArgsConstructor
    public static class GitComboBoxItem {
        private String key;
        private String value;

        @Override
        public String toString() {
            // 显示在 JComboBox 中的内容
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof GitComboBoxItem) {
                GitComboBoxItem item = (GitComboBoxItem) o;
                return key.equals(item.key);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }
}
