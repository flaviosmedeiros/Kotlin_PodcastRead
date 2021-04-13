package br.ufpe.cin.android.podcast

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val title: TextView = view.findViewById(R.id.item_title)
    val date: TextView = view.findViewById(R.id.item_date)
}