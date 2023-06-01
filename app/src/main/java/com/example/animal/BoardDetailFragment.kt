package com.example.animal

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.animal.dto.User
import com.example.animal.adapter.BoardDetailAdapter
import com.example.animal.databinding.FragmentBoardDetailBinding
import com.example.animal.dto.LikeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

class BoardDetailFragment : Fragment() {

    private lateinit var mContext: Context
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
    private var title: String? = ""
    private var firestore: FirebaseFirestore? = null
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference
    private val likeViewModel: LikeViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBoardDetailBinding.inflate(inflater, container, false)
        val bundle = arguments
        val contentUid = bundle?.getString("contentUid")
        val uid = bundle?.getString("uid")
        val title = bundle?.getString("title")


        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        mDbRef.child("user").child(uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        val author = snapshot.getValue<User>()
                        binding.boardDetailNickname.text = author?.nickname
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "유저 데이터 오류", error.toException())
                }
            })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.main_nav)
        bottomNavigation.menu.findItem(R.id.board_detail_chat).isVisible = true
        bottomNavigation.menu.findItem(R.id.board_detail_favorit).isVisible = true
        bottomNavigation.menu.findItem(R.id.home).isVisible = false
        bottomNavigation.menu.findItem(R.id.home_two).isVisible = false

        val menuItem = bottomNavigation.menu.findItem(R.id.board_detail_favorit)
        val contentUid = arguments?.getString("contentUid")

        // 비동기 작업 처리
        viewLifecycleOwner.lifecycleScope.launch {
            val isLiked = withContext(Dispatchers.IO) {
                checkLikeStatus(contentUid)
            }
            // UI 업데이트
            updateLikeButtonUI(menuItem, isLiked)
        }
    }

    private suspend fun checkLikeStatus(contentUid: String?): Boolean {
        val userLikesDocument = firestore?.collection("userLikes")?.document(mAuth.currentUser!!.uid)

        val document = userLikesDocument?.get()?.await()
        if (document != null) {
            val likedPosts = document["likedPosts"] as? List<String> ?: emptyList()

            return contentUid in likedPosts
        }

        return false
    }

    private fun updateLikeButtonUI(menuItem: MenuItem, isLiked: Boolean) {
        menuItem.setIcon(if (isLiked) R.drawable.board_favorit else R.drawable.board_no_favorit)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
            title = bundle.getString("title")
        }
        bindData()
        getDataBoard()
        /*val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.main_nav)
        val menuItem = bottomNavigation.menu.findItem(R.id.board_detail_favorit)
        val contentUid = arguments?.getString("contentUid")
        updateLikeButtonUI(contentUid, menuItem)*/
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        val boardDetailNav = activity?.findViewById<BottomNavigationView>(R.id.main_nav)
        boardDetailNav?.visibility = View.VISIBLE
    }


    override fun onResume() {
        super.onResume()

        // 좋아요 버튼 상태 업데이트
        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.main_nav)
        val menuItem = bottomNavigation.menu.findItem(R.id.board_detail_favorit)
        val contentUid = arguments?.getString("contentUid")

        // 비동기 작업 처리
        viewLifecycleOwner.lifecycleScope.launch {
            val isLiked = withContext(Dispatchers.IO) {
                checkLikeStatus(contentUid)
            }
            // UI 업데이트
            updateLikeButtonUI(menuItem, isLiked)
        }
    }

    override fun onDetach() {
        super.onDetach()

        val boardDetailNav = activity?.findViewById<BottomNavigationView>(R.id.main_nav)
        boardDetailNav?.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bottom_nav_menu, menu)

        // main_nav의 항목 숨기기
        menu.findItem(R.id.home).isVisible = false
        menu.findItem(R.id.home_two).isVisible = false

        // board_detail_chat과 board_detail_favorit 메뉴 항목 보이기
        menu.findItem(R.id.board_detail_chat).isVisible = true
        menu.findItem(R.id.board_detail_favorit).isVisible = true
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
        val priceFormat = NumberFormat.getNumberInstance(Locale.US)
        val formattedPrice = priceFormat.format(price?.toLongOrNull() ?: 0)
        binding.boardDetailPrice.text = formattedPrice

        val dotsIndicator = binding.viewDot
        val viewPager = binding.boardDetailImages
        // ViewPager 어댑터 설정
        if (imageUrl != null) {
            val adapter = BoardDetailAdapter(mContext, imageUrl!!)
            binding.boardDetailImages.adapter = adapter
            dotsIndicator.attachTo(viewPager)
        }
    }

    private fun getDataBoard() {
        firestore = FirebaseFirestore.getInstance()
        val contentUid = arguments?.getString("contentUid")
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (contentUid != null && uid != null) {
            firestore?.collection("images")?.document(contentUid)
                ?.get()
                ?.addOnSuccessListener { document ->
                    if (document.exists()) {
                        title = document.getString("title")
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

    /*private fun updateLikeButtonUI(contentUid: String?, menuItem: MenuItem) {
        likeViewModel.isLiked.observe(viewLifecycleOwner) { isLiked ->
            menuItem.setIcon(if (isLiked) R.drawable.board_favorit else R.drawable.board_no_favorit)
        }

        // 초기 상태 설정
        val userLikesDocument = firestore?.collection("userLikes")?.document(mAuth.currentUser!!.uid)

        userLikesDocument?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                val likedPosts = document["likedPosts"] as? List<String> ?: emptyList()

                val isLiked = contentUid in likedPosts
                likeViewModel.toggleLikeStatus()
            }
        }
    }*/

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
