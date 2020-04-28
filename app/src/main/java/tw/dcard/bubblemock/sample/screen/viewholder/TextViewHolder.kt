package tw.dcard.bubblemock.sample.screen.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_text.view.*
import tw.dcard.bubblemock.sample.R

class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun create(parent: ViewGroup) =
            TextViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.list_item_text, parent, false)
            )
    }

    private val textView = itemView.textView!!

    fun bind(message: String) {
        textView.text = message
    }
}