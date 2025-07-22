package com.github.gtcbaba.gtcplugin.config;

import com.github.gtcbaba.gtcplugin.adapter.DateTypeAdapter;
import com.github.gtcbaba.gtcplugin.api.MaXiaoBaoApi;
import com.github.gtcbaba.gtcplugin.api.interceptor.HeaderInterceptor;
import com.github.gtcbaba.gtcplugin.api.interceptor.LogInterceptor;
import com.github.gtcbaba.gtcplugin.api.interceptor.ResponseInterceptor;
import com.github.gtcbaba.gtcplugin.constant.CommonConstant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Date;

/**
 * 初始化请求工具
 *
 * @author pine
 */
public class ApiConfig {

    public static MaXiaoBaoApi maXiaoBaoApi;

    static {
        // 自定义 Gson 实例
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(new LogInterceptor())
                .addInterceptor(new ResponseInterceptor())
                .build();
        String mianShiYaBaseUrl = CommonConstant.API;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mianShiYaBaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        maXiaoBaoApi = retrofit.create(MaXiaoBaoApi.class);
    }

}
