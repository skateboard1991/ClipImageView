package com.skateboard.clipimageview

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_clipimage_activity.*
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream

class ClipImageActivity : AppCompatActivity()
{
    private var inputPath = ""

    private var outputPath = ""

    private var maxScale = 1.0f

    private var minScale = 1.0f

    private var clipWidth = 90

    private var clipHeight = 90

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clipimage_activity)
        toolbar.title = getString(R.string.clip_title)
        setSupportActionBar(toolbar)
        handleIntentData()
        initClipImageView()
    }

    private fun handleIntentData()
    {
        val bundle = intent.extras
        bundle?.let {
            inputPath = it.getString(INPUT_PATH)
            outputPath = it.getString(OUTPUT_PATH)
            maxScale = it.getFloat(MAX_SCALE)
            minScale = it.getFloat(MIN_SCALE)
            clipWidth = it.getInt(CLIP_WIDTH)
            clipHeight = it.getInt(CLIP_HEIGHT)
        }
    }

    private fun initClipImageView()
    {
        clipImageView.clipWidth = clipWidth
        clipImageView.clipHeight = clipHeight
        clipImageView.minScale = minScale
        clipImageView.maxScale = maxScale
        clipImageView.onsaveClipImageListener = object : ClipImageView.OnSaveClipImageListsner
        {
            override fun onImageFinishedSav()
            {
                hideLoadingProgress()
                val intent = Intent()
                intent.data = Uri.fromFile(File(outputPath))
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

        }
        clipImageView.post {
            decodeImage()
        }
    }

    private val loadImageTask = object : AsyncTask<Void, Void, Bitmap?>()
    {
        override fun onPreExecute()
        {
            super.onPreExecute()
            showLoadingProgress()
        }

        override fun doInBackground(vararg params: Void?): Bitmap?
        {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            val fd = contentResolver.openFileDescriptor(Uri.parse(inputPath), "r").fileDescriptor
            var bitmap: Bitmap? = null
            try
            {
                val fdIn = BufferedInputStream(FileInputStream(fd))
                fdIn.mark(fdIn.available())
                BitmapFactory.decodeStream(fdIn, null, options)
                val sampleSize = calculateInSampleSize(options, clipImageView.width, clipImageView.height)
                options.inSampleSize = sampleSize
                options.inJustDecodeBounds = false
                fdIn.reset()
                bitmap = BitmapFactory.decodeStream(fdIn, null, options)
                fdIn.close()
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
            return bitmap
        }

        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int
        {
            var width = options.outWidth
            var height = options.outHeight
            var inSampleSize = 1
            while (width > reqWidth || height > reqHeight)
            {
                inSampleSize *= 2
                width /= 2
                height /= 2
            }
            return inSampleSize
        }

        override fun onPostExecute(result: Bitmap?)
        {
            super.onPostExecute(result)
            hideLoadingProgress()
            clipImageView.setImageBitmap(result)
        }
    }

    private fun decodeImage()
    {
        loadImageTask.execute()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.clip_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        if (item?.itemId == R.id.use)
        {
            showLoadingProgress()
            clipImageView.clipAndSaveImage(outputPath)
        }
        return false
    }

    private fun showLoadingProgress()
    {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingProgress()
    {
        progressBar.visibility = View.GONE
    }

    companion object
    {
        val CLIP_WIDTH = "clip_width"
        val CLIP_HEIGHT = "clip_height"
        val MAX_SCALE = "max_scale"
        val MIN_SCALE = "min_scale"
        val INPUT_PATH = "input_path"
        val OUTPUT_PATH = "output_path"

        fun startCropImage(context: Activity, requestCode: Int, inputPath: String, outputPath: String, clipWidth: Int, clipHeight: Int, maxScale: Float = 1.0f, minScale: Float = 1.0f)
        {
            val intent = Intent(context, ClipImageActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(CLIP_WIDTH, clipWidth)
            bundle.putInt(CLIP_HEIGHT, clipHeight)
            bundle.putFloat(MAX_SCALE, maxScale)
            bundle.putFloat(MIN_SCALE, minScale)
            bundle.putString(INPUT_PATH, inputPath)
            bundle.putString(OUTPUT_PATH, outputPath)
            intent.putExtras(bundle)
            context.startActivityForResult(intent, requestCode)
        }
    }
}