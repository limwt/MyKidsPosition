package com.wt.kids.mykidsposition.view.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wt.kids.mykidsposition.R
import com.wt.kids.mykidsposition.data.response.ResponseItemsData

class PlaceListAdapter :
    ListAdapter<ResponseItemsData, PlaceListAdapter.ItemViewHolder>(differ) {
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: ResponseItemsData) {
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val addressTextView = view.findViewById<TextView>(R.id.addressTextView)
            titleTextView.text = Html.fromHtml(data.title, HtmlCompat.FROM_HTML_MODE_LEGACY)
            addressTextView.text = Html.fromHtml(data.roadAddress.ifEmpty { data.address }, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.item_place, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val differ = object : DiffUtil.ItemCallback<ResponseItemsData>() {
            override fun areContentsTheSame(oldItem: ResponseItemsData, newItem: ResponseItemsData): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: ResponseItemsData, newItem: ResponseItemsData): Boolean {
                return (oldItem.mapx == newItem.mapx) && (oldItem.mapy == newItem.mapy)
            }

        }
    }

}