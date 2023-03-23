package com.example.animal

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animal.adapter.GalleryAdapter
import com.example.animal.DTO.ContentDTO
import com.example.animal.databinding.ActivityBoardWriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class BoardWrite : AppCompatActivity() {
    var storage: FirebaseStorage? = null
    private lateinit var binding: ActivityBoardWriteBinding
    private var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    lateinit var galleryAdapter: GalleryAdapter

    var imageList: ArrayList<Uri> = ArrayList()


    //스피너 코드
    private val categoryList = listOf("강아지", "고양이", "기타")

    private val spinnerAdapter by lazy {
        ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList).apply {
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
        //스피너 설정
        binding.spinnerCategory.adapter = spinnerAdapter

        binding.btnUpload.setOnClickListener {
            contentUpload()
            val intent: Intent = Intent(this@BoardWrite, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        //결과 코드 Ok, 결과값 null 아니면
        if (it.resultCode == RESULT_OK) {
            //멀티 선택은 clipDate
            if (it.data!!.clipData != null) {
                //선택한 이미지 갯수
                val count = it.data!!.clipData!!.itemCount

                for (index in 0 until count) {
                    //이미지 담기
                    val imageUri = it.data!!.clipData!!.getItemAt(index).uri
                    //이미지 추가
                    imageList.add(imageUri)
                }
            } else { //싱글 이미지
                val imageUri = it.data!!.data
                imageList.add(imageUri!!)
            }
            galleryAdapter.notifyDataSetChanged()

        }

    }


    fun contentUpload() {

        val imageUrlList = ArrayList<String>()

        // Firebase storage에 저장할 이미지 파일 이름 생성
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        // 각각의 이미지를 Firebase Storage에 업로드
        imageList.forEachIndexed { index, imageUri ->
            val imageFileName = "IMAGE_" + timestamp + "_$index" + "_png"
            val filePath = getRealPathFromURI(this@BoardWrite, imageUri)

            // BitmapFactory를 사용하여 해당 이미지 파일을 Bitmap 객체로 변환하고, 크기를 줄여줍니다.
            // 이미지 파일을 Firebase Storage에 업로드합니다.
            val storageRef = storage?.reference?.child("images")?.child(imageFileName)
            val outputStream = ByteArrayOutputStream()

            // BitmapFactory를 사용하여 해당 이미지 파일을 Bitmap 객체로 변환하고, 크기를 줄여줍니다.
            val bitmap = BitmapFactory.decodeFile(filePath)
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, false)

            // 이미지 파일을 압축하여 outputStream에 씁니다.
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

            // outputStream을 ByteArray로 변환합니다.
            val data = outputStream.toByteArray()

            // 이미지 파일을 Firebase Storage에 업로드합니다.
            val uploadTask = storageRef?.putBytes(data)

            // 업로드된 이미지 파일의 URL을 imageUrlList에 추가합니다.
            uploadTask?.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                storageRef.downloadUrl
            }?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result.toString()
                    imageUrlList.add(downloadUri)

                    // 모든 이미지 파일의 업로드가 완료되면 firestore에 데이터를 저장합니다.
                    if (imageUrlList.size == imageList.size) {
                        val contentDTO = ContentDTO().apply {
                            //글 제목
                            title = binding.editTitle.text.toString()

                            if (binding.boardRadioMan.isChecked) {
                                //'남자 선택'
                                gender = "남아"
                            }else if(binding.boardRadioWomen.isChecked){
                                // '여자' 선택
                                gender = "여아"
                            }

                            // 카테고리 삽입
                            category = categoryList[binding.spinnerCategory.selectedItemPosition]

                            name = binding.boardName.text.toString()

                            age = binding.boardAge.text.toString()

                            price = binding.boardPrice.text.toString()

                            //픔종
                            breed = binding.boardBreed.text.toString()

                            //백신 접종
                            vaccine = binding.boardVaccine.text.toString()

                            //주소(업데이트 예정)
                            where = binding.boardWhere.text.toString()

                            // 라디오 버튼 상태 저장
                            if (binding.boardRadioSpay.isChecked) {
                                // "O"가 선택된 경우
                                spay = "O"
                            } else if (binding.boardRadioSpayFalse.isChecked) {
                                // "X"가 선택된 경우
                                spay = "X"
                            }

                            //시간
                            timeStamp = timestamp


                            // 내용 삽입
                            content = binding.editContent.text.toString()

                            //이미지 URL 삽입
                            imageUrl = imageUrlList.joinToString(separator = ",")

                            // user uid 삽입
                            uid = auth?.currentUser?.uid

                            // user id 삽입
                            userId = auth?.currentUser?.email

                            contentUid = UUID.randomUUID().toString() //게시글 랜덤 값 저장
                        }

                        firestore?.collection("images")?.add(contentDTO)
                            ?.addOnSuccessListener { documentReference ->
                                setResult(Activity.RESULT_OK)
                                finish()
                            }?.addOnFailureListener { e ->
                                Log.w("TAG", "Error adding document", e)
                            }
                    }
                } else {
                    Log.w("TAG", "uploadTask failed")
                }
            }

        }
    }
}