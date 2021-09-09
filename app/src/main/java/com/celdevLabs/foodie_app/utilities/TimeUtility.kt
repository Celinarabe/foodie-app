package com.celdevLabs.foodie_app.utilities

import java.text.SimpleDateFormat
import java.util.*

object TimeUtility {

    //converts epoch time to string format
    fun getDateTime(s: Long): String {
        val timeZone = TimeZone.getDefault()
        val offset = timeZone.getOffset(s)
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val netDate = Date(s - offset)
        return sdf.format(netDate)
    }


}