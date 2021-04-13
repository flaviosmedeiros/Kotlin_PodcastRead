package br.ufpe.cin.android.podcast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.prof.rssparser.Article

class Adapter(private val list: MutableList<Article>, var onTitleListener: OnClickTitleListener): RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemfeed, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = list[position].title
        holder.date.text = list[position].pubDate

        holder.title.setOnClickListener {
            onTitleListener.onClick(list[position])
        }
    }


    //## MainActivy como responsavel
    interface OnClickTitleListener {
        fun onClick(podCast: Article)
    }
}