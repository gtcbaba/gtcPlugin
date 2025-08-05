package com.github.gtcbaba.gtcplugin.api;

import com.github.gtcbaba.gtcplugin.model.common.BaseResponse;
import com.github.gtcbaba.gtcplugin.model.common.Page;
import com.github.gtcbaba.gtcplugin.model.common.PageRequest;
import com.github.gtcbaba.gtcplugin.model.dto.*;
import com.github.gtcbaba.gtcplugin.model.response.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

/**
 * 码小保接口
 *
 * @author pine
 */
public interface MaXiaoBaoApi {

    /**
     * 获取登录用户信息
     */
    @GET("user/get/login")
    Call<BaseResponse<User>> getLoginUser();

    /**
     * 获取登录用户信息
     */
    @POST("user/login")
    Call<BaseResponse<User>> userLogin(
            @Body UserLoginRequest userLoginRequest
    );

    /**
     * 用户退出登录
     */
    @POST("user/logout")
    Call<BaseResponse<Boolean>> userLogout();

    @POST("task/list/page")
    Call<BaseResponse<Page<Task>>> getTaskList(
            @Body TaskTypeQueryRequest queryRequest
    );

    @GET("task/apps")
    Call<BaseResponse<List<App>>> listAppsUndercodeRepositoryId(
           @Query("codeRepositoryId") long codeRepositoryId
    );

    @GET("branch/app/list")
    Call<BaseResponse<List<Branch>>> listBranchesUnderAppId(
            @Query("appId") long appId
    );

    @POST("branch/my")
    Call<BaseResponse<List<BranchVO>>> listMyBranchesUnderTaskId(
            @Body BranchesSearchRequest branchesSearchRequest
    );

    @POST("branch/add")
    Call<BaseResponse<Boolean>> addBranchesUnderApps(
            @Body BranchesAddRequest branchesAddRequest
    );

    @POST("branch/add/link")
    Call<BaseResponse<Boolean>> addLinksToBranches(
            @Body BranchesAddLinkRequest branchesAddLinkRequest
    );






//    /**
//     * 获取题库列表
//     */
//    @POST("questionBankCategory/list_questionBank")
//    Call<BaseResponse<Page<QuestionBank>>> getQuestionBankList(
//            @Body QuestionBankCategoryBankQueryRequest queryRequest
//    );
//
//    /**
//     * 获取题目列表
//     */
//    @POST("question/list/page/vo")
//    Call<BaseResponse<Page<Question>>> getQuestionList(
//            @Body QuestionQueryRequest queryRequest
//    );
//
//    /**
//     * 搜索题目列表
//     */
//    @POST("question/search")
//    Call<BaseResponse<Page<Question>>> searchQuestionList(
//            @Body QuestionQueryRequest queryRequest
//    );
//
//    @POST("question_bank/list_question")
//    Call<BaseResponse<Page<Question>>> listQuestionByQuestionBank(
//            @Body QuestionQueryRequest queryRequest
//    );
//
//
//    /**
//     * 获取刷题信息
//     *
//     * @param questionBankId 题库 id
//     * @param questionId    题目 id
//     * @return {@link Call }<{@link BaseResponse }<{@link DoQuestionInfoVO }>>
//     */
//    @GET("question_bank/do_question_info")
//    Call<BaseResponse<DoQuestionInfoVO>> getDoQuestionInfo(
//            @Query("questionBankId") long questionBankId,
//            @Query("questionId") long questionId
//    );
//
//    /**
//     * 获取回答列表
//     */
//    @POST("question_answer/list/by_question")
//    Call<BaseResponse<Page<QuestionAnswer>>> listQuestionAnswerByQuestionId(
//            @Body QuestionAnswerQueryRequest queryRequest
//    );
//
//    /**
//     * 获取题库分类列表
//     */
//    @POST("questionBankCategory/list")
//    Call<BaseResponse<List<QuestionBankCategory>>> listQuestionBankCategory(
//            @Body PageRequest pageRequest
//    );
//
//    /**
//     * 获取题库列表
//     */
//    @POST("question_bank/list/page/vo")
//    Call<BaseResponse<Page<QuestionBank>>> listQuestionBankVoByPage(
//            @Body QuestionBankQueryRequest queryRequest
//    );
//
//    /**
//     * 获取标签分类列表
//     */
//    @POST("tagCategory/list")
//    Call<BaseResponse<List<TagGroup>>> listTagCategory(
//            @Body TagCategoryQueryRequest queryRequest
//    );



}
