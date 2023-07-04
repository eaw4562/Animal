package com.example.animal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.animal.MainActivity
import com.example.animal.SingupActivity
import com.example.animal.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding
    lateinit var fauth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    var TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fauth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPass.text.toString()

            login(email, password)
        }

        binding.btnSign.setOnClickListener {
            val intent : Intent = Intent(this@LoginActivity, SingupActivity::class.java)
            startActivity(intent)
        }

    }

    private fun login(email: String, password: String){
        fauth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    //로그인 성공
                    val intent : Intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    finish()

                    //토큰 저장
                    getAndSaveFCMToken()
                }else{
                    //로그인 실패
                    Toast.makeText(this,"로그인 실패",Toast.LENGTH_SHORT).show()
                    Log.d("Login","Error : ${task.exception}")
                }
            }
    }

    private fun setListenerToEditText() {
        binding.editPass.setOnKeyListener{ view, keycode, event ->
            if(event.action == KeyEvent.ACTION_DOWN
                && keycode == KeyEvent.KEYCODE_ENTER
            ){
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.editPass.windowToken, 0)
            }
            false
        }

    }
    private fun getAndSaveFCMToken() {
        // Firebase Cloud Messaging에서 토큰 가져오기
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(task.isSuccessful){
                val token = task.result //가져온 토큰 값
                val uid = fauth.currentUser?.uid
                Log.d(TAG,"Login Firbase Token : $token") //로그에 토큰 값 출력
                saveFCMToken(uid!!,token) //토큰 값을 저장하는 함수 호출
            }else{
                Log.e(TAG, "Failed to get Firebase Token: ${task.exception}")
            }
        }
    }

    private fun saveFCMToken(uid: String, token: String?) {
        if (token != null) {
            val dbRef = FirebaseDatabase.getInstance().reference //realtime DB 레퍼런스 객체 가져오기
            val userRef = dbRef.child("user").child(uid) //"user"노드 아래에 현재 사욪자의 UID로 참조
            userRef.child("FCMToken").setValue(token)
                .addOnSuccessListener {
                    Log.d(TAG, "FCM Token saved successfully")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to save FCM token: ${e.message}")
                }
        } else {
            Log.e(TAG, "FCM Token is null")
        }
    }
}
