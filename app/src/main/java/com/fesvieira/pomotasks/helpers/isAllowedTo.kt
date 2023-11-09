package com.fesvieira.pomotasks.helpers

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat

fun Context.isAllowedTo(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED