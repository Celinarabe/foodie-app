package com.example.foodie_app

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.foodie_app.databinding.FragmentDishDetailBinding
import com.example.foodie_app.db.entities.Dish
import com.example.foodie_app.utilities.TimeUtility.getDateTime
import com.example.foodie_app.view_models.DishViewModel
import com.example.foodie_app.view_models.DishViewModelFactory


class DishDetailFragment : Fragment() {

    private val navigationArgs: DishDetailFragmentArgs by navArgs()

    //view binding
    private var _binding: FragmentDishDetailBinding? = null
    //non null assertion when you know its not null
    private val binding get() = _binding!!
    //DishViewModel thats shared across all fragments in MainActivity
    private val sharedViewModel: DishViewModel by activityViewModels() {
        DishViewModelFactory(
            (activity?.application as DishApplication).database
                .dishDAO()
        )
    }

    private lateinit var currDish: Dish

    private fun bind(dish:Dish) {
        binding.apply{
            tvDishName.text = dish.name
            tvDate.text = getDateTime(dish.date)
            tvDishLocation.text = dish.location
            tvDishNotes.text = dish.notes
            val uri = Uri.parse(currDish.dishUri)
            Glide.with(requireContext()).load(uri).into(imgSelectedDish)

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDishDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dishID = navigationArgs.itemId
        sharedViewModel.getDish(dishID).observe(this.viewLifecycleOwner) { selectedDish ->
            currDish = selectedDish
            bind(currDish)
        }
    }

    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}