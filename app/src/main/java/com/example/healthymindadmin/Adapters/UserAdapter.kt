package com.example.healthymindadmin.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthymindadmin.Models.UsersModel
import com.example.healthymindadmin.R
class UserAdapter (private val userList: List<UsersModel>,
                   private val deleteListener: UserAdapter.DeleteListener):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>(){


    interface DeleteListener {
        fun onLongClick(position: Int, uid: String?)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val users = userList[position]
        holder.bind(users)

        holder.itemView.setOnLongClickListener {
            deleteListener.onLongClick(position, users.uid)
            true
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userId: TextView = itemView.findViewById(R.id.tv_uid1)
        val userName: TextView = itemView.findViewById(R.id.tv_resultId1)
        val userEmail: TextView = itemView.findViewById(R.id.tv_categoryName1)
        val userPassword: TextView = itemView.findViewById(R.id.tv_result1)

        fun bind(users : UsersModel){

            userId.text = users.uid
            userName.text = users.name
            userEmail.text = users.email
            userPassword.text = users.password
        }
    }
}