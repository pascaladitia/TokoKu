package com.pascal.tokoku.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pascal.tokoku.common.utils.isVisibility
import com.pascal.tokoku.data.local.entity.StoreEntity
import com.pascal.tokoku.databinding.ItemStoreBinding

class AdapterStore(
    val data: List<StoreEntity?>?,
    val itemClick: OnClickListener
) : RecyclerView.Adapter<AdapterStore.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data?.get(position)
        holder.bind(item!!)
    }

    override fun getItemCount(): Int = data!!.size

    inner class ViewHolder(val binding: ItemStoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoreEntity) {

            with(binding) {
                if (item.isVisit == true) {
                    isVisibility(cheklistVisit, true)
                }

                name.text = item.storeName
                subName.text = "${item.address}-${item.storeCode}"
                distance.text = "${item.distance}m"

                root.setOnClickListener {
                    itemClick.detail(item)
                }
            }

        }
    }

    interface OnClickListener {
        fun detail(item: StoreEntity)
    }
}