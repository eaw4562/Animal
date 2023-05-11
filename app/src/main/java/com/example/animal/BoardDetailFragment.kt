package com.example.animal

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.example.animal.DTO.User
import com.example.animal.adapter.BoardDetailAdapter
import com.example.animal.databinding.FragmentBoardDetailBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
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
    var firestore: FirebaseFirestore? = null
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference


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
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        val boardDetailNav = activity?.findViewById<BottomNavigationView>(R.id.main_nav)
        boardDetailNav?.visibility = View.VISIBLE
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


        // ViewPager 어댑터 설정
        if (imageUrl != null) {
            val adapter = BoardDetailAdapter(mContext, imageUrl!!)
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