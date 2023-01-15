package com.umc.approval.ui.liz

data class ApprovalPaper(
    val approval_status: Boolean,
    val approval_result: Boolean,
    val title: String,
    val content: String,
    val approve_count: Int,
    val reject_count: Int,
    val views: Int,
    val department: String,
    val date: String,  // 서버 측에서 보내주는 데이터 가공 필요
)

data class Post(
    val user_profile_thumbnail: String,
    val user_nickname: String,
    val user_rank: String,
    val views: Int,
    val content: String,
    val comment_count: Int,
    val like_count: Int,
    val date: String,
)

data class ApprovalReport(
    val user_profile_thumbnail: String,
    val user_nickname: String,
    val user_rank: String,
    val title: String,
    val content: String,
    val image_path: String,
    val views: Int,
    val comment_count: Int,
    val like_count: Int,
    val date: String,
)