package com.umc.approval.data.repository.comment

import android.util.Log
import com.umc.approval.data.dto.comment.get.CommentListDto
import com.umc.approval.data.dto.comment.post.CommentPostDto
import com.umc.approval.data.retrofit.instance.RetrofitInstance.commentAPI
import okhttp3.ResponseBody
import retrofit2.Call

/**
 * Approval Fragment Repository
 */
class CommentRepository {

    fun getDocumentComments(accessToken: String?= null, documentId: Int?=null,
                    toktokId : Int?=null, reportId : Int?=null): Call<CommentListDto> {
        return commentAPI.getComments(documentId = documentId)
    }

    fun getTokComments(accessToken: String?= null, documentId: Int?=null,
                            toktokId : Int?=null, reportId : Int?=null): Call<CommentListDto> {
        return commentAPI.getComments(toktokId = toktokId)
    }

    fun getReportComments(accessToken: String?= null, documentId: Int?=null,
                            toktokId : Int?=null, reportId : Int?=null): Call<CommentListDto> {
        return commentAPI.getComments(reportId = reportId)
    }

    fun postComments(accessToken: String, commentPostDto: CommentPostDto): Call<ResponseBody> {
        return commentAPI.postComment(accessToken, commentPostDto)
    }

    fun deleteComment(accessToken: String, commentId : String): Call<ResponseBody> {
        return commentAPI.deleteComment(accessToken, commentId)
    }
}