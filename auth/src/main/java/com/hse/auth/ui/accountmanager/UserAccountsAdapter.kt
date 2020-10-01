package com.hse.auth.ui.accountmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.hse.auth.R
import com.hse.auth.ui.models.UserAccountData
import com.hse.core.common.onClick
import kotlinx.android.synthetic.main.item_user_account.view.*

class UserAccountsAdapter(
    private val onUserClickListener: ((UserAccountData) -> Unit)
) : ListAdapter<UserAccountData, UserAccountsAdapter.UserAccountViewHolder>(
    DIFF_CALLBACK
) {

    companion object {
        //TODO
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserAccountData>() {
            override fun areItemsTheSame(
                oldItem: UserAccountData,
                newItem: UserAccountData
            ): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: UserAccountData,
                newItem: UserAccountData
            ): Boolean {
                return false
            }

        }
    }

    class UserAccountViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAccountViewHolder =
        UserAccountViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_account, parent, false)
        )


    override fun onBindViewHolder(holder: UserAccountViewHolder, position: Int) {
        val item = getItem(position)
        holder.view.userEmailTv.text = item.email
        holder.view.userAvatarIv.load(item.avatartUrl) {
            crossfade(true)
            error(R.drawable.ic_person)
            fallback(R.drawable.ic_person)
            placeholder(R.drawable.ic_person)
        }
        holder.view.userContainerLl.onClick {
            onUserClickListener.invoke(item)
        }
    }
}