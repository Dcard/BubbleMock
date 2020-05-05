package tw.dcard.bubblemock.sample.screen.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_member.view.*
import tw.dcard.bubblemock.sample.R
import tw.dcard.bubblemock.sample.model.Member

class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun create(parent: ViewGroup) =
            MemberViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.list_item_member,
                    parent,
                    false
                )
            )
    }

    private val nameTextView = itemView.nameTextView!!
    private val ageTextView = itemView.ageTextView!!
    private val addressTextView = itemView.addressTextView!!

    fun bind(member: Member) {
        nameTextView.text = member.name
        ageTextView.text = member.age?.toString()
        addressTextView.text = member.address
    }
}