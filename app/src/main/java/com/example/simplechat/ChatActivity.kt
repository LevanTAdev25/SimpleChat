package com.example.simplechat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplechat.databinding.ActivityChatBinding
import com.example.simplechat.model.Messages
import com.example.simplechat.themecustom.RvChatCustom
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var mAdaper : RvChatCustom
    private lateinit var listChat : MutableList<Messages>
    private lateinit var email: String
    private lateinit var auth: FirebaseAuth
    private var keyConversation: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        dbref = FirebaseDatabase.getInstance().reference
        var i = intent
        email = i.getStringExtra("email")!!
        binding.txtUsername.text = email
        auth = Firebase.auth
        binding.rvChat.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        listChat = mutableListOf<Messages>()
        mAdaper = RvChatCustom(listChat)
        binding.rvChat.adapter = mAdaper
        setUpSendButton()
        var fromuid : String? = null
        dbref.child("users").orderByChild("email").equalTo(email).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot!=null){
                    for(child in snapshot.children){
                        fromuid = child.key
                        break
                    }
                }
                if(fromuid != null){
                    searchForExistingConversation(fromuid!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        binding.rvChat.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                binding.rvChat.postDelayed({
                    binding.rvChat.scrollToPosition(mAdaper.itemCount - 1)
                }, 100)
            }
        }
    }
    fun setUpSendButton(){
        binding.btnSend.setOnClickListener {
            if(binding.msgSend.text.isNotBlank()){
                Log.wtf("error",keyConversation)
                var keyChat = dbref.child("messages").child(keyConversation).child("content").push().key
                dbref.child("messages").child(keyConversation).child("content").child(keyChat!!).setValue(
                    Messages(
                        auth.currentUser!!.uid,
                        binding.msgSend.text.toString(),
                        System.currentTimeMillis()
                    )
                ).addOnSuccessListener {

                }.addOnFailureListener {
                    Log.e("CHAT", "Lỗi khi lưu tin nhắn: ")
                }
               /* listChat.add(Messages(auth.currentUser!!.uid,binding.msgSend.text.toString(),System.currentTimeMillis()))
                mAdaper.notifyDataSetChanged()
                binding.rvChat.scrollToPosition(listChat.size-1)*/
            }
        }
    }
    private fun createNewConversation(friendUid: String) {
        keyConversation = dbref.child("messages").push().key!!

        val participants = mapOf(
            auth.currentUser!!.uid to true,
            friendUid to true
        )

        dbref.child("messages").child(keyConversation).child("participants")
            .setValue(participants).addOnSuccessListener {
                Log.d("ChatActivity", "Tạo conversation mới: $keyConversation")

                // Thêm vào friends nếu chưa có
                dbref.child("friends").child(auth.currentUser!!.uid).child(friendUid).setValue(true)
                dbref.child("friends").child(friendUid).child(auth.currentUser!!.uid).setValue(true)
            }.addOnFailureListener { e ->
                Log.e("ChatActivity", "Lỗi khi tạo conversation: ${e.message}")
            }
    }
    fun searchForExistingConversation(fromUid : String){
        dbref.child("messages").get().addOnSuccessListener { snapshot ->
            var found = false
            if(snapshot.exists()){
                for(child in snapshot.children){
                    if(child.child("participants").hasChild(fromUid!!) && child.child("participants").hasChild(auth.currentUser!!.uid)){
                        keyConversation = child.key!!
                        Log.wtf("error",keyConversation)
                        found = true
                        break
                    }
                }
            }
            if(found == true){
                addMessage()
            }else{
                createNewConversation(fromUid)
            }
        }
    }
    fun addMessage(){
       /* dbref.child("messages").child(keyConversation).child("content").orderByChild("timestamp").limitToLast(20).addListenerForSingleValueEvent( object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(child in snapshot.children){
                    listChat.add(child.getValue(Messages::class.java)!!)
                    mAdaper.notifyItemInserted(listChat.size-1)
                    binding.rvChat.scrollToPosition(listChat.size-1)
                }
                for(item in listChat){
                    Log.wtf("error",item.message)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })*/
        dbref.child("messages").child(keyConversation).child("content").orderByChild("timestamp").limitToLast(20).addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {
                if(snapshot.exists()){
                    listChat.add(snapshot.getValue(Messages::class.java)!!)
                    mAdaper.notifyItemInserted(listChat.size-1)
                    binding.rvChat.scrollToPosition(listChat.size-1)
                    Log.wtf("error","done")
                    }
                }


            override fun onChildChanged(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}