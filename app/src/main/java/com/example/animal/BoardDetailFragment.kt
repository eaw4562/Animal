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


class BoardDetailFragment : Fragment() {

    private lateinit var binding: FragmentBoardDetailBinding
    private var name: String? = null
    private var imageUrl: List<String>? = null
    private var category: String? = null
    private var gender: String? = null
    private var age: String? = null
    private var breed: String? = null
    private var vaccine: String? = null
    private var where: String? = null
    private var spay: String? = null
    private var content: String? = null


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

    private fun getDataBoard() {
        val uid = arguments?.getString("contentUid")
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("images").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        name = document.getString("name")
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
                .addOnFailureListener { exception ->
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