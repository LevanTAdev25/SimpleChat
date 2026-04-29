package com.example.simplechat.themecustom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechat.databinding.ActivityReceiverBinding
import com.example.simplechat.databinding.ActivitySenderBinding
import com.example.simplechat.model.Messages
import com.google.firebase.auth.FirebaseAuth

class RvChatCustom(val list: List<Messages>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    inner class SenderViewHolder(val binding: ActivitySenderBinding): RecyclerView.ViewHolder(binding.root)
    inner class ReceiverViewHolder(val binding: ActivityReceiverBinding): RecyclerView.ViewHolder(binding.root)
    private var dbref = FirebaseAuth.getInstance().currentUser
    private var senderActivity = 0
    private var recieverActivity = 1
    override fun getItemViewType(position: Int): Int {
        return if(dbref?.uid == list[position].senderId){
            senderActivity
        }else{
            recieverActivity
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        if(viewType == senderActivity) {
            var viewHolder = ActivitySenderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return SenderViewHolder(viewHolder)
        }else{
            var viewHolder = ActivityReceiverBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ReceiverViewHolder(viewHolder)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if(holder is SenderViewHolder){
            holder.binding.apply {
                msgSender.text = list[position].message
            }
        }else if(holder is ReceiverViewHolder){
            holder.binding.apply {
                msgReceiver.text = list[position].message
            }
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }
}