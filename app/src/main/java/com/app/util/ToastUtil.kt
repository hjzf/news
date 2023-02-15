package com.app.util

import android.widget.Toast
import com.app.NewsApplication

/**
 * String的拓展函数:弹出Toast,内容是字符串本身
 */
fun String.showToast() {
    try {
        Toast.makeText(NewsApplication.context, this, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}