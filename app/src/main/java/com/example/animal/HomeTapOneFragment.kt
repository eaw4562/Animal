package com.example.animal

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.animal.Adpater.BoardAdapter
import com.example.animal.DTO.Item

class HomeTapOneFragment : Fragment() {

    private lateinit var boardAdapter: BoardAdapter
    private var itemList = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_tap_one_fragment, container, false)

        // RecyclerView 설정
        boardAdapter = BoardAdapter(itemList)
        val recyclerView: RecyclerView = view.findViewById(R.id.one_home_recycler)
        recyclerView.setHasFixedSize(true)
        if (boardAdapter != null) {
            recyclerView.adapter = boardAdapter
        }

        // 아이템 추가
        itemList.add(Item("아이템 1", "1000원", R.drawable.item1))
        itemList.add(Item("아이템 2", "2000원", R.drawable.item2))
        itemList.add(Item("아이템 3", "3000원", R.drawable.item2))
        itemList.add(Item("아이템 4", "4000원", R.drawable.item1))
        itemList.add(Item("아이템 5", "5000원", R.drawable.item2))

        // 데이터 변경 감지
        boardAdapter.notifyDataSetChanged()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnWrite = view.findViewById<AppCompatButton>(R.id.btn_write)
        btnWrite.setOnClickListener {
            val intent = Intent(requireContext(), BoardWrite::class.java)
            startActivity(intent)
        }
    }

}
