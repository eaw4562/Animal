package com.example.animal

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.animal.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import org.w3c.dom.Text

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    var firestore: FirebaseFirestore? = null
    private lateinit var drawerLayout: DrawerLayout

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
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, chatFragment)
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.board_detail_favorit -> {
                val favoritFragment = FavoritFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, favoritFragment)
                    .addToBackStack(null)
                    .commit()
                return true
            }
        }
        return false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance() // auth 변수 초기화
        firestore = FirebaseFirestore.getInstance()
        binding.mainNav.setOnItemSelectedListener(this)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)

        binding.mainNav.selectedItemId = R.id.home

        // drawerLayout 초기화
        drawerLayout = findViewById(R.id.drawer_layout)

        val myProfile = findViewById<ImageButton>(R.id.header_profile)
        myProfile.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        val myAccountTextView = binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.my_account)
        val currentUSer = auth.currentUser

        if(currentUSer != null){
            val email = currentUSer.email
            myAccountTextView.text = email
        }else{
            myAccountTextView.text ="로그인이 필요합니다."
        }
    }
}


