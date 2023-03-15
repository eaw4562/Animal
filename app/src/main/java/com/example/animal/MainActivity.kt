package com.example.animal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.animal.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var auth : FirebaseAuth
    var firestore : FirebaseFirestore? = null

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
    when(menuItem.itemId){
        R.id.home -> {
            //MainActivity
            return@OnNavigationItemSelectedListener true
        }
        R.id.home_two -> {
            //2번째 탭
            val fragment = HomeTapTwoFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit()
            return@OnNavigationItemSelectedListener true
        }
        else -> false
    }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.main_nav)
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }
}