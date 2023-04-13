package com.example.animal

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.animal.DTO.ChatDTO
import com.example.animal.adapter.ChatAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUserId: String

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendbtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid ?: ""
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)

        recyclerView = view.findViewById(R.id.chat_recycler)
        inputMessage = view.findViewById(R.id.chat_input_edit)
        sendbtn = view.findViewById(R.id.chat_send_btn)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contentUid = arguments?.getString("contentUid")

        //Firestore 채팅 데이터 가져오기
        val chatCollection = firestore.collection("contents").document(contentUid!!).collection("chat")
        val query = chatCollection.orderBy("timeStamp")
        query.addSnapshotListener {value, error ->
            if(error != null){
                Log.w(TAG,"Listen falied", error)
                return@addSnapshotListener
            }
            //RecycleView에 적용
            val chatList = mutableListOf<ChatDTO>()
            for(document in value!!){
                val chat = document.toObject(ChatDTO::class.java)
                chatList.add(chatList)
            }
            recyclerView.adapter = ChatAdapter(chatList)
        }
    }
}
