package com.example.simplechat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simplechat.databinding.ActivityEntryBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth



class EntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntryBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        binding.btnLogin.setOnClickListener {
            var i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }
        binding.btnSignup.setOnClickListener {
            var i = Intent(this , SignUpActivity::class.java)
            startActivity(i)
        }
    }
    override fun onStart() {
        super.onStart()
        var currentUser = auth.currentUser
        if(currentUser != null){
            reload()
        }
    }
    fun reload(){
        var currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.reload()?.addOnCompleteListener {
                task->
            if (task.isSuccessful){
                Toast.makeText(this,"successful", Toast.LENGTH_LONG).show()
                var i = Intent(this, MainChatActivity::class.java)
                startActivity(i)
                finish()
            } else{
                Toast.makeText(this,"failed", Toast.LENGTH_LONG).show()
            }
        }
    }
}