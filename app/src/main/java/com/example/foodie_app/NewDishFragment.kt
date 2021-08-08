package com.example.foodie_app

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.foodie_app.databinding.FragmentNewDishBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class NewDishFragment : Fragment() {
    //to implement view binding, we need to get a refgerence to the binding
    //object for the corresponding layout file
    //its null bc it will be null until the layout is inflated in onCreateView()
    private var _binding: FragmentNewDishBinding? = null
    //non null assertion when you know its not null
    private val binding get() = _binding!!

    private val today = MaterialDatePicker.todayInUtcMilliseconds()
    private lateinit var datePicker:MaterialDatePicker<Long>

    val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var photoFile : File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        dateBtn.text = getDateTime(today)
        dateBtn.setOnClickListener {
            datePicker.show(this.parentFragmentManager, datePicker.toString())
        }
        val addPicBtn = binding.addPicBtn
        addPicBtn.setOnClickListener {
            showPictureOptions()
        }
    }

    private fun createImageFile() : File {
        //create file name
        val timeStamp:String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = this.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val uniquePrefix:String = "JPEG_${timeStamp}_"
        return File.createTempFile(uniquePrefix, ".jpg", storageDir)

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
            val dateString = getDateTime(dateSelection!!)
            binding.btnDate.text = dateString
        }
    }

    private fun dispatchChoosePictureIntent() {
        val intent1 = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent1, 2)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()
        //NOTE: this doesnt work for API >= 24
        //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)
        val fileProvider = FileProvider.getUriForFile(this.requireContext(), "com.example.foodie_app.fileprovider", photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider) //passing in a file provider allows for more secure content sharing
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            Toast.makeText(this.context, "Unable to open camera", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            val takenImage = data?.extras?.get("data") as Bitmap //low quality image
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            Log.d("NewDishFragment", "${photoFile.absolutePath}")
            Log.d("NewDishFragment", "${takenImage}")
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

            val aspectRatio = takenImage.width / takenImage.height
            val newWidth = displayMetrics.widthPixels

            val newHeight = (newWidth.toDouble() / aspectRatio).roundToInt()



            Log.d("NewDishFragment", "${displayMetrics.widthPixels}, ${displayMetrics.heightPixels}")
            binding.imageView.setImageBitmap(Bitmap.createScaledBitmap(takenImage, newWidth, newHeight, false))
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showPictureOptions() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Photo")
            .setMessage("Select a method to add a photo")
            .setCancelable(true)
            .setNegativeButton("Choose From Gallery") { _, _ ->
                dispatchChoosePictureIntent()
            }
            .setPositiveButton("Take Photo") { _, _ ->
                dispatchTakePictureIntent()
            }
            .show()
    }


    //converts epoch time to date string format
    private fun getDateTime(s: Long): String? {
        val timeZone = TimeZone.getDefault()
        val offset = timeZone.getOffset(s)
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val netDate = Date(s - offset)
        return sdf.format(netDate)
    }
}