package com.umc.approval.data.repository.community

import com.umc.approval.data.dto.community.get.CommunityReportDto
import com.umc.approval.data.dto.community.get.CommunityTokDto
import com.umc.approval.data.dto.communitydetail.get.CommunityItemDto
import com.umc.approval.data.dto.upload.post.TalkUploadDto
import com.umc.approval.data.retrofit.instance.RetrofitInstance.communityApi
import okhttp3.ResponseBody
import retrofit2.Call

/**
 * Community Repository
 * */
class CommunityRepository() {

    /**get toktoks*/
    fun get_toks(sortBy: Int):Call<CommunityTokDto> {
        return communityApi.get_community_tok_items(sortBy)
    }

    /**get reports*/
    fun get_reports(sortBy: Int):Call<CommunityReportDto> {
        return communityApi.get_community_report_items(sortBy)
    }

    /**get reports*/
    fun tok_upload(accessToken: String, talkUploadDto: TalkUploadDto):Call<ResponseBody> {
        return communityApi.upload_community_tok(accessToken, talkUploadDto)
    }


    /**get toktok detail*/
    fun get_tok_detail(accessToken: String, tokId: Int): Call<CommunityItemDto> {
        return communityApi.get_community_tok_detail(accessToken, tokId)
    }

    /**get report detail*/
    fun get_report_detail(accessToken: String, reportId: Int): Call<CommunityItemDto> {
        return communityApi.get_community_report_detail(accessToken, reportId)
    }
}