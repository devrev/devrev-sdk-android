package ai.devrev.sdk.sample.adapter

import android.R
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewListAdapter(
    private val items: List<String>,
    private val onBindCard: (CardView, Int) -> Unit // callback for business logic
) : RecyclerView.Adapter<RecyclerViewListAdapter.ViewHolder>() {

    class ViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        val textView: TextView = cardView.findViewById(R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = CardView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                val margin = (8 * parent.context.resources.displayMetrics.density).toInt()
                setMargins(margin, margin, margin, margin)
            }
            radius = (8 * parent.context.resources.displayMetrics.density)
            cardElevation = (4 * parent.context.resources.displayMetrics.density)
            setContentPadding(
                (16 * parent.context.resources.displayMetrics.density).toInt(),
                (16 * parent.context.resources.displayMetrics.density).toInt(),
                (16 * parent.context.resources.displayMetrics.density).toInt(),
                (16 * parent.context.resources.displayMetrics.density).toInt()
            )
            val textView = TextView(parent.context).apply {
                id = R.id.text1
            }
            addView(textView)
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position]

        // delegate business logic
        onBindCard(holder.cardView, position)
    }

    override fun getItemCount() = items.size
}
