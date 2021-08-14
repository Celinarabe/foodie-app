package com.example.foodie_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodie_app.databinding.FragmentListFeedBinding
import com.example.foodie_app.view_models.DishViewModel
import com.example.foodie_app.view_models.DishViewModelFactory


class ListFeedFragment : Fragment() {
    //DishViewModel
    //private val sharedViewModel: DishViewModel by activityViewModels()
    //reference to the view binding object
    private var _binding: FragmentListFeedBinding? = null
    //non null assertion when you know its not null
    private val binding get() = _binding!!

    private val viewModel: DishViewModel by activityViewModels {
        DishViewModelFactory(
            (activity?.application as DishApplication).database.dishDAO()
        )
    }

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
        val adapter = DishListAdapter{
        }
        binding.recyclerView.adapter = adapter
        viewModel.allDishes.observe(this.viewLifecycleOwner){
            dishes -> dishes.let {
                adapter.submitList(it)
        }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)


    }
}