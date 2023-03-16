package com.example.animal

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.animal.Adpater.GalleryAdapter
import com.example.animal.DTO.ContentDTO
import com.example.animal.databinding.ActivityBoardWriteBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.Date

//https://aries574.tistory.com/453?category=375976 참고
class BoardWrite : AppCompatActivity() {
    private val PICK_IMAGE_FROM_ALBUM = 0
    private var storage : FirebaseStorage? = null
    private var selectedImageUri : Uri? = null
    private lateinit var binding : ActivityBoardWriteBinding
    private var auth : FirebaseAuth? = null
    private var firestore : FirebaseFirestore? = null
    lateinit var galleryAdapter : GalleryAdapter

    var imageList : ArrayList<Uri> = ArrayList()


    // Camera Preview의 ImageView를 리스트 cameraPreviewList에 담아 cameraPreviewIndex 변수를 사용하여 하나씩 표시하도록 구현
    private val cameraPreviewList = mutableListOf<ImageView>()
    private var cameraPreviewIndex = 0

   /* private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            for (i in cameraPreviewList.indices) {
                if (cameraPreviewIndex < cameraPreviewList.size) {
                    Glide.with(this).load(uri).into(cameraPreviewList[cameraPreviewIndex])
                    cameraPreviewIndex++
                    break
                }
            }
        }
    }*/



    //스피너 코드
    private val categoryList = listOf("강아지","고양이","기타")

    private val spinnerAdapter by lazy {
        ArrayAdapter(this,android.R.layout.simple_spinner_item,categoryList).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //초기화 코드
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        galleryAdapter = GalleryAdapter(imageList, this)

        binding.imageRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.imageRecyclerView.adapter = galleryAdapter

        binding.boardCamera.setOnClickListener {
            //갤러리 호출
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            //멀티 선택 가능
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            activityResult.launch(intent)
        }




        /* Camera Preview ImageView 초기화
        cameraPreviewList.apply {
            add(binding.boardCameraPreview1)
            add(binding.boardCameraPreview2)
            add(binding.boardCameraPreview3)
            add(binding.boardCameraPreview4)
            add(binding.boardCameraPreview5)
            add(binding.boardCameraPreview6)
            add(binding.boardCameraPreview7)
            add(binding.boardCameraPreview8)
            add(binding.boardCameraPreview9)
            add(binding.boardCameraPreview10)
        }*/

        //앨범 오픈
        //binding.boardCamera.setOnClickListener {
        //    pickImage.launch("image/*")
       // }

        //스피너 설정
        binding.spinnerCategory.adapter = spinnerAdapter

        //open album
       // var photoPickerIntent = Intent(Intent.ACTION_PICK)
      //  photoPickerIntent.type = "image/*"
        //startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        //글쓰기 버튼 클릭

    }
    private val activityResult : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        //결과 코드 Ok, 결과값 null 아니면
        if(it.resultCode == RESULT_OK){
            //멀티 선택은 clipDate
            if(it.data!!.clipData != null){
                //선택한 이미지 갯수
                val count = it.data!!.clipData!!.itemCount

                for(index in 0 until count){
                    //이미지 담기
                    val imageUri = it.data!!.clipData!!.getItemAt(index).uri
                    //이미지 추가
                    imageList.add(imageUri)
                }
            }else{ //싱글 이미지
                val imageUri = it.data!!.data
                imageList.add(imageUri!!)
            }
            galleryAdapter.notifyDataSetChanged()
        }
    }


    fun contentUpload(){

        //Firebase storage에 저장할 이미지 파일 이름 생성
        var timestamp = SimpleDateFormat("yyyyMMDD_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_png"

        //이미지가 저장될 경로 지정
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        // Uri.EMPTY로 초기화
        var uri = selectedImageUri ?: Uri.EMPTY


        //Firebase Storage에 파일 업로드(Promise method
        storageRef?.putFile(selectedImageUri!!)?.continueWith{ task : Task<UploadTask.TaskSnapshot> ->
            return@continueWith storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->

            // contentDTO 객체 생성
            var contentDTO = ContentDTO().apply {

                //글 제목
                title = binding.editTitle.text.toString()

                // 카테고리 삽입
                category = categoryList[binding.spinnerCategory.selectedItemPosition]

                //픔종
                breed = binding.boardBreed.text.toString()

                //백신 접종
                vaccine = binding.boardVaccine.text.toString()

                //주소(업데이트 예정)
                where = binding.boardWhere.text.toString()

                // 라디오 버튼 상태 저장
                if (binding.boardRadioTrue.isChecked) {
                    // "O"가 선택된 경우
                    radio = "O"
                } else if (binding.boardRadioFalse.isChecked) {
                    // "X"가 선택된 경우
                    radio = "X"
                }

                // 내용 삽입
                content = binding.editContent.text.toString()

                // 이미지 URL 삽입
                imageUrl = uri?.toString() ?: ""

                // user uid 삽입
                uid = auth?.currentUser?.uid

                // user id 삽입
                userId = auth?.currentUser?.email

                //시간
              //  timestamp = System.currentTimeMillis()

            }

            // firestore에 데이터 저장
            firestore?.collection("images")?.document()?.set(contentDTO)

            setResult(Activity.RESULT_OK)

            finish()

        }
    }

}

