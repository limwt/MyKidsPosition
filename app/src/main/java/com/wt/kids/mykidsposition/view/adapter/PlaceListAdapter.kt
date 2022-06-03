package com.wt.kids.mykidsposition.view.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wt.kids.mykidsposition.R
import com.wt.kids.mykidsposition.data.response.ResponseItemsData
import com.wt.kids.mykidsposition.utils.Logger
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PlaceListAdapter @Inject constructor(
    private val logger: Logger
) : ListAdapter<ResponseItemsData, PlaceListAdapter.ItemViewHolder>(differ) {
    private var itemClickListener: OnItemClickListener? = null

    enum class SheetType {
        TYPE_NONE,
        TYPE_SEARCH,
        TYPE_SAVED
    }

    private var currentType = SheetType.TYPE_NONE

    interface OnItemClickListener {
        fun onItemClick(view: View, data: ResponseItemsData)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClickListener = listener
    }

    fun setSheetType(type: SheetType) {
        currentType = type
    }

    fun getCurrentSheetType() = currentType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.item_place, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: ResponseItemsData) {
            val container = view.findViewById<View>(R.id.placeTextContainer)
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val addressTextView = view.findViewById<TextView>(R.id.addressTextView)
            val deleteButton = view.findViewById<ImageView>(R.id.deleteButton)
            deleteButton.visibility = if (currentType == SheetType.TYPE_SAVED) View.VISIBLE else View.GONE

            deleteButton.setOnClickListener { v ->
                itemClickListener?.onItemClick(v, data)
            }

            titleTextView.text = Html.fromHtml(data.title, HtmlCompat.FROM_HTML_MODE_LEGACY)
            addressTextView.text = Html.fromHtml(data.roadAddress.ifEmpty { data.address }, HtmlCompat.FROM_HTML_MODE_LEGACY)

            container.setOnClickListener { v ->
                itemClickListener?.onItemClick(v, data)
            }
        }
    }

    companion object {
        private val logTag = "PlaceListAdapter"

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