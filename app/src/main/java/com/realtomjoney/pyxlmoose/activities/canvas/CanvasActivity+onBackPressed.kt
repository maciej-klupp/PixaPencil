package com.realtomjoney.pyxlmoose.activities.canvas

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.core.view.drawToBitmap
import com.realtomjoney.pyxlmoose.activities.main.MainActivity
import com.realtomjoney.pyxlmoose.converters.BitmapConverter
import com.realtomjoney.pyxlmoose.converters.JsonConverter
import com.realtomjoney.pyxlmoose.database.AppData
import com.realtomjoney.pyxlmoose.extensions.navigateHome
import com.realtomjoney.pyxlmoose.utility.StringConstants

fun CanvasActivity.extendedOnBackPressed() {
    if (currentFragmentInstance != null) {
        currentFragmentInstance = null
        navigateHome(supportFragmentManager, currentFragmentInstance!!, binding.activityCanvasRootLayout, binding.activityCanvasPrimaryFragmentHost, StringConstants.APP_NAME)
    } else {
        startActivity(Intent(context, MainActivity::class.java))
    }
}