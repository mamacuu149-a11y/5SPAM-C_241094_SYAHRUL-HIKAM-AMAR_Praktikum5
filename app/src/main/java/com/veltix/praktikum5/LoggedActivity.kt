package com.veltix.praktikum5

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

// Kelas dasar: mencatat setiap callback lifecycle ke Logcat dengan tag "Lifecycle".
open class LoggedActivity : AppCompatActivity() {

    private val nama = javaClass.simpleName

    private fun catat(pesan: String) {
        Log.d(TAG, "$nama.$pesan")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val state = if (savedInstanceState == null) "baru" else "dipulihkan"
        catat("onCreate (state=$state)")
    }

    override fun onStart() {
        super.onStart()
        catat("onStart")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        catat("onRestoreInstanceState")
    }

    override fun onResume() {
        super.onResume()
        catat("onResume")
    }

    override fun onPause() {
        super.onPause()
        catat("onPause")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        catat("onSaveInstanceState")
    }

    override fun onStop() {
        super.onStop()
        catat("onStop")
    }

    override fun onRestart() {
        super.onRestart()
        catat("onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        catat("onDestroy (rotasi=$isChangingConfigurations, selesai=$isFinishing)")
    }

    companion object {
        const val TAG = "Lifecycle"
    }
}