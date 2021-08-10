package com.example.foodie_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.foodie_app.databinding.FragmentListFeedBinding
import com.example.foodie_app.entities.DishViewModel


class ListFeedFragment : Fragment() {
    private val sharedViewModel: DishViewModel by activityViewModels()

    //to implement view binding, we need to get a refgerence to the binding
    //object for the corresponding layout file
    //its null bc it will be null until the layout is inflated in onCreateView()
    private var _binding: FragmentListFeedBinding? = null
    //non null assertion when you know its not null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding.tvDishName.text = sharedViewModel._dishObj?.name

    }

}