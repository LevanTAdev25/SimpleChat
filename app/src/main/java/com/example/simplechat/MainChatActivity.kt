package com.example.simplechat

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplechat.databinding.ActivityMainChatBinding
import com.example.simplechat.databinding.AlertAddfriendCustomBinding
import com.example.simplechat.model.Users
import com.example.simplechat.themecustom.RvFrCustom
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainChatBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var list: MutableList<Users>
    private lateinit var mAdapter: RvFrCustom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFirebase()
        initRecycler()
        initDrawer()
        initNotificationPermission()

        loadUser()
    }

    private fun initFirebase() {
        auth = Firebase.auth
        dbref = FirebaseDatabase.getInstance().reference
        list = mutableListOf()

        Log.d("FIREBASE", FirebaseAuth.getInstance().app.options.projectId!!)
        Log.d("FIREBASE", FirebaseDatabase.getInstance().app.options.projectId!!)
        Log.d("AUTH", "Current user: ${FirebaseAuth.getInstance().currentUser?.uid}")
    }

    private fun initRecycler() {
        binding.rvListFr.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mAdapter = RvFrCustom(list)

        mAdapter.setOnClickListener(object : RvFrCustom.OnClickListener {
            override fun onClick(position: Int) {
                val intent = Intent(this@MainChatActivity, ChatActivity::class.java)
                intent.putExtra("email", list[position].email)
                startActivity(intent)
            }
        })

        binding.rvListFr.adapter = mAdapter
    }

    private fun initDrawer() {
        val headerView = binding.nvProfile.getHeaderView(0)
        val username = headerView.findViewById<TextView>(R.id.textUsername)
        username.text = auth.currentUser?.email.toString()

        binding.layoutDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        binding.nvProfile.setNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.itLogout -> {
                    auth.signOut()
                    val intent = Intent(this, EntryActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                R.id.itRequest -> {
                    startActivity(Intent(this, RequestActivity::class.java))
                }

                R.id.itAddFriend -> {
                    showAddFriendDialog()
                }
            }
            true
        }
    }

    private fun initNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }


    private fun showAddFriendDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val viewDialog = AlertAddfriendCustomBinding.inflate(layoutInflater)

        val userUid = auth.currentUser?.uid

        if (userUid != null) {
            viewDialog.btnAdd.setOnClickListener {

                val emailToFind =
                    viewDialog.editEmailAdd.text.toString().trim().lowercase()

                if (emailToFind != auth.currentUser?.email) {

                    val keyRequest = dbref.child("friend_request").push().key!!
                    val query =
                        dbref.child("users").orderByChild("email").equalTo(emailToFind)

                    query.get().addOnSuccessListener { snapshot ->

                        if (snapshot.exists()) {
                            for (child in snapshot.children) {

                                val mapUser = mapOf(
                                    "From" to userUid,
                                    "To" to child.key,
                                    "Status" to "Pending"
                                )

                                dbref.child("friend_request")
                                    .child(keyRequest)
                                    .setValue(mapUser)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                this,
                                                "Da them thanh cong",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Khong co nguoi dung nao nhu nay",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Query lỗi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        dialogBuilder.setView(viewDialog.root)
        dialogBuilder.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(1, 1, 1, "Profile")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 1) {
            binding.layoutDrawer.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadUser() {
        dbref.child("friends")
            .child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()

                    for (child in snapshot.children) {
                        val key = child.key

                        dbref.child("users")
                            .child(key!!)
                            .child("email")
                            .get()
                            .addOnSuccessListener { snap ->
                                list.add(Users(snap.getValue(String::class.java)!!))
                                mAdapter.notifyDataSetChanged()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
