package com.sumita.authebtication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient //define google signIn client
    //    The Google Sign-In client is a software library provided
    //    by Google that enables developers to integrate Google
    //    Sign-In functionality into their applications and websites.
    //    Google Sign-In allows users to authenticate themselves using
    //    their Google accounts, eliminating the need for users to create
    //    separate usernames and passwords for your application
    //    Google Sign-In client handles the authentication process
    //and returns information about the user, such as their email address and profile picture.
    //A client for interacting with the Google Sign In API
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        firebaseAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        //GoogleSignInOptions contains options used to configure the Auth.GOOGLE_SIGN_IN_API.
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        //Log.d("gso", "GSO--- $gso")
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        //Build a GoogleSignInClient with the options specified by gso.
        //Log.d("gso Client", "GSO--- $googleSignInClient")
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // User is signed in, redirect to the main activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        tvaskForLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        btnSignUp.setOnClickListener {
            signUpUser()
        }
        btnGoogleSignUp.setOnClickListener {
            val signInClient = googleSignInClient.signInIntent
            //Starting the intent prompts the user to select a Google account to sign in with.
            launcher.launch(signInClient)
            Log.d("launcher", "launcher ${launcher.toString()}")
        }
    }
    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.result
                Log.d("ACCOUNT", "Account ${account.toString()}")
                val credencial = GoogleAuthProvider.getCredential(account?.idToken, null)
                Log.d("credencial", "credencial ${credencial.toString()}")
                firebaseAuth.signInWithCredential(credencial).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "done", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else
                        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        }
    }
    private fun signUpUser() {
        var email = etLEmail.text.toString()
        var password = edtSPassword.text.toString()
        var conformPassword = edtSComformPassword.text.toString()
        if (email.isBlank() || password.isBlank() || conformPassword.isBlank() || !email.contains("@")) {
            Toast.makeText(this, "Every field must be filled", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password != conformPassword) {
            Toast.makeText(this, "password does not match with conformpassword", Toast.LENGTH_SHORT)
                .show()
            return
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Sign up successfully", Toast.LENGTH_SHORT).show()
                etLEmail.text = null
                edtSPassword.text = null
                edtSComformPassword.text = null
                Intent(this, MainActivity::class.java).also {
                    startActivity(it)
                }
            } else {
                Toast.makeText(this, "Error creating user  ${it.exception}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}