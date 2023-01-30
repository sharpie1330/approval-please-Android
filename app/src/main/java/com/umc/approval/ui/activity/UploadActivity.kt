package com.umc.approval.ui.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.amazonaws.regions.Regions
import com.umc.approval.API
import com.umc.approval.R
import com.umc.approval.data.dto.opengraph.OpenGraphDto
import com.umc.approval.data.dto.upload.post.ApprovalUploadDto
import com.umc.approval.databinding.ActivityUploadBinding
import com.umc.approval.databinding.ActivityUploadLinkDialogBinding
import com.umc.approval.databinding.ActivityUploadTagDialogBinding
import com.umc.approval.ui.adapter.upload_activity.ImageUploadAdapter
import com.umc.approval.ui.viewmodel.approval.UploadDocumentViewModel
import com.umc.approval.ui.adapter.upload_activity.UploadHashtagRVAdapter
import com.umc.approval.util.CrawlingTask
import com.umc.approval.util.S3Util
import com.umc.approval.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding

    /**서버로 보낼 데이터*/
    private lateinit var uploadFile: ApprovalUploadDto

    /**Upload Viewmodel*/
    lateinit var viewModel: UploadDocumentViewModel

    /**Image Adapter*/
    private lateinit var imageRVAdapter : ImageUploadAdapter

    /**Open graph Manger*/
    private lateinit var manager: InputMethodManager

    /*태그 다이얼로그*/
    private lateinit var tagDialogBinding : ActivityUploadTagDialogBinding
    private lateinit var tagButton : ImageButton
    private lateinit var tagTextView : TextView
    /*태그 데이터*/
    private lateinit var tagString : String
    private lateinit var tagArray : List<String>

    /*링크 다이얼로그*/
    private lateinit var linkDialogBinding : ActivityUploadLinkDialogBinding
    private lateinit var linkButton : ImageButton
    private lateinit var linkTextView : TextView
    private lateinit var linkEraseButton : ImageButton
    private lateinit var opengraphText : TextView
    private lateinit var opengraphUrl : TextView
    private lateinit var opengraphImage : ImageView
    private lateinit var opengraphId : ConstraintLayout
    private lateinit var linkDialog: Dialog

    /*다이얼로그 버튼*/
    private lateinit var dialogCancelButton : Button
    private lateinit var dialogConfirmButton : Button
    private lateinit var tagDialogEditText :EditText
    private lateinit var linkDialogEditText :EditText



    /*링크 데이터*/
    private lateinit var linkString :String

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        /*View Model 초기화*/
        viewModel = ViewModelProvider(this).get(UploadDocumentViewModel::class.java)

        /*Open Graph manager 초기화*/
        manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        //opengraph 초기화
        binding.openGraph.isVisible = false

        /*부서명 선택 Spinner*/
        /*서버연동 : 부서명 카테고리 받아서 departments 수정*/
        select_category()

        /*이미지 선택시 실행되는 메서드*/
        observe_pic()

        /*이미지 선택 이벤트*/
        image_upload_event()

        /*move to approval fragment*/
        back_to_approval()

        /*link observer*/
        link_observe()

        /*opengraph observer*/
        opengraph_observe()

        /*제출 버튼 클릭 이벤트 후 approval fragment 로 이동*/
        upload_item()

        /**init dialog*/
        showLinkDialog()

        /*태그 입력 다이얼로그 열기*/
        tagButton = binding.uploadTagBtn
        tagString = ""
        tagButton.setOnClickListener{
            showTagDialog()
        }

        /*링크 첨부 다이얼로그*/
        linkButton = binding.uploadLinkBtn
        linkString = ""
        linkButton.setOnClickListener{
            showLinkDialog()
        }
    }

    /**파일을 업로드하는 로직*/
    private fun upload_item() {
        binding.uploadSubmitBtn.setOnClickListener {

            uploadFile = ApprovalUploadDto(0, binding.uploadTitleEt.text.toString()
                , binding.uploadContentEt.text.toString())

            //링크가 있을 경우
            if (viewModel.opengraph.value != null) {
                uploadFile.opengraph = viewModel.opengraph.value
            }

            //사진이 있을 경우
            if (viewModel.pic.value != null) {
                S3_connect()
            }

            //태그가 있을 경우
            if (viewModel.tags.value != null) {
                uploadFile.tag = viewModel.tags.value
            }

            viewModel.post_document(uploadFile)
            finish()
        }
    }

    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }

    fun Spinner.avoidDropdownFocus() {
        val listPopup = Spinner::class.java
            .getDeclaredField("mPopup")
            .apply { isAccessible = true }
            .get(this)
        if (listPopup is ListPopupWindow) {
            val popup = ListPopupWindow::class.java
                .getDeclaredField("mPopup")
                .apply { isAccessible = true }
                .get(listPopup)
            if (popup is PopupWindow) {
                popup.isFocusable = false
                popup.height = 100
            }
        }
    }
    /**category spinner*/
    private fun select_category() {
        var departments = arrayOf(
            "디지털 기기",
            "생활 가전",
            "생활 용품",
            "가구 / 인테리어",
            "주방 / 건강",
            "출산 / 유아동",
            "패션 의류 / 잡화",
            "뷰티 / 미용",
            "스포츠 / 레저 / 헬스",
            "취미 / 게임 / 완구",
            "문구 / 오피스",
            "도서 / 음악",
            "티켓 / 교환권",
            "식품",
            "동물 / 식물",
            "영화 / 공연",
            "자동차 / 공구",
            "기타 물품",
        )

        val adapter = object : ArrayAdapter<String>(this, R.layout.item_upload_spinner) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

                val v = super.getView(position, convertView, parent)

                if (position == count) {
                    //마지막 포지션의 textView 를 힌트 용으로 사용합니다.
                    (v.findViewById<View>(R.id.tvItemSpinner) as TextView).text = ""
                    //아이템의 마지막 값을 불러와 hint로 추가해 줍니다.
                    (v.findViewById<View>(R.id.tvItemSpinner) as TextView).hint = getItem(count)
                }

                return v
            }

            override fun getCount(): Int {
                //마지막 아이템은 힌트용으로만 사용하기 때문에 getCount에 1을 빼줍니다.
                return super.getCount() - 1
            }
        }

        adapter.addAll(departments.toMutableList())

        adapter.add("부서를 선택해주세요.")

        val newsSourceSpinner = findViewById<View>(R.id.upload_department_spinner) as Spinner


        val popup = Spinner::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true

        // Get private mPopup member variable and try cast to ListPopupWindow
        val popupWindow = popup[newsSourceSpinner] as ListPopupWindow

        // set popup height
        popupWindow.height = 250

        binding.uploadDepartmentSpinner.avoidDropdownFocus()
        binding.uploadDepartmentSpinner.adapter = adapter
        binding.uploadDepartmentSpinner.setSelection(adapter.count)
        binding.uploadDepartmentSpinner.dropDownVerticalOffset = dipToPixels(40f).toInt()
        binding.uploadDepartmentSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    /**move to approval fragment*/
    private fun back_to_approval() {
        binding.backToApproval.setOnClickListener {
            finish()
        }
    }

    /*태그 다이얼로그*/
    private fun showTagDialog(){
        val tagDialog = Dialog(this);
        tagDialogBinding = ActivityUploadTagDialogBinding.inflate(layoutInflater)

        tagDialog.setContentView(tagDialogBinding.root)
        tagDialog.setCanceledOnTouchOutside(true)
        tagDialog.setCancelable(true)
        dialogCancelButton = tagDialogBinding.uploadTagDialogCancelButton
        dialogConfirmButton = tagDialogBinding.uploadTagDialogConfirmButton
        tagDialogEditText = tagDialogBinding.uploadTagDialogEt

        /*취소버튼*/
        dialogCancelButton.setOnClickListener{
            tagDialog.dismiss()
        }


        /*확인버튼*/
        dialogConfirmButton.setOnClickListener{
            tagTextView = binding.uploadTagTv;
            // tagTextView.setText(tagDialogEditText.text.toString())
            tagString = tagDialogEditText.text.toString()

            if(tagString.length>1){
                tagArray = tagString.split(" ")

                tagTextView.setText("("+tagArray.size+"/4)");

                val dataRVAdapter = UploadHashtagRVAdapter(tagArray)
                binding.uploadHashtagItem.adapter = dataRVAdapter
                binding.uploadHashtagItem.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

            }else{
                tagTextView.setText("태그를 입력하세요 (0/4)")
            }

            tagDialog.dismiss()
        }


        /*태그 입력 동적코드*/
        tagDialogEditText.setOnClickListener(View.OnClickListener {
            if(tagDialogEditText.text.toString().length==0){
                tagDialogEditText.setText("#");
                tagDialogEditText.setSelection(tagDialogEditText.text.length)
            }
        })

        //val spannableStringBuilder = SpannableStringBuilder(text)
        tagDialogEditText.setText(tagString)

       var originText = ""
        var hashtagCount = 0;

        tagDialogEditText.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                originText = s.toString();
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                var text = s.toString()
                var textLength = text.length-1;
                if(hashtagCount >= 4){
                    tagDialogEditText.setText(originText)
                }

                if(text[textLength] == ' '){
                    val timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            runOnUiThread {
                                hashtagCount = 0;
                                for(i in 0..textLength){
                                    if(text[i] == ' '){
                                        hashtagCount++;
                                        val spannableStringBuilder = SpannableStringBuilder(s?.toString() ?: "")
                                        spannableStringBuilder.setSpan(
                                            ForegroundColorSpan(Color.parseColor("#6C39FF")),
            //                            BackgroundColorSpan(Color.parseColor("#CBB9FF")),
                                            0,
                                            i,
                                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                        )
                                        if(hashtagCount <= 4){
                                            tagDialogEditText.setText(spannableStringBuilder.append('#'))
                                            tagDialogEditText.setSelection(tagDialogEditText.text.length)
                                        }
                                    }
                                }
                            }
                        }
                    }, 10)
                }
            }
        })
        tagDialog.show()
    }

    /*링크 첨부 다이얼로그*/
    private fun showLinkDialog() {
        linkDialog = Dialog(this)
        linkDialogBinding = ActivityUploadLinkDialogBinding.inflate(layoutInflater)

        linkDialog.setContentView(linkDialogBinding.root)
        linkDialog.setCanceledOnTouchOutside(true)
        linkDialog.setCancelable(true)

        //Dialog 초기화
        dialogCancelButton = linkDialogBinding.uploadLinkDialogCancelButton
        dialogConfirmButton = linkDialogBinding.uploadLinkDialogConfirmButton
        linkDialogEditText = linkDialogBinding.uploadLinkDialogEt
        linkEraseButton = linkDialogBinding.uploadLinkEraseBtn
        opengraphText = linkDialogBinding.openGraphText
        opengraphUrl = linkDialogBinding.openGraphUrl
        opengraphImage = linkDialogBinding.openGraphImage
        opengraphId = linkDialogBinding.openGraph

        //Dialog Opengraph 초기화
        opengraphId.isVisible = false

        /*취소버튼*/
        dialogCancelButton.setOnClickListener {
            linkDialogEditText.setText(linkString) //초기화
            linkDialog.dismiss()
        }

        /*확인버튼*/
        dialogConfirmButton.setOnClickListener {
            linkString = linkDialogEditText.text.toString()
            linkDialog.dismiss()
        }

        linkEraseButton.setOnClickListener{
            linkString = ""
            linkDialogEditText.setText(linkString)
        }

        /*링크 첨부 다이얼로그*/
        linkButton = binding.uploadLinkBtn
        linkButton.setOnClickListener{
            linkDialog.show()
        }

        linkDialogEditText.setText(linkString)

        /*link url 바뀔때 마다 적용*/
        editLinkUrl()
    }

    /**observe graph live data 변경 시*/
    private fun opengraph_observe() {
        viewModel.opengraph.observe(this) {
            opengraphId.isVisible = true
            opengraphText.setText(it.title)
            opengraphUrl.setText(it.url)
            opengraphImage.load(it.image)

            binding.openGraph.isVisible = true
            binding.uploadLinkTv.isVisible = false
            binding.openGraphText.setText(it.title)
            binding.openGraphUrl.setText(it.url)
            binding.openGraphImage.load(it.image)
        }
    }

    /**link live data 변경 시*/
    private fun link_observe() {
        viewModel.link.observe(this) {

            //link 변경시 opengraph 초기화
            opengraphId.isVisible = false
            binding.openGraph.isVisible = false
            binding.uploadLinkTv.isVisible = true

            manager.hideSoftInputFromWindow(
                currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
            var openGraphDto = OpenGraphDto("", "", "", "", "")

            CoroutineScope(Dispatchers.IO).launch {
                val elements = CrawlingTask.getElements(it)
                elements?.let {
                    it.forEach { el ->
                        when (el.attr("property")) {
                            "og:url" -> {
                                el.attr("content")?.let { content ->
                                    openGraphDto.url = content
                                }
                            }
                            "og:site_name" -> {
                                el.attr("content")?.let { content ->
                                    openGraphDto.siteName = content
                                }
                            }
                            "og:title" -> {
                                el.attr("content")?.let { content ->
                                    openGraphDto.title = content
                                }
                            }
                            "og:description" -> {
                                el.attr("content")?.let { content ->
                                    openGraphDto.description = content
                                }
                            }
                            "og:image" -> {
                                el.attr("content")?.let { content ->
                                    openGraphDto.image = content
                                }
                            }
                        }
                    }
                }
                if (openGraphDto.title.toString() != "" && openGraphDto.description.toString() != "") {
                    viewModel.setOpengraph(openGraphDto)
                }
            }
        }
    }

    /**image upload event*/
    @RequiresApi(Build.VERSION_CODES.M)
    private fun image_upload_event() {
        binding.uploadImageBtn.setOnClickListener {
            when {
                // 갤러리 접근 권한이 있는 경우
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> showGallery(this)

                // 갤러리 접근 권한이 없는 경우 && 교육용 팝업을 보여줘야 하는 경우
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                -> showPermissionContextPopup()

                // 권한 요청 하기
                else -> requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Utils.PICK_IMAGE_FROM_GALLERY_PERMISSION
                )
            }
        }
    }

    /**Image observer*/
    private fun observe_pic() {
        viewModel.pic.observe(this) {
            imageRVAdapter = ImageUploadAdapter(viewModel.pic.value!!)
            binding.uploadItem.adapter = imageRVAdapter
            binding.uploadItem.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    /**move to gallery*/
    private fun showGallery(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        activity.startActivityForResult(intent, Utils.PICK_IMAGE_FROM_GALLERY)
    }

    /**권한이 없을때 권한 등록 팝업 함수*/
    @RequiresApi(Build.VERSION_CODES.M)
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Utils.PICK_IMAGE_FROM_GALLERY_PERMISSION
                )
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }

    /**사진 선택(갤러리에서 나온) 이후 실행되는 함수*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Utils.PICK_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            val list = mutableListOf<Uri>()

            data?.let { it ->
                if (it.clipData != null) {   // 사진을 여러개 선택한 경우
                    val count = it.clipData!!.itemCount
                    if (count > 4) {
                        Toast.makeText(this, "사진은 4장까지 선택 가능합니다.", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }
                    for (i in 0 until count) {
                        val imageUri = it.clipData!!.getItemAt(i).uri
                        list.add(imageUri)
                        viewModel.setImage(list)
                    }
                } else {      // 1장 선택한 경우
                    val imageUri = it.data!!
                    list.add(imageUri)
                    viewModel.setImage(list)
                }
            }
        }
    }

    /**권한 요청 승인 이후 실행되는 함수*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Utils.PICK_IMAGE_FROM_GALLERY_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    showGallery(this)
                else
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * S3
     * */
    /*S3 connect*/
    private fun S3_connect() {

        val imageList = mutableListOf<String>()

        for (uri in viewModel.pic.value!!) {

            /**uri 변환*/
            val realPathFromURI = getRealPathFromURI(uri)
            val file = File(realPathFromURI)

            /**S3에 저장*/
            S3Util().getInstance()
                ?.setKeys(API.S3_ACCESS_KEY, API.S3_ACCESS_SECRET_KEY)
                ?.setRegion(Regions.AP_NORTHEAST_2)
                ?.uploadWithTransferUtility(
                    this,
                    "approval-please/approval", file, "test"
                )

            imageList.add("aws")
        }

        uploadFile.images = imageList
    }

    /*File Uri for S3 connect*/
    private fun getRealPathFromURI(uri: Uri): String {
        val buildName = Build.MANUFACTURER
        if(buildName.equals("Xiaomi")) {
            return uri.path.toString()
        }

        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        var cursor = contentResolver.query(uri, proj, null, null, null)

        if(cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }

        return cursor.getString(columnIndex)
    }

    /**
     * Open Graph
     * */
    /*url 형식 메서드*/
    private fun getUrl(url: String) : String {
        return if(url.contains("http://") || url.contains("https://")) url
        else "https://".plus(url)
    }

    /*link 변경 메서드*/
    private fun editLinkUrl() {
        //addTextChangedListener는 editText속성을 가지는데 값이 변할때마다 viewModel로 결과가 전달
        linkDialogEditText.addTextChangedListener { text: Editable? ->
            text?.let {
                var url = it.toString()
                viewModel.setLink(getUrl(url.trim()))
            }
        }
    }
}
