package com.celdevLabs.foodie_app

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.celdevLabs.foodie_app.databinding.FragmentNewDishBinding
import com.celdevLabs.foodie_app.db.entities.Dish
import com.celdevLabs.foodie_app.utilities.TimeUtility
import com.celdevLabs.foodie_app.view_models.DishViewModel
import com.celdevLabs.foodie_app.view_models.DishViewModelFactory
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*



class NewDishFragment : Fragment() {

    //view binding
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

    private val STORAGE_PERMISSION_CODE = 1
    //image capture request code
    val REQUEST_IMAGE_CAPTURE = 1

    //Date Picker - default today's date
    private var selectedDate = MaterialDatePicker.todayInUtcMilliseconds()
    //datePicker object
    private var datePicker: MaterialDatePicker<Long>? = null

    //photo file obj uploaded by user
    private var photoFile: File? = null
    private var photoUri: Uri? = null

    private var currDish: Dish? = null
    private val navigationArgs: NewDishFragmentArgs by navArgs()



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
        val dishID = navigationArgs.dishId
        binding.apply {
            addPicBtn.setOnClickListener {
                this@NewDishFragment.showPictureOptions()
            }
            imageView.setOnClickListener {
                this@NewDishFragment.showPictureOptions()
            }
            btnDate.setOnClickListener {
                datePicker?.show(this@NewDishFragment.parentFragmentManager, datePicker.toString())
            }
        }
        if (dishID == -1) NewDishBind() else {
            sharedViewModel.getDish(dishID).observe(this.viewLifecycleOwner) { selectedItem ->
                currDish = selectedItem
                EditDishBind(currDish!!)
            }
        }
    }

    private fun NewDishBind() {
        binding?.apply {
            btnDate.text = TimeUtility.getDateTime(selectedDate)
            btnSave.setOnClickListener { this@NewDishFragment.saveDish() }
            imageView.setImageResource(R.drawable.ic_baseline_fastfood_24)
        }
    }

    //bind view
    private fun EditDishBind(dish: Dish) {

        binding.apply {
            etDishName.setText(dish.name, TextView.BufferType.SPANNABLE)
            etLocation.setText(dish.location, TextView.BufferType.SPANNABLE)
            btnDate.text = TimeUtility.getDateTime(dish.date)
            etNotes.setText(dish.notes, TextView.BufferType.SPANNABLE)
            if (currDish!!.dishUri != "null") {
                val uri = Uri.parse(dish.dishUri)
                Glide.with(requireContext()).load(uri).into(imageView)
                photoUri = uri
            } else {
                imageView.setImageResource(R.drawable.ic_baseline_fastfood_24)
            }

            btnSave.setOnClickListener { this@NewDishFragment.updateDish() }
        }
    }


    //TO DO: consolidate save/update dish
    private fun updateDish() {
        val newName: String = binding.etDishName.text.toString()
        Log.d("NewDishFragment", "new name: ${newName}")
        val newDate: Long = selectedDate
        val location: String = binding.etLocation.text.toString()
        val notes: String = binding.etNotes.text.toString()
        if (sharedViewModel.isEntryValid(newName)) {
            sharedViewModel.updateDish(currDish!!.idx,newName, newDate, location, notes, photoUri.toString())
            Toast.makeText(requireContext(), "Successfully Updated", Toast.LENGTH_SHORT).show()
            val action = NewDishFragmentDirections.actionNewDishFragmentToListFeedFragment()
            findNavController().navigate(action)
        }
    }



    //save dish to ViewModel
    private fun saveDish() {
        val newName: String = binding.etDishName.text.toString()
        val newDate: Long = selectedDate
        val location: String = binding.etLocation.text.toString()
        val notes: String = binding.etNotes.text.toString()
        if (sharedViewModel.isEntryValid(newName)) {
            sharedViewModel.addNewDish(newName, newDate, location, notes, photoUri.toString())
            val action = NewDishFragmentDirections.actionNewDishFragmentToListFeedFragment()
            findNavController().navigate(action)
        } else {
            // Set error text
            binding.etDishName.error = getString(R.string.dish_name_validation)
            Toast.makeText(requireContext(), "Must enter a name", Toast.LENGTH_SHORT).show()

        }
    }



    //create MaterialUI's date picker
    private fun buildDatePicker() {
        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
        //constrain calendar to today and backwards
        val constraintsBuilder =
            CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now())
        datePickerBuilder.setTitleText("Select Date")
        datePickerBuilder.setCalendarConstraints(constraintsBuilder.build())
        datePicker = datePickerBuilder.build()
        datePicker!!.addOnPositiveButtonClickListener {
            // Respond to positive button click.
            val dateSelection = datePicker!!.selection
            val dateString = TimeUtility.getDateTime(dateSelection!!)
            selectedDate = dateSelection
            binding.btnDate.text = dateString

        }
    }


    //sets imageView to user's photo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("NewDishFragment", "got a result: $requestCode $resultCode ${Activity.RESULT_OK}")
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                photoUri = Uri.fromFile(photoFile)
                Glide.with(requireContext()).load(photoUri).into(binding.imageView)
            } else { //getting image from gallery
                if (data !== null) {
                    photoUri = data?.data!!
                    Glide.with(requireContext()).load(photoUri).into(binding.imageView)
                }
            }
            Log.d("NewDishFragment", "newURI: $photoUri")
        } catch (e: IOException) {
            Log.i("TAG", "Some exception $e")
        }
    }

    //alerts user to upload or take photo
    private fun showPictureOptions() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Photo")
            .setMessage("Select a method to add a photo")
            .setCancelable(true)
            .setNegativeButton("Choose From Gallery") { _, _ ->
                showPermission()
            }
            .setPositiveButton("Take Photo") { _, _ ->
                dispatchTakePictureIntent()
            }
            .show()
    }

    //check permission and if not granted, launch request
    fun showPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            dispatchChoosePictureIntent()
        } else{
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
            AlertDialog.Builder(requireContext()).setTitle("Permission Needed").setMessage("Allow app to access photos?")
                .setPositiveButton("OK", DialogInterface.OnClickListener(
                    fun (dialogInterface: DialogInterface, which: Int){
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                    }
                ))
                .setNegativeButton("Cancel", DialogInterface.OnClickListener(
                    fun (dialogInterface: DialogInterface, which:Int) {
                        dialogInterface.dismiss()
                    }
                ))
                .create().show()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    //creates file w/ unique name
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = this.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val uniquePrefix: String = "JPEG_${timeStamp}_"
        return File.createTempFile(uniquePrefix, ".jpg", storageDir)
    }

    private fun dispatchChoosePictureIntent() {
        val intent1 = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent1, 2)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()
        val fileProvider = FileProvider.getUriForFile(
            this.requireContext(),
            "com.celdevLabs.foodie_app.fileprovider",
            photoFile!!
        )
        takePictureIntent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            fileProvider
        ) //passing in a file provider allows for more secure content sharing
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            Toast.makeText(this.context, "Unable to open camera", Toast.LENGTH_SHORT).show()
        }
    }
}