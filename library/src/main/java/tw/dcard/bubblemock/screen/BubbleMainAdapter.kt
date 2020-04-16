package tw.dcard.bubblemock.screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_bubble.view.*
import tw.dcard.bubblemock.R

/**
 * @author Batu
 */
class BubbleMainAdapter(
    private val interaction: Interaction
) : RecyclerView.Adapter<BubbleMainAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        companion object {

            fun create(parent: ViewGroup) =
                ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_bubble, parent, false)
                )
        }

        private val messageTextView = itemView.messageTextView!!
        private val pageNameTextView = itemView.pageNameTextView!!
        private val checkImageView = itemView.checkImageView!!

        fun bind(item: BubbleMainItem) {
            pageNameTextView.text = item.pageName
            messageTextView.text = item.message
            checkImageView.visibility = if (item.selected) View.VISIBLE else View.INVISIBLE
        }
    }

    interface Interaction {
        fun onItemSelected(index: Int, selected: Boolean)
    }

    var items: List<BubbleMainItem>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.create(parent)

    override fun getItemCount() = items?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items!![position])
        holder.itemView.tag = position
        holder.itemView.setOnClickListener {
            val index = it.tag as Int
            val item = items!![index]
            item.selected = !item.selected
            notifyItemChanged(index)

            interaction.onItemSelected(index, item.selected)
        }
    }
}