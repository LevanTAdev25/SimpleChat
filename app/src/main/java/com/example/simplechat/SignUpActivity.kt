package com.example.simplechat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simplechat.databinding.ActivitySignUpBinding
import com.example.simplechat.model.Users
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var dbauth: FirebaseAuth
    private lateinit var dbref: DatabaseReference
    private var email: String? = null
    private var password : String? = null
    private var confirmPassword: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dbauth = Firebase.auth
        dbref = FirebaseDatabase.getInstance().reference
        binding.btnSignup.setOnClickListener {
            email = binding.editEmail.text.toString()
            password = binding.editPassword.text.toString()
            confirmPassword = binding.editConfirmPassword.text.toString()
            if(password == confirmPassword){
                dbauth.createUserWithEmailAndPassword(email!!,password!!).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val uid = task.result.user?.uid
                        val user = Users(email!!)
                        dbref.child("users").child(uid!!).setValue(user).addOnCompleteListener { }
                        var i = Intent(this, MainChatActivity::class.java)
                        startActivity(i)
                        finish()
                    }else{
                        Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        Log.e("AUTH", "Sign up error", task.exception)
                    }
                }

            }else{
                Toast.makeText(this,"password and confirm password must be matched", Toast.LENGTH_SHORT).show()
            }
        }
    }
}