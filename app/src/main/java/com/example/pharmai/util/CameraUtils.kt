// 📄 CameraUtils.kt
package com.example.pharmai.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object CameraUtils {

    sealed class ImageProcessingResult {
        data class Success(val data: ByteArray) : ImageProcessingResult()
        data class Error(val message: String, val exception: Exception? = null) : ImageProcessingResult()
    }

    fun bitmapToByteArray(bitmap: Bitmap): ImageProcessingResult {
        return try {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            ImageProcessingResult.Success(stream.toByteArray())
        } catch (e: Exception) {
            ImageProcessingResult.Error("Failed to compress image", e)
        }
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        return try {
            var width = bitmap.width
            var height = bitmap.height
            val bitmapRatio = width.toFloat() / height.toFloat()

            if (bitmapRatio > 1) {
                width = maxSize
                height = (width / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height * bitmapRatio).toInt()
            }

            Bitmap.createScaledBitmap(bitmap, width, height, true)
        } catch (e: Exception) {
            // Return original bitmap if resizing fails
            bitmap
        }
    }

    fun saveBitmapToCache(context: Context, bitmap: Bitmap, filename: String): File? {
        return try {
            val file = File(context.cacheDir, filename)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}