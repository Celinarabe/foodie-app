package com.example.foodie_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foodie_app.databinding.FragmentNewDishBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
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

    private val today = MaterialDatePicker.todayInUtcMilliseconds()
    private lateinit var datePicker:MaterialDatePicker<Long>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("NewDishFragment", "$today")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewDishBinding.inflate(inflater, container, false)
        buildDatePicker()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //access date button
        val dateBtn = binding.btnDate
        dateBtn.text = getDateTime(today.toString())
        dateBtn.setOnClickListener {
            datePicker.show(this.parentFragmentManager, datePicker.toString())
        }
    }

    private fun buildDatePicker() {
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
        //constrain calendar to today and backwards
        val constraintsBuilder = CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now())
        datePickerBuilder.setTitleText("Select Date")
        datePickerBuilder.setCalendarConstraints(constraintsBuilder.build())
        datePicker = datePickerBuilder.build()
        datePicker.addOnPositiveButtonClickListener {
            // Respond to positive button click.
            val dateSelection = datePicker.selection
            val dateString = getDateTime(dateSelection.toString())
            binding.btnDate.text = dateString
        }
    }



    private fun getDateTime(s: String): String? {
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val netDate = Date(s.toLong())
        return sdf.format(netDate)

    }

}