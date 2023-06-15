package com.example.finderapp.core

import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import coil.load
import coil.transform.CircleCropTransformation
import com.example.finderapp.R
import com.example.finderapp.data.model.Categories
import com.example.finderapp.data.model.Permissions
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

fun List<String>.toFormatAddress(): String{
    var addressList = ""
    this.forEach() {
        addressList = "$addressList$it. "
    }
    return addressList
}

fun List<Categories>.toFormatCategories(): String{
    var categories = ""
    this.forEach {
        categories = categories + it.title + ". "
    }
    return categories
}

fun ImageView.loadImage(url: String, isCircle: Boolean){
    if(url.isNotEmpty()){
        this.load(url){
            crossfade(true)
            crossfade(200)
            if(isCircle) transformations(CircleCropTransformation())
        }
    }else{
        this.load(R.drawable.not_found){
            if(isCircle) transformations(CircleCropTransformation())
        }
    }
}

fun Boolean.validateBusiness(): Int{
    return  if(this) R.drawable.ic_red_circle else R.drawable.ic_green_circle
}

fun Double.toDistanceFormat(digits: Int): String{
    val df = DecimalFormat()
    df.maximumFractionDigits = digits
    var finalDistance = this

    return if(finalDistance >= 1000){
        finalDistance /= 1000
        df.format(finalDistance) + " km"
    }else{
        df.format(finalDistance) + " m"
    }
}

fun Dialog.initialize(view: View, isCancelable: Boolean): Dialog {
    this.setContentView(view)
    this.setCancelable(isCancelable)
    this.window?.setBackgroundDrawableResource(android.R.color.transparent)
    return this
}


fun validatePermissions(context: Context ,typePermission: String): Permissions{
    return if (ContextCompat.checkSelfPermission(context, typePermission) == PackageManager.PERMISSION_GRANTED) {
        Permissions.ACCEPTED
    } else {
        Permissions.REFUSED
    }

//    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//        if (ContextCompat.checkSelfPermission(context, typePermission) == PackageManager.PERMISSION_GRANTED) {
//            Permissions.ACCEPTED
//        }else{
//            Permissions.REFUSED
//        }
//
//    }else{
//        Permissions.PREVIOUS_VERSIONS
//    }
}

fun List<String>.toLoadCarousel(): List<CarouselItem>{
    val list = mutableListOf<CarouselItem>()
    this.forEach {
        list.add(CarouselItem(it))
    }
   return list
}

fun String.toDateFormat(): String {
    //for the string received
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    //for the new string format
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    //parse the string
    val stringParsed = parser.parse(this)
    //add the new format to string
    val date: String? = stringParsed?.let {
        formatter.format(stringParsed)
    }
    return date ?: ""
}