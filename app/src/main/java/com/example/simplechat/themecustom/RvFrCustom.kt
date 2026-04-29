package com.example.simplechat.themecustom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechat.databinding.FriendrvCustomBinding
import com.example.simplechat.model.Users

class RvFrCustom(val list: List<Users>) : RecyclerView.Adapter<RvFrCustom.RvFrViewHolder>(){
    class RvFrViewHolder(val binding: FriendrvCustomBinding) : RecyclerView.ViewHolder(binding.root)
    private var viewOnClick: OnClickListener? = null

    interface OnClickListener{
        fun onClick(position: Int)
    }
    fun setOnClickListener(listener: OnClickListener){
        viewOnClick = listener
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RvFrViewHolder {
        val viewHolder = FriendrvCustomBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RvFrViewHolder(viewHolder)
    }

    override fun onBindViewHolder(
        holder: RvFrViewHolder,
        position: Int
    ) {
        holder.binding.apply{
            textUsername.text = list[position].email.toString()
        }
        holder.binding.root.setOnClickListener {
            viewOnClick?.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}