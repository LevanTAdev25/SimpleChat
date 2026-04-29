package com.example.simplechat

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplechat.databinding.ActivityMainChatBinding
import com.example.simplechat.databinding.ActivityRequestBinding
import com.example.simplechat.model.Users
import com.example.simplechat.themecustom.RvRqCustom
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestBinding
    private lateinit var listRq: MutableList<Users>
    private lateinit var dbref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var mAdapter: RvRqCustom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFirebase()
        initRecycler()
        loadFriendRequests()
    }

    // ================= INIT =================

    private fun initFirebase() {
        auth = Firebase.auth
        dbref = FirebaseDatabase.getInstance().reference
        listRq = mutableListOf()
    }

    private fun initRecycler() {
        binding.RvListRq.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mAdapter = RvRqCustom(listRq)
        binding.RvListRq.adapter = mAdapter
    }

    // ================= LOAD REQUEST =================

    private fun loadFriendRequests() {

        dbref.child("friend_request")
            .orderByChild("To")
            .equalTo(auth.currentUser?.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (!snapshot.exists()) return

                    for (requestSnapshot in snapshot.children) {

                        val requestKey = requestSnapshot.key ?: continue
                        val status = requestSnapshot.child("Status")
                            .getValue(String::class.java)

                        if (status == "Pending") {
                            handlePendingRequest(requestSnapshot, requestKey)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun handlePendingRequest(
        requestSnapshot: DataSnapshot,
        requestKey: String
    ) {
        val fromUid =
            requestSnapshot.child("From").getValue(String::class.java) ?: return

        dbref.child("users")
            .child(fromUid)
            .child("email")
            .get()
            .addOnSuccessListener { emailSnap ->

                val email = emailSnap.getValue(String::class.java) ?: return@addOnSuccessListener

                listRq.add(Users(email))
                mAdapter.notifyItemInserted(listRq.size - 1)
                binding.RvListRq.scrollToPosition(listRq.size - 1)

                setupAcceptDeclineListener(requestKey, fromUid)
            }
    }

    // ================= ACCEPT / DECLINE =================

    private fun setupAcceptDeclineListener(
        requestKey: String,
        fromUid: String
    ) {

        mAdapter.setOnAcceptListener(object : RvRqCustom.OnAcceptListener {
            override fun onClick(position: Int) {
                acceptFriend(requestKey, fromUid, position)
            }
        })

        mAdapter.setOnDeclineListener(object : RvRqCustom.OnDeclineListener {
            override fun onClick(position: Int) {
                declineFriend(fromUid, position)
            }
        })
    }

    private fun acceptFriend(
        requestKey: String,
        fromUid: String,
        position: Int
    ) {

        dbref.child("friend_request")
            .child(requestKey)
            .child("Status")
            .setValue("Accepted")
            .addOnSuccessListener {

                val currentUid = auth.currentUser!!.uid

                // Add friend both sides
                dbref.child("friends")
                    .child(currentUid)
                    .child(fromUid)
                    .setValue(true)

                dbref.child("friends")
                    .child(fromUid)
                    .child(currentUid)
                    .setValue(true)

                // Create chat room
                val keyChat = dbref.child("messages").push().key!!

                dbref.child("messages")
                    .child(keyChat)
                    .child("participants")
                    .child(fromUid)
                    .setValue(true)

                dbref.child("messages")
                    .child(keyChat)
                    .child("participants")
                    .child(currentUid)
                    .setValue(true)

                Toast.makeText(
                    this@RequestActivity,
                    "Them thanh cong",
                    Toast.LENGTH_SHORT
                ).show()

                removeItem(position)
            }
    }

    private fun declineFriend(fromUid: String, position: Int) {

        dbref.child("friend_request")
            .orderByChild("From")
            .equalTo(fromUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val key = child.key ?: continue
                        dbref.child("friend_request")
                            .child(key)
                            .child("Status")
                            .setValue("Declined")
                    }
                    removeItem(position)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun removeItem(position: Int) {
        listRq.removeAt(position)
        mAdapter.notifyItemRemoved(position)
        mAdapter.notifyItemRangeChanged(position, listRq.size)
    }
}
