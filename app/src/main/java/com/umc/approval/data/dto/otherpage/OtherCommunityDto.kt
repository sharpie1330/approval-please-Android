package com.umc.approval.data.dto.otherpage

import com.google.gson.annotations.SerializedName
import com.umc.approval.data.dto.community.get.CommunityReportDto
import com.umc.approval.data.dto.community.get.CommunityTokDto

data class OtherCommunityDto(
    @SerializedName("toktokCount")
    val toktokCount : Int? = null,
    @SerializedName("toktokContent")
    val toktokContent : CommunityTokDto? = null,
    @SerializedName("reportCount")
    val reportCount : Int? = null,
    @SerializedName("reportContent")
    val reportContent : CommunityReportDto? = null
)
