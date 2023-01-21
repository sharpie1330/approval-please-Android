package com.umc.approval.data.retrofit.api

import com.umc.approval.data.dto.ApprovalPaperDto
import com.umc.approval.data.dto.CommunityPostDto
import com.umc.approval.data.dto.ApprovalReportDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface HomeAPI {
    /**
     * @Post
     * accessToken: 사용자 검증 토큰
     * category: 사용자가 선택한 카테고리(관심부서)
     * @Get
     * ApprovalPaperDto: 결재 서류(state, category, updatedAt, image, title, content, tag, viewCount, approveCount, rejectCount 정보) 리스트
     * 관심부서 결재서류 목록 조회 API
     */
    @GET("/documents/likes")
    @Headers("content-type: application/json")
    fun getInterestingCategoryDocuments(
        @Header("Authorization") accessToken: String,
        @Query("category") category: Int
    ): Call<ApprovalPaperDto>

    /**
     * @Post
     * accessToken: 사용자 검증 토큰
     * sortBy: 정렬 방식(인기순/최신순)
     * @Get
     * ApprovalPaperDto: 결재 서류(state, category, updatedAt, image, title, content, tag, viewCount, approveCount, rejectCount 정보) 리스트
     * 전체 부서 결재서류 목록 조회 API
     */
    @GET("/documents")
    @Headers("content-type: application/json")
    fun getDocuments(
        @Header("Authorization") accessToken: String,
        @Query("sortBy") sortBy: Int,
    ): Call<ApprovalPaperDto>

    /**
     * @Post
     * accessToken: 사용자 검증 토큰
     * sortBy: 정렬 방식(인기순)
     * @Get
     * CommunityPostDto: 결재톡톡 게시글(nickname, profileImage, content, imageUrl, viewCount, likeCount, commentCount, updatedAt 정보) 리스트
     * 인기 게시글 목록 조회 API
     */
    @GET("/community/toktok")
    @Headers("content-type: application/json")
    fun getHotPosts(
        @Header("Authorization") accessToken: String,
        @Query("sortBy") sortBy: Int
    ): Call<CommunityPostDto>

    /**
     * @Post
     * accessToken: 사용자 검증 토큰
     * @Get
     * ApprovalReportDto: 결재 보고서(nickname, profileImage, content, imageUrl, viewCount, likeCount, commentCount, updatedAt 정보) 리스트
     * 결재 보고서 목록 조회 API
     */
    @GET("/community/reports")
    @Headers("content-type: application/json")
    fun getReports(
        @Header("Authorization") accessToken: String
    ): Call<ApprovalReportDto>
}