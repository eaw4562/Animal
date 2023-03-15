package com.example.animal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.animal.databinding.ActivitySingupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SingupActivity : AppCompatActivity() {

    lateinit var binding : ActivitySingupBinding
    lateinit var fauth : FirebaseAuth
    private lateinit var DbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인증 초기화 코드
        fauth = FirebaseAuth.getInstance()

        //데이터베이스 초기화 코드
        DbRef = Firebase.database.reference

        //회원가입 버튼 클릭 이벤트
        binding.btnSign.setOnClickListener {
            val name = binding.editSignName.text.toString().trim()
            val email = binding.editSignEmail.text.toString().trim()
            val password = binding.editSignPass.text.toString().trim()
            val passchek = binding.editSignPasscheck.text.toString().trim()

            if(binding.editSignPass.text.toString().equals( binding.editSignPasscheck.text.toString())){
                signUp(name,email,password)
            }else{
                Toast.makeText(this,"비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                Log.d("Signup","Error")
            }
        }
    }
    //회원 가입 코드
    private fun signUp(name: String, email: String, password: String) {

        fauth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                //회원가입 성공
                if(task.isSuccessful){
                    Toast.makeText(this, "회원 가입 성공", Toast.LENGTH_SHORT).show()

                    val intent: Intent = Intent(this@SingupActivity, MainActivity::class.java)
                    startActivity(intent)
                    addUserToDatabase(name, email, fauth.currentUser?.uid!!)
                }else{
                    Toast.makeText(this,"회원 가입 실패", Toast.LENGTH_SHORT).show()
                    Log.d("SignUp","Error : ${task.exception}")
                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
        DbRef.child("user").child(uid).setValue(User(name,email,uid))
    }
}