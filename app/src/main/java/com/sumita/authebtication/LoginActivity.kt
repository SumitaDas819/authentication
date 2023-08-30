package com.sumita.authebtication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.etLEmail

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // User is signed in, redirect to the main activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        btnLogin.setOnClickListener {
            login()
        }
        tvaskforSingup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }
    private fun login(){
        var email=etLEmail.text.toString()
        var password=etLPassword.text.toString()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(this,"Login successfuly",Toast.LENGTH_SHORT).show()
                startActivity( Intent(this, MainActivity::class.java))
                finish()
            }
            else
                Toast.makeText(this,"Login unsuccessfull",Toast.LENGTH_SHORT).show()
        }
    }
}