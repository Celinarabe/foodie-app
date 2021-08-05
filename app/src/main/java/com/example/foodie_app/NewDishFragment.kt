package com.example.foodie_app

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.foodie_app.databinding.FragmentNewDishBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*


class NewDishFragment : Fragment() {
    //to implement view binding, we need to get a refgerence to the binding
    //object for the corresponding layout file
    //its null bc it will be null until the layout is inflated in onCreateView()
    private var _binding: FragmentNewDishBinding? = null
    //non null assertion when you know its not null
    private val binding get() = _binding!!

    private val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Select date")
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewDishBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            //access date button
        val dateBtn = binding.btnDate
        dateBtn.setOnClickListener {
            Log.d("NewDishFragment", "in onclick")
            datePicker.show(this.parentFragmentManager, datePicker.toString())
            datePicker.addOnPositiveButtonClickListener {
                // Respond to positive button click.
                Log.d("NewDishFragment", "in  positive onclick")

                val dateSelection = datePicker.selection
                val dateString = getDateTime(dateSelection.toString())
                binding.btnDate.text = dateString

            }

        }


    }

    private fun getDateTime(s: String): String? {
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val netDate = Date(s.toLong())
        return sdf.format(netDate)

    }

}