package com.example.simplechat.themecustom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechat.databinding.FriendrequestCustomBinding
import com.example.simplechat.model.Users

class RvRqCustom(val listRq: List<Users>): RecyclerView.Adapter<RvRqCustom.RvRqViewHolder>(){
    class RvRqViewHolder(val binding: FriendrequestCustomBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var viewOnAcceptListener: OnAcceptListener
    private lateinit var viewOnDeclineListener: OnDeclineListener
    interface OnAcceptListener{
        fun onClick(position: Int)
    }
    interface OnDeclineListener{
        fun onClick(position: Int)
    }
    fun setOnAcceptListener(viewAccept: OnAcceptListener){
        viewOnAcceptListener = viewAccept
    }
    fun setOnDeclineListener(viewDecline: OnDeclineListener){
        viewOnDeclineListener = viewDecline
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RvRqViewHolder {
        var viewHolder = FriendrequestCustomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RvRqViewHolder(viewHolder)
    }

    override fun onBindViewHolder(
        holder: RvRqViewHolder,
        position: Int
    ) {
        holder.binding.apply {
            textUsername.text = listRq[position].email.toString()
            btnAccept.setOnClickListener {
                viewOnAcceptListener.onClick(position)
            }
            btnDecline.setOnClickListener {
                viewOnDeclineListener.onClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return listRq.size
    }
}