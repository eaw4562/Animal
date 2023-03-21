package com.example.animal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.animal.databinding.FragmentBoardDetailBinding
import com.google.firebase.storage.FirebaseStorage


class BoardDetailFragment : Fragment() {
    private lateinit var binding: FragmentBoardDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBoardDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        // 파라미터에서 값 가져오기
        val title = arguments?.getString("title")
        val price = arguments?.getString("price")
        val imageUrl = arguments?.getString("imageUrl")

        // Firebase Storage에서 이미지 가져오기
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl!!)
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(requireContext())
                .load(uri)
                .into(binding.boardDetailImage)
        }

        return view
    }

    companion object {
        fun newInstance(title: String, price: String, imageUrl: String): BoardDetailFragment {
            val fragment = BoardDetailFragment()
            val args = Bundle()
            args.putString("title", title)
            args.putString("price", price)
            args.putString("imageUrl", imageUrl)
            fragment.arguments = args
            return fragment
        }
    }
}