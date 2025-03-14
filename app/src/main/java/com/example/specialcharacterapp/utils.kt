package com.example.specialcharacterapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Special Character", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "$text copied to clipboard", Toast.LENGTH_SHORT).show()
}
