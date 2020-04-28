package tw.dcard.bubblemock.sample.screen.main

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tw.dcard.bubblemock.sample.model.Member
import tw.dcard.bubblemock.sample.screen.viewholder.MemberViewHolder
import tw.dcard.bubblemock.sample.screen.viewholder.TextViewHolder

class MainAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_MEMBER = 0x01
        const val ITEM_TYPE_TEXT = 0x10
    }

    interface Item

    data class MemberItem(val member: Member) : Item

    data class TextItem(val text: String) : Item

    var items: List<Item>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int = when (items!![position]) {
        is MemberItem -> ITEM_TYPE_MEMBER
        is TextItem -> ITEM_TYPE_TEXT
        else -> throw IllegalArgumentException("Unknown item type.")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_MEMBER -> MemberViewHolder.create(parent)
            ITEM_TYPE_TEXT -> TextViewHolder.create(parent)
            else -> throw IllegalArgumentException("Unknown view holder for view type: $viewType.")
        }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        items?.get(position)?.let { item ->
            when (item) {
                is MemberItem -> (holder as MemberViewHolder).bind(item.member)
                is TextItem -> (holder as TextViewHolder).bind(item.text)
            }
        }
    }
}