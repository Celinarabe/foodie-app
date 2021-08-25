package com.example.foodie_app

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
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

    private var isLinearLayoutManager = false

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
        setHasOptionsMenu(true) //enabling options menu on the top app bar
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ListFeedFragment", "in onviewcreated")
        chooseLayout()
        binding.floatingActionButton.setOnClickListener {
            val action = ListFeedFragmentDirections.actionListFeedFragmentToNewDishFragment(
                getString(R.string.add_fragment_title)
            )
            this.findNavController().navigate(action)
        }
    }

    private fun chooseLayout() {
        //create adapter
        val adapter = DishListAdapter(this.requireContext()) {
            val action = ListFeedFragmentDirections.actionListFeedFragmentToDishDetailFragment(it.idx)
            this.findNavController().navigate(action)
        }

        //set layout manager
        if (isLinearLayoutManager) {
            binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        } else {
            binding.recyclerView.layoutManager = GridLayoutManager(this.context, 3, GridLayoutManager.VERTICAL, false)
            Log.d("ListFeedFragment", "in the else")
        }

        //set adapter
        binding.recyclerView.adapter = adapter
        viewModel.allDishes.observe(this.viewLifecycleOwner){
                dishes -> dishes.let {
            adapter.submitList(it)
        }
        }
    }

    private fun setIcon(menuItem: MenuItem?) {
        if (menuItem == null)
            return
        menuItem.icon = if (isLinearLayoutManager)
            ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_grid_on_24)
        else ContextCompat.getDrawable(requireContext(), R.drawable.ic_feed_list)
    }

    //this function inflates the menu (top app bar?)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_example, menu) //using our custom menu
        val layoutButton = menu?.findItem(R.id.action_switch_view)
        setIcon(layoutButton)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_view -> {
                // navigate to settings screen
                isLinearLayoutManager = !isLinearLayoutManager
                chooseLayout()
                setIcon(item)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}