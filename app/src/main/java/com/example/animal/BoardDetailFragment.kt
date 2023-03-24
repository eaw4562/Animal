package com.example.animal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.animal.adapter.BoardDetailPagerAdapter
import com.example.animal.databinding.FragmentBoardDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.model.DocumentKey


class BoardDetailFragment : Fragment() {

    private lateinit var binding: FragmentBoardDetailBinding
    private var name: String? = ""
    private var imageUrl: List<String>? = null
    private var category: String? = ""
    private var gender: String? = "'"
    private var age: String? = ""
    private var breed: String? = ""
    private var vaccine: String? = ""
    private var where: String? = ""
    private var spay: String? = ""
    private var content: String? = ""
    private var price: String? = ""
    var firestore: FirebaseFirestore? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBoardDetailBinding.inflate(inflater, container, false)

        arguments?.let { bundle ->
            imageUrl = bundle.getString("imageUrl")?.split(",")
            name = bundle.getString("name")
            category = bundle.getString("category")
            age = bundle.getString("age")
            gender = bundle.getString("gender")
            breed = bundle.getString("breed")
            vaccine = bundle.getString("vaccine")
            where = bundle.getString("where")
            spay = bundle.getString("spay")
            spay = if (spay == "O") "중성화 O" else "중성화 X"
            content = bundle.getString("content")
            price = bundle.getString("price")
        }



        bindData()
        getDataBoard()

        return binding.root
    }

    private fun bindData() {
        binding.boardDetailName.text = name
        binding.boardDetailCategory.text = category
        binding.boardDetailGender.text = gender
        binding.boardDetailAge.text = age
        binding.boardDetailBreed.text = breed
        binding.boardDetailVaccine.text = vaccine
        binding.boardDetailWhere.text = where
        binding.boardDetailSpay.text = spay
        binding.boardDetailContent.text = content
        binding.boardDetailPrice.text = price

        // ViewPager 어댑터 설정
        if (imageUrl != null) {
            val adapter = BoardDetailPagerAdapter(requireContext(), imageUrl!!)
            binding.boardDetailImages.adapter = adapter

        }
        // ViewPager DotIndicator 추후 수정 예정
        // 현재 적용 x
      /*  val dotsIndicator = binding.viewDot
        dotsIndicator.setViewPager2(binding.boardDetailImages)
        dotsIndicator.setViewPager2(binding.boardDetailImages)
        dotsIndicator.dotsColor = ContextCompat.getColor(requireContext(), R.color.black)
        dotsIndicator.selectedDotColor = ContextCompat.getColor(requireContext(), R.color.purple_700)*/
    }

    /**
     * TODO: 인자값을 매개로 컬렉션에서 값을 가져오는 코드로 바꿀예정
     *          해결 완료 contentUid 값을 documentId로 저장해 경로 불러옴
     */
    private fun getDataBoard() {
        firestore = FirebaseFirestore.getInstance()
        val contentUid = arguments?.getString("contentUid")
        if (contentUid != null) {
            firestore?.collection("images")?.document(contentUid)
                ?.get()
                ?.addOnSuccessListener { document ->
                    if (document.exists()) {
                        name = document.getString("name")
                        price = document.getString("price")
                        imageUrl = document.getString("imageUrl")?.split(",")
                        category = document.getString("category")
                        age = document.getString("age")
                        gender = document.getString("gender")
                        breed = document.getString("breed")
                        vaccine = document.getString("vaccine")
                        where = document.getString("where")
                        spay = document.getString("spay")
                        spay = if (spay == "O") "중성화 O" else "중성화 X"
                        content = document.getString("content")
                        bindData()
                    } else {
                        Log.d("BoardDetailFragment", "No such document")
                    }
                }
                ?.addOnFailureListener { exception ->
                    Log.d("BoardDetailFragment", "Error getting documents: ", exception)
                }
        }
    }



    companion object {
        fun newInstance(contentUid: String?, imageUrl: String?): BoardDetailFragment {
            val fragment = BoardDetailFragment()
            val args = Bundle().apply {
                putString("contentUid", contentUid)
                putString("imageUrl", imageUrl)

            }
            fragment.arguments = args
            return fragment
        }
    }
}