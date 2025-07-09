package com.neverwintermoon.zoomimage_bug_92

import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil3.load
import com.github.panpf.zoomimage.CoilZoomImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : ComponentActivity() {
  private lateinit var imageView: CoilZoomImageView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    requestWindowFeature(Window.FEATURE_NO_TITLE)
    setContentView(R.layout.activity_main)

    imageView = findViewById(R.id.cstm_image)

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.CREATED) {
        val path = copyAssetToExternalStorage("huge panorama.jpg")

        imageView.load(path) {

        }
      }
    }
  }

  private suspend fun copyAssetToExternalStorage(
    assetFileName: String
  ) = withContext(Dispatchers.IO) {
    try {
      val externalFilesDir = getExternalFilesDir(null)
      val destinationFile = File(externalFilesDir, assetFileName)

      if (destinationFile.exists()) {
        return@withContext destinationFile.absolutePath
      }

      assets.open(assetFileName).use { inputStream ->
        destinationFile.outputStream().use { outputStream ->
          inputStream.copyTo(outputStream)
        }
      }

      return@withContext destinationFile.absolutePath
    } catch (e: Exception) {
      Log.e(TAG, "copyAssetToExternalStorage. Error copying file", e)
    }
  }

  companion object {
    const val TAG = "MainActivity"
  }
}
