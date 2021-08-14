package com.example.foodie_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foodie_app.databinding.FragmentListFeedBinding


class ListFeedFragment : Fragment() {
    //DishViewModel
    //private val sharedViewModel: DishViewModel by activityViewModels()
    //reference to the view binding object
    private var _binding: FragmentListFeedBinding? = null
    //non null assertion when you know its not null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentListFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val dishDAO = DishDatabase.getInstance(requireContext()).dishDAO()
//        val dishRepository = DishRepository(dishDAO)
//        sharedViewModel = DishViewModel(dishRepository)

        //binding.tvDishName.text = sharedViewModel.newDishObj?.name

    }
}