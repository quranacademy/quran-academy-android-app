package org.quranacademy.quran.core.permissions

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.afollestad.assent.Permission

fun Fragment.isPermissionBlockedFromAsking(permission: Permission): Boolean {
    return activity!!.isPermissionBlockedFromAsking(permission)
}

fun Activity.isPermissionBlockedFromAsking(permission: Permission): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return ContextCompat.checkSelfPermission(this, permission.value) != PackageManager.PERMISSION_GRANTED
                && !shouldShowRequestPermissionRationale(permission.value)
    }
    return false
}