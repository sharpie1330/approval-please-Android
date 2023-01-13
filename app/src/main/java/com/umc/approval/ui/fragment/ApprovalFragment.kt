package com.umc.approval.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.umc.approval.databinding.FragmentApprovalBinding
import com.umc.approval.ui.liz.ApprovalPaperListFragment

/**
 * Approval View
 * */
class ApprovalFragment : Fragment() {

    private var _binding : FragmentApprovalBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentApprovalBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.fabAddPost.setOnClickListener{
            // 게시글 작성 화면으로 이동
            Log.d("로그", "게시글 작성 버튼 클릭")
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FragmentManager 참조 가져오기
        childFragmentManager
            .beginTransaction()
            .replace(binding.fragmentContainer.id, ApprovalPaperListFragment())
            .commit()
    }

    /**
     * viewBinding이 더이상 필요 없을 경우 null 처리 필요
     */
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}