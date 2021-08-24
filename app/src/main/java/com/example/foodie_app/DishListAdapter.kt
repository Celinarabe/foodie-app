package com.example.foodie_app

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodie_app.databinding.FragmentListItemBinding
import com.example.foodie_app.db.entities.Dish


class DishListAdapter(private val context: Context, private val onItemClicked: (Dish) -> Unit) :
    ListAdapter<Dish, DishListAdapter.DishViewHolder>(DiffCallback) {

    inner class DishViewHolder(private var binding: FragmentListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(currDish : Dish){
            binding.apply {
                tvDishName.text = currDish.name
                tvDishLocation.text = currDish.location
                var uri = Uri.parse(currDish.dishUri)
                Glide.with(context).load(uri).into(imgDish)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishListAdapter.DishViewHolder {
        val viewObj = FragmentListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DishViewHolder(viewObj)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.itemView.setOnClickListener{
            onItemClicked(currentItem)
        }
        holder.bind(currentItem)
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