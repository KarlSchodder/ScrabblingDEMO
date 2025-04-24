package com.example.ccl3_scrabbling.data

import android.content.Context
import android.util.Log
import com.example.ccl3_scrabbling.R
import java.io.BufferedReader
import java.io.InputStreamReader

lateinit var dictionary: List<String>

fun loadDictionaryContent(context: Context) {
    try {
        Log.d("Dictionary", "Loading dictionary")
        val inputStream = context.resources.openRawResource(R.raw.content)
        val reader = BufferedReader(InputStreamReader(inputStream))
        dictionary = reader.readLines()
        reader.close()
        Log.d("Dictionary", "Dictionary loaded")
    } catch (e: Exception) {
        Log.e("Dictionary", "Error loading dictionary", e)
    }
}