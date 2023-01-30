package com.umc.approval.ui.adapter.community_fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.umc.approval.App.Companion.context
import com.umc.approval.data.dto.community.get.CommunityReport
import com.umc.approval.data.dto.community.get.CommunityReportDto
import com.umc.approval.databinding.CommunityReportItemBinding
import com.umc.approval.ui.adapter.community_post_activity.CommunityImageRVAdapter
import com.umc.approval.ui.adapter.upload_activity.UploadHashtagRVAdapter

/**
 * 결재 톡톡 및 보고서를 받아와 연결해주는 RV Adapter
 * */
class CommunityReportItemRVAdapter(private val items : CommunityReportDto) : RecyclerView.Adapter<CommunityReportItemRVAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: CommunityReportItemBinding) : RecyclerView.ViewHolder(binding.root) {

            fun binding(data : CommunityReport) {

                if (data.reportLink != null) {
                    binding.reportOpenGraphImage.load(data.reportLink.get(0).image)
                    binding.reportOpenGraphText.setText(data.reportLink.get(0).title)
                    binding.reportOpenGraphUrl.setText(data.reportLink.get(0).url)

                /*결재서류 부분*/
                if(data.documentImageUrl == null){
                    binding.communityDocumentLayout.ivApprovalReportThumbnail.isVisible = false
                }else{
                    binding.communityDocumentLayout.ivApprovalReportThumbnail.load(data.documentImageUrl.get(0))
                    if(data.documentImageUrl.size > 1){
                        binding.communityDocumentLayout.documentImageCountTv.text = "+" + (data.documentImageUrl.size - 1).toString()
                    }
                }

                binding.communityDocumentLayout.documentTitle.text = data.documentTitle
                binding.communityDocumentLayout.documentContent.text = data.documentContent

                /*결재 보고서 부분*/
                binding.reportCategoryItemText.text = data.reportContent// 내용
                binding.reportUserName.text = data.nickname
                binding.reportViewText.text = data.view.toString()
                binding.tvLikeCount.text = data.likedCount.toString()
                binding.tvCommentCount.text = data.commentCount.toString()

                if(data.reportImageUrl == null){
                    binding.uploadImageLayout.isVisible = false
                }else{
                    var imageRVAdapter = CommunityImageRVAdapter(data.reportImageUrl as ArrayList<String>)
                    binding.imageRv.adapter = imageRVAdapter
                    binding.imageRv.layoutManager =
                        LinearLayoutManager(context(), LinearLayoutManager.HORIZONTAL, false)
                }

                if(data.reportTag != null){
                    binding.uploadHashtagItem.isVisible = false
                }else{
                    val dataRVAdapter = UploadHashtagRVAdapter(data.reportTag)
                    binding.uploadHashtagItem.adapter = dataRVAdapter
                    binding.uploadHashtagItem.layoutManager = LinearLayoutManager(context(), RecyclerView.HORIZONTAL, false)
                }

                if (data.reportLink != null) {
                    binding.reportOpenGraphImage.load(data.reportLink[0].image)
                    binding.reportOpenGraphText.setText(data.reportLink[0].title)
                    binding.reportOpenGraphUrl.setText(data.reportLink[0].url)
                }else{
                    binding.reportLinkLayout.isVisible = false
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CommunityReportItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding(items.communityReport[position])
        if (itemClick != null){
            holder.binding.communityDocumentLayout.documentBtn.setOnClickListener(View.OnClickListener {
                itemClick?.move_to_report_activity()
            })
        }
    }

    override fun getItemCount(): Int {
        return items.communityReport.size
    }

    /**RV item click event*/
    interface ItemClick{ //인터페이스
        fun move_to_report_activity()
    }

    var itemClick: ItemClick? = null
}