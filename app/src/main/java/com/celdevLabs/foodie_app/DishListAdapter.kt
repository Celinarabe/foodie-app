package com.celdevLabs.foodie_app

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.celdevLabs.foodie_app.databinding.FragmentGridItemBinding
import com.celdevLabs.foodie_app.databinding.FragmentListItemBinding
import com.celdevLabs.foodie_app.db.entities.Dish
import com.celdevLabs.foodie_app.utilities.TimeUtility.getDateTime


class DishListAdapter(private val isLinear: Boolean, private val context: Context, private val onItemClicked: (Dish) -> Unit) :
    ListAdapter<Dish, RecyclerView.ViewHolder>(DiffCallback) {

    inner class DishViewHolder(private var binding: FragmentListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(currDish : Dish){
            binding.apply {
                tvDishName.text = currDish.name
                tvDishDate.text = getDateTime(currDish.date)
                if (currDish.dishUri != "null") {
                    var uri = Uri.parse(currDish.dishUri)
                    Glide.with(context).load(uri).centerCrop().into(imgDish)
                }
            }
        }
    }

    inner class DishViewHolderGrid(private var binding: FragmentGridItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(currDish : Dish){
            binding.apply {

                if (currDish.dishUri != "null") {
                    var uri = Uri.parse(currDish.dishUri)
                    Glide.with(context).load(uri).fitCenter().into(imgDish)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (isLinear) {
            val viewObj =
                FragmentListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DishViewHolder(viewObj)
        } else {
            val viewObj =
                FragmentGridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DishViewHolderGrid(viewObj)
        }
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.itemView.setOnClickListener{
            onItemClicked(currentItem)
        }
        if (isLinear) {
            Log.d("DishListAdapter", "${holder}")
            (holder as DishViewHolder).bind(currentItem)
        } else {
            (holder as DishViewHolderGrid).bind(currentItem)
        }
    }



    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Dish>() {
            override fun areItemsTheSame(oldItem: Dish, newItem: Dish): Boolean {
                return oldItem === newItem
            }
            override fun areContentsTheSame(
                oldItem: Dish,
                newItem: Dish
            ): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}