package com.example.animal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animal.DTO.ChatDTO
import com.example.animal.adapter.ChatAdapter
import com.example.animal.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var currentUserId: String
    private lateinit var currentUserEmail : String

    private lateinit var messageList: ArrayList<ChatDTO>

    private lateinit var contentUid: String
    private lateinit var uid: String
    private lateinit var title: String

    private lateinit var reciverName: String
    private lateinit var reciverUid: String
    private lateinit var senderRoom: String //받는 대화방
    private lateinit var reciverRoom: String //보낸 대화방
    private lateinit var timestamp : String
    lateinit var chatRoom : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        currentUserId = mAuth.currentUser?.uid ?: ""
        currentUserEmail = mAuth.currentUser?.email ?: ""
        mDbRef = FirebaseDatabase.getInstance().reference

        // 전달 받은 bundle에서 값을 받아옴
        arguments?.let {
            contentUid = it.getString("contentUid").toString()
            reciverUid = it.getString("uid").toString()
            title = it.getString("title").toString()

            var senderUid = it.getString("senderUid")
            if (senderUid == null) {
                senderUid = mAuth.currentUser?.uid
            }


            //채팅 방
            chatRoom = listOf(senderUid, reciverUid).sortedBy { it }.joinToString(separator = "_")

            //받는이 방senderUid
            //senderRoom = senderUid + "_" + reciverUid

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater)

        // 게시글의 타이틀을 출력
        binding.chatTitleText.text = title


        //RecyclerView
        messageList = ArrayList()
        val chatAdapter: ChatAdapter = ChatAdapter(requireContext(), messageList)
        binding.chatRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecycler.adapter = chatAdapter


        // 메시지 전송 버튼에 대한 onClick 이벤트 설정
        binding.chatSendBtn.setOnClickListener {
            val message = binding.chatInputEdit.text.toString()
            val currentTime = System.currentTimeMillis()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
            sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val dateString = sdf.format(currentTime)
            val messageObject = ChatDTO(currentUserId, message, reciverUid, currentTime, dateString)

            // 데이터 저장
            val senderChatRef = mDbRef.child("Chat").child("chatRooms").child(chatRoom).child("messages").push()

            // 데이터 저장
            senderChatRef.setValue(messageObject).addOnSuccessListener {
                mDbRef.child("Chat").child("chatRooms").child(chatRoom).child("users").child(currentUserId).setValue(true)
                mDbRef.child("Chat").child("chatRooms").child(chatRoom).child("users").child(reciverUid).setValue(true)

                // 채팅 저장 성공
            }
            //입력값 초기화
            binding.chatInputEdit.setText("")
            chatAdapter.notifyDataSetChanged()
        }

        //메시지 가져오기
        mDbRef.child("Chat").child("chatRooms").child(chatRoom).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for(postSnapshat in snapshot.children){

                        val message = postSnapshat.getValue(ChatDTO::class.java)
                        messageList.add(message!!)
                    }
                    Log.w("ChatFragment", "chatList size: ${messageList.size}") // 새로 추가한 로그

                    //적용
                    chatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        return binding.root
    }


}