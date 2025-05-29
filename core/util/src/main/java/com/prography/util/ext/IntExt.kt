package com.prography.util.ext

import android.content.res.Resources

fun Int.DpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()