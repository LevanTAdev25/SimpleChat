package com.example.simplechat

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.simplechat.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var email: String? = null
    private var password: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*enableEdgeToEdge()
        setContent {
            SimpleChatTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }*/
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        binding.btnLogin.setOnClickListener {
            email = binding.editEmail.text.toString()
            password = binding.editPassword.text.toString()
            auth.signInWithEmailAndPassword(email!!,password!!).addOnCompleteListener {task->
                if(task.isSuccessful){
                    var i = Intent(this, MainChatActivity::class.java)
                    startActivity(i)
                }else{
                    var dialog = AlertDialog.Builder(this)
                    dialog.setTitle("email or password is not in correct")
                    dialog.setPositiveButton("ok") {dialog, i->
                        dialog.dismiss()
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }
    }
}

/*
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SimpleChatTheme {
        Greeting("Android")
    }
}*/
