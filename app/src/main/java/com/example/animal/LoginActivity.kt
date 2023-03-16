package com.example.animal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.animal.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth

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

}