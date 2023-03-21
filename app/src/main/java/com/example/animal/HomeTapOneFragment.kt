package com.example.animal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animal.adapter.BoardAdapter
import com.example.animal.DTO.ContentDTO
import com.example.animal.DTO.Item
import com.example.animal.databinding.HomeTapOneFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeTapOneFragment : Fragment() {

    private lateinit var boardAdapter: BoardAdapter
    private var itemList = mutableListOf<Item>()
    private lateinit var binding : HomeTapOneFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeTapOneFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        // RecyclerView 설정
        boardAdapter = BoardAdapter(itemList)
        val recyclerView: RecyclerView = binding.oneHomeRecycler
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = boardAdapter

        // Firebase에서 데이터 가져오기
        FirebaseFirestore.getInstance().collection("images")
            ?.orderBy("timeStamp", Query.Direction.DESCENDING)
            ?.get()
            ?.addOnSuccessListener { documents ->
                for (document in documents) {
                    val contentDTO = document.toObject(ContentDTO::class.java)
                    if (contentDTO.title != null && contentDTO.price != null && contentDTO.imageUrl != null) {
                        val item = Item(contentDTO.title, contentDTO.price, contentDTO.imageUrl!!)
                        itemList.add(item)
                    }
                }
                // 데이터 변경 감지
                boardAdapter.notifyDataSetChanged()
            }
            ?.addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnWrite = binding.btnWrite
        btnWrite.setOnClickListener {
            val intent = Intent(requireContext(), BoardWrite::class.java)
            startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "HomeTapOneFragment"
    }
}