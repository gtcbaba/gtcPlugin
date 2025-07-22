package com.github.gtcbaba.gtcplugin.config;


import com.github.gtcbaba.gtcplugin.constant.PageConstant;
import com.github.gtcbaba.gtcplugin.model.response.User;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 全局配置
 *
 * @author pine
 */
@State(name = "GlobalState", storages = {@Storage("maxiaobao.xml")})
public class GlobalState implements PersistentStateComponent<GlobalState.State> {

    public static class State {
        public String token = "";
        public int pageSize = PageConstant.PAGE_SIZE;
        public User user = null;
    }

    private State state = new State();

    @Nullable
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    public void saveToken(String token) {
        state.token = token;
    }

    public void saveUser(User user) {
        state.user = user;
    }

    public String getSavedToken() {
        return state.token;
    }

    public User getSavedUser() {
        return state.user;
    }

    public void removeSavedToken() {
        state.token = "";
    }

    public void removeSavedUser() {
        state.user = null;
    }

    public static GlobalState getInstance() {
        return ApplicationManager.getApplication().getService(GlobalState.class);
    }
}
