package com.example.foodie_app

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.foodie_app.databinding.FragmentNewDishBinding
import com.example.foodie_app.utilities.BitmapUtility
import com.example.foodie_app.view_models.DishViewModel
import com.example.foodie_app.view_models.DishViewModelFactory
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class NewDishFragment : Fragment() {
    /*
     * VIEW BINDING
     */
    //reference to view binding object
    private var _binding: FragmentNewDishBinding? = null
    //non null assertion when you know its not null
    private val binding get() = _binding!!
    //DishViewModel thats shared across all fragments in MainActivity
    private val sharedViewModel: DishViewModel by activityViewModels() {
        DishViewModelFactory(
            (activity?.application as DishApplication).database
                .dishDAO()
        )
    }

    /*
     * DATE PICKER
     */
    //today's date to populate default date in date picker
    private val today = MaterialDatePicker.todayInUtcMilliseconds()
    //datePicker object
    private lateinit var datePicker:MaterialDatePicker<Long>

    /*
     * IMAGE HANDLING
     */
    //image capture request code
    val REQUEST_IMAGE_CAPTURE = 1
    //photo file obj uploaded by user
    private var photoFile: File? = null

    private var photoPath:String = ""
    //photo byte array to insert into db
    private var photoArray : ByteArray? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewDishBinding.inflate(inflater, container, false)
        buildDatePicker()
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply{
            btnDate.text = getDateTime(today)
            btnDate.setOnClickListener {
                datePicker.show(this@NewDishFragment.parentFragmentManager, datePicker.toString())
            }
            addPicBtn.setOnClickListener {
                this@NewDishFragment.showPictureOptions()
            }
            btnSave.setOnClickListener { this@NewDishFragment.saveDish() }
        }
    }

    //save dish to ViewModel
    private fun saveDish() {

        val newName :String = binding.etDishName.text.toString()
        val newDate :String = binding.btnDate.text.toString()
        val location :String = binding.etLocation.text.toString()
        val notes :String = binding.etNotes.text.toString()
        if (sharedViewModel.isEntryValid(newName)) {
            sharedViewModel.addNewDish(newName, newDate, location, notes, photoArray)
            val action = NewDishFragmentDirections.actionNewDishFragmentToListFeedFragment()
            findNavController().navigate(action)
        }
    }

    //create MaterialUI's date picker
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


    //sets imageView to user's photo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            photoPath = photoFile?.absolutePath.toString()
            val takenImage = BitmapFactory.decodeFile(photoPath)
            val sizedPhoto = resizePhoto(takenImage)
            photoArray = BitmapUtility.getBytes(sizedPhoto)
            binding.imageView.setImageBitmap(sizedPhoto)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    //alerts user to upload or take photo
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


    //resizes and rotates photo
    fun resizePhoto(oldPhoto:Bitmap):Bitmap {
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val aspectRatio = oldPhoto.width / oldPhoto.height
        val newWidth = displayMetrics.widthPixels
        val matrix = Matrix()
        matrix.postRotate(90.0F)
        val newHeight = (newWidth.toDouble() / aspectRatio).roundToInt()
        val scaledBitmap = Bitmap.createScaledBitmap(oldPhoto, newWidth, newHeight, false)
        val rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
        return rotatedBitmap
    }

    //converts epoch time to string format
    private fun getDateTime(s: Long): String? {
        val timeZone = TimeZone.getDefault()
        val offset = timeZone.getOffset(s)
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val netDate = Date(s - offset)
        return sdf.format(netDate)
    }

    //creates file w/ unique name
    private fun createImageFile() : File {
        val timeStamp:String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = this.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val uniquePrefix:String = "JPEG_${timeStamp}_"
        return File.createTempFile(uniquePrefix, ".jpg", storageDir)
    }

    private fun dispatchChoosePictureIntent() {
        val intent1 = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent1, 2)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()
        val fileProvider = FileProvider.getUriForFile(this.requireContext(), "com.example.foodie_app.fileprovider", photoFile!!)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider) //passing in a file provider allows for more secure content sharing
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            Toast.makeText(this.context, "Unable to open camera", Toast.LENGTH_SHORT).show()
        }
    }

}