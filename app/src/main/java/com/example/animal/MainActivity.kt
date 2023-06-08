package com.example.animal

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.animal.databinding.ActivityMainBinding
import com.example.animal.dto.LikeViewModel
import com.example.animal.service.MyFirebaseMessagingService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    var firestore: FirebaseFirestore? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private val likedViewModel: LikeViewModel by viewModels()

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                val homeFragment = HomeTapOneFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, homeFragment)
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.home_two -> {
                val twoHomeFragment = HomeTapTwoFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, twoHomeFragment)
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.board_detail_chat -> {
                val chatFragment = ChatFragment()
                val bundle = Bundle()

                val boardDetailFragment =
                    supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                if (boardDetailFragment is BoardDetailFragment) {
                    bundle.putString(
                        "contentUid",
                        boardDetailFragment.arguments?.getString("contentUid")
                    )
                    bundle.putString("uid", boardDetailFragment.arguments?.getString("uid"))
                    bundle.putString("title", boardDetailFragment.arguments?.getString("title"))
                } else {
                    bundle.putString("contentUid", "default_contentUid")
                    bundle.putString("uid", "default_uid")
                    bundle.putString("title", "default_title")
                }

                chatFragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, chatFragment)
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.board_detail_favorit -> {
                val bundle = Bundle()
                val contentUid: String

                val boardDetailFragment =
                    supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                if (boardDetailFragment is BoardDetailFragment) {
                    contentUid = boardDetailFragment.arguments?.getString("contentUid").toString()
                } else {
                    contentUid = "default_contentUid"
                }

                // 좋아요 버튼 클릭 시 Firestore 업데이트
                updateLikedPosts(contentUid, item)

                return true
            }
            else -> return false
        }
    }

    private fun updateLikedPosts(contentUid: String, item: MenuItem) {
        val userLikesDocument = firestore?.collection("userLikes")?.document(auth.currentUser!!.uid)

        userLikesDocument?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                val likedPosts = document["likedPosts"] as? MutableList<String> ?: mutableListOf()

                if (contentUid in likedPosts) {
                    likedPosts.remove(contentUid)
                } else {
                    likedPosts.add(contentUid)
                }
                userLikesDocument.set(mapOf("likedPosts" to likedPosts))
            }

            // 좋아요 상태 업데이트
            val isLiked = contentUid in (document?.get("likedPosts") as? List<String> ?: emptyList())
            likedViewModel.toggleLikeStatus()

            // UI 업데이트
            item.setIcon(if (isLiked) R.drawable.board_no_favorit else R.drawable.board_favorit)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //FCM 설정 Token값 가져오기
        MyFirebaseMessagingService().getFirebaseToken()

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        binding.mainNav.setOnItemSelectedListener(this)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)


        binding.mainNav.selectedItemId = R.id.home

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.my_chat_list -> {
                    val myChatList = ChatRoomFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, myChatList)
                        .addToBackStack(null)
                        .commit()
                    drawerLayout.closeDrawer(GravityCompat.END)
                    return@setNavigationItemSelectedListener true
                }
                R.id.my_favorit_list -> {
                    val myFavoriteList = FavoriteFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, myFavoriteList)
                        .addToBackStack(null)
                        .commit()
                    drawerLayout.closeDrawer(GravityCompat.END)
                    return@setNavigationItemSelectedListener true
                }
            }
            return@setNavigationItemSelectedListener false
        }

        val myProfile = binding.headerProfile
        myProfile.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        val backButton = binding.imgBack
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val myAccountTextView = binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.my_account)
        val currentUser = auth.currentUser

        if(currentUser != null){
            val email = currentUser.email
            myAccountTextView.text = email
        }else{
            myAccountTextView.text ="로그인이 필요합니다."
        }
    }
}
