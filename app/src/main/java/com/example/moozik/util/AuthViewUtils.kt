package com.example.moozik.util

import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView

fun View.collectEditTexts(): List<EditText> {
    val result = mutableListOf<EditText>()
    collectEditTextsInto(result)
    return result
}

fun View.findImageViewByContentDescription(text: String): ImageView? {
    return collectImageViews().firstOrNull {
        it.contentDescription?.toString()?.contains(text, ignoreCase = true) == true
    }
}

private fun View.collectEditTextsInto(result: MutableList<EditText>) {
    if (this is EditText) {
        result.add(this)
    }
    if (this is ViewGroup) {
        for (i in 0 until childCount) {
            getChildAt(i).collectEditTextsInto(result)
        }
    }
}

private fun View.collectImageViews(): List<ImageView> {
    val result = mutableListOf<ImageView>()
    collectImageViewsInto(result)
    return result
}

private fun View.collectImageViewsInto(result: MutableList<ImageView>) {
    if (this is ImageView) {
        result.add(this)
    }
    if (this is ViewGroup) {
        for (i in 0 until childCount) {
            getChildAt(i).collectImageViewsInto(result)
        }
    }
}



